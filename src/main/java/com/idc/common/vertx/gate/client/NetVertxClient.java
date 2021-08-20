package com.idc.common.vertx.gate.client;

import com.alibaba.fastjson.JSON;
import com.idc.common.util.VertxMsgUtils;
import com.idc.common.vertx.gate.common.VertxTcpMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
@Component
public class NetVertxClient extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String socketId;
    private NetSocket netSocket;
    private boolean connected = Boolean.FALSE;

    @Override
    public void start() throws Exception {
        NetClientOptions options = new NetClientOptions();
        options.setTcpKeepAlive(true);
        options.setReconnectAttempts(10);
        options.setReconnectInterval(30000);
        NetClient client = vertx.createNetClient();

        final RecordParser parser = RecordParser.newDelimited("idcEnd", h -> {
            String msg = h.toString();
            log.info("Net Vertx TCP client receive:{} ", msg);
            VertxTcpMessage response = JSON.parseObject(msg, VertxTcpMessage.class);

            if (response.getSide() == 0) {
                socketId = response.getSocketId();
            } else {
                // do anyThing
            }
        });

        client.connect(8082, "127.0.0.1", conn -> {
            if (conn.succeeded()) {
                log.info("client ok");
                netSocket = conn.result();
                connected = Boolean.TRUE;
                netSocket.handler(parser);
            } else {
                conn.cause().printStackTrace();
                log.error("Net Vertx TCP client connect to Server error:", conn.cause());
            }
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

    }

    public boolean isConnected() {
        return connected;
    }

}
