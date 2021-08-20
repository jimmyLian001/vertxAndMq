package com.idc.common.vertx.gate.server;

import com.alibaba.fastjson.JSON;
import com.idc.common.util.VertxMsgUtils;
import com.idc.common.vertx.gate.common.VertxTcpMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.core.streams.ReadStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 描述：tcp 服务端
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
@Component
public class NetVertxServer extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Map<String, NetSocket> SOCKET_MAP = new HashMap<>();
    private static final Map<String, Long> ACTIVE_SOCKET_MAP = new HashMap<>();


    @Override
    public void start() throws Exception {
        NetServerOptions options = new NetServerOptions();
        options.setTcpKeepAlive(true);
        //idcEnd消息结束标识
        final RecordParser parser = RecordParser.newDelimited("idcEnd", h -> {
            VertxTcpMessage vertxTcpMessage = JSON.parseObject(h.toString(), VertxTcpMessage.class);
            log.info("Net Vertx TCP Server receive:{}", vertxTcpMessage);
            final String socketId = vertxTcpMessage.getSocketId();
            // 心跳
            if (vertxTcpMessage.isHeartBeat()) {
                long timeStamp = System.currentTimeMillis();
                ACTIVE_SOCKET_MAP.put(socketId, timeStamp);
                vertxTcpMessage.setTimeStamp(timeStamp);
                vertxTcpMessage.setHeartBeat(Boolean.TRUE);
                vertxTcpMessage.setSide(2);
                SOCKET_MAP.get(socketId).write(VertxMsgUtils.joinMsg(vertxTcpMessage));
            } else {
                // 其他信息，这里简单模拟一下，原样返回给客户端
                SOCKET_MAP.get(socketId).write(VertxMsgUtils.joinMsg(vertxTcpMessage));
                log.info("收到客户端消息socketId：{}，msgBody：{}", socketId, "msgBody mock");
            }
        });

        NetServer server = vertx.createNetServer(options);

        ReadStream<NetSocket> stream = server.connectStream();
        //接收消息
        stream.handler(netSocket -> {
            String socketId = netSocket.writeHandlerID();
            log.debug("New socket Id:{} ", socketId);
            netSocket.handler(parser);
            VertxTcpMessage vertxTcpMessageCon = new VertxTcpMessage();
            vertxTcpMessageCon.setHeartBeat(Boolean.FALSE);
            vertxTcpMessageCon.setSide(0);
            vertxTcpMessageCon.setSocketId(socketId);
            netSocket.write(VertxMsgUtils.joinMsg(vertxTcpMessageCon));
            SOCKET_MAP.put(socketId, netSocket);
            ACTIVE_SOCKET_MAP.put(socketId, System.currentTimeMillis());
        });
        //结束拦截
        stream.endHandler(end -> {
            log.info("stream end");
        });
        //异常拦截
        stream.exceptionHandler(ex -> {
            log.info("stream ex :{} ", ex.getMessage());
            ex.printStackTrace();
        });

        // 检查心跳
        vertx.setPeriodic(1000L * 60, t -> {
            log.info("SOCKET MAP");
            log.info(SOCKET_MAP.toString());
            log.info("ACTIVE MAP");
            log.info(ACTIVE_SOCKET_MAP.toString());

            Iterator<Map.Entry<String, Long>> iterator = ACTIVE_SOCKET_MAP.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Long> entry = iterator.next();
                long time = System.currentTimeMillis() - entry.getValue();
                if (time > (1000 * 60)) {
                    log.debug("SocketId: {}, 被清除", entry.getKey());
                    SOCKET_MAP.remove(entry.getKey()).close();
                    iterator.remove();
                }
            }
        });

        //开启TCP端口监听
        server.listen(8082, res -> {
            if (res.succeeded()) {
                log.info("Server start !!!");
            } else {
                res.cause().printStackTrace();
            }
        });
    }


}
