package com.idc.common.vertx.tcpvertix;

import com.idc.common.util.VertxMsgUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
@Component
public class TcpClientVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String socketId;
    private NetSocket netSocket;

    @Override
    public void start() throws Exception {
        NetClientOptions options = new NetClientOptions();
        options.setTcpKeepAlive(true);
        options.setReconnectAttempts(10);
        options.setReconnectInterval(30000);
        NetClient client = vertx.createNetClient();

        final RecordParser parser = RecordParser.newDelimited("\n", h -> {
            String msg = h.toString();
            log.info("客户端解包:{} ", msg);
            String[] msgSplit = msg.split("\\*");

            String socketId1 = msgSplit[0];
            String body = msgSplit[1];

            if ("Server".equals(body)) {
                socketId = socketId1;
            }
        });

        client.connect(8081, "127.0.0.1", conn -> {
            if (conn.succeeded()) {
                log.info("client ok");
                netSocket = conn.result();
                netSocket.handler(parser::handle);
            } else {
                conn.cause().printStackTrace();
            }
        });

        // 客户端每 20 秒 发送一次心跳包
        vertx.setPeriodic(1000L * 20, t -> {
            if (netSocket != null)
                netSocket.write(VertxMsgUtils.joinMsg(socketId, "PING"));
        });

        // 模拟客户端发消息 每次10秒钟
        vertx.setPeriodic(3000L * 10, t -> {
            if (netSocket != null) {
                netSocket.write(VertxMsgUtils.joinMsg(socketId, "Hello Vert.x,this tcp message is from vertx client"));
            }
        });
    }

}
