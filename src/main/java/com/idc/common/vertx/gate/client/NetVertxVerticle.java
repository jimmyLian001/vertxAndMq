package com.idc.common.vertx.gate.client;

import com.alibaba.fastjson.JSON;
import com.idc.common.po.Response;
import com.idc.common.util.VertxMsgUtils;
import com.idc.common.vertx.gate.common.Request;
import com.idc.common.vertx.gate.common.VertxTcpMessage;
import com.idc.common.vertx.gate.exchage.ChannelHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
public class NetVertxVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String socketId;
    private Vertx vertx;
    private int port;
    private String host;
    private NetSocket netSocket;
    private boolean connected = Boolean.FALSE;
    private long recentHeartBeatTimestamp = 0;
    private AtomicBoolean isConnecting = new AtomicBoolean(false);

    public void setChannelHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    private ChannelHandler channelHandler;

    public NetVertxVerticle(Vertx vertx) {
        this.vertx = vertx;
    }

    public NetSocket getNetSocket() {
        return netSocket;
    }


    @Override
    public void start() throws Exception {
        NetClientOptions options = new NetClientOptions();
        options.setTcpKeepAlive(true);
        options.setReconnectAttempts(10);
        options.setReconnectInterval(30000);
    }

    public NetClient connect(int port, String host) {
        NetClient client = vertx.createNetClient();
        this.host = host;
        this.port = port;
        final RecordParser parser = RecordParser.newDelimited("idcEnd", h -> {
            String msg = h.toString();
            VertxTcpMessage response = JSON.parseObject(msg, VertxTcpMessage.class);

            if (response.getSide() == 0) {
                socketId = response.getSocketId();
                log.info("Net Vertx TCP client connect success:{} ", msg);
            } else {
                if (response.isHeartBeat()) {
                    recentHeartBeatTimestamp = System.currentTimeMillis();
                    log.info("vertx client receive heartBeat");
                } else {
                    log.info("Net Vertx TCP client receive:{} ", msg);
                    received(response);
                }
            }
        });
        // 防止系统还未连接成功造成的获取状态失败
        connected = Boolean.TRUE;
        client.connect(port, host, conn -> {
            if (conn.succeeded()) {
                log.info("client ok");
                netSocket = conn.result();
                connected = Boolean.TRUE;
                netSocket.handler(parser);
            } else {
                connected = Boolean.FALSE;
                log.error("Net Vertx TCP client connect to Server error:", conn.cause());
            }
            isConnecting.compareAndSet(true, false);
        });
        VertxTcpMessage vertxTcpMessage = new VertxTcpMessage();
        vertxTcpMessage.setHeartBeat(Boolean.TRUE);
        vertxTcpMessage.setSide(1);
        // 客户端每 30 秒 发送一次心跳包
        vertx.setPeriodic(1000L * 20, t -> {
            if (netSocket != null) {
                vertxTcpMessage.setTimeStamp(System.currentTimeMillis());
                vertxTcpMessage.setMessageId(UUID.randomUUID().toString());
                vertxTcpMessage.setSocketId(socketId);
                netSocket.write(VertxMsgUtils.joinMsg(vertxTcpMessage));
            }
        });

        // 客户端每 30秒检测是否有心跳然后重连
        vertx.setPeriodic(1000L * 30, t -> {
            reconnect();
        });
        return client;
    }


    private void checkBeforeConnect() {
        if (netSocket != null) {
            netSocket.close();
        }
    }


    synchronized void reconnect() {
        if (System.currentTimeMillis() - recentHeartBeatTimestamp > 30 * 1000 && !isConnecting.get()) {
            isConnecting.compareAndSet(false, true);
            log.warn("verxtx tcp client lost connection,try reconnecting...");
            connected = Boolean.FALSE;
            checkBeforeConnect();
            connect(this.port, this.host);
        }
    }


    /**
     * 当前通道接收到消息
     *
     * @param message 消息内容
     * @return response
     */
    public Object received(VertxTcpMessage message) {
        if(message.getMessageType() == 1){
            Request request = new Request(Long.parseLong(message.getMessageId()));
            request.setData(message.getContent());
            request.setInvocationRemote(message.getInvocation());
            request.setRouteOrigin(message.getRouteOrigin());
            request.setRouteDestination(message.getRouteDestination());
            channelHandler.received(channelHandler.getChannel(), request);
            return request;
        }else{
            Response response = new Response(Long.parseLong(message.getMessageId()));
            response.setResult(message.getContent());
            response.setRouteOrigin(message.getRouteOrigin());
            response.setRouteDestination(message.getRouteDestination());
            channelHandler.received(channelHandler.getChannel(), response);
            return response;
        }
    }

    /**
     * 通过当前连接通道发送消息
     *
     * @param object 消息内容
     */
    public void send(Object object) {
        Request req = (Request) object;
        VertxTcpMessage vertxTcpMessage = new VertxTcpMessage();
        vertxTcpMessage.setHeartBeat(Boolean.FALSE);
        vertxTcpMessage.setSide(1);
        vertxTcpMessage.setTimeStamp(System.currentTimeMillis());
        vertxTcpMessage.setMessageId(String.valueOf(req.getId()));
        vertxTcpMessage.setSocketId(socketId);
        vertxTcpMessage.setContent(req.getData());
        vertxTcpMessage.setRouteOrigin(req.getRouteOrigin());
        vertxTcpMessage.setRouteDestination(req.getRouteDestination());
        vertxTcpMessage.setInvocation(req.getInvocation());
        netSocket.write(VertxMsgUtils.joinMsg(vertxTcpMessage));
    }

    public boolean isConnected() {
        return connected;
    }

}
