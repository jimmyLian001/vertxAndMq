package com.idc.common.vertx.tcpvertix;

import com.idc.common.util.VertxMsgUtils;
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
public class NetServerVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final Map<String, NetSocket> SOCKET_MAP = new HashMap<>();
    private static final Map<String, Long> ACTIVE_SOCKET_MAP = new HashMap<>();


    @Override
    public void start() throws Exception {
        NetServerOptions options = new NetServerOptions();
        options.setTcpKeepAlive(true);

        final RecordParser parser = RecordParser.newDelimited("\n", h -> {
            final String msg = h.toString();
            log.info("服务器解包:{}", msg);

            final String[] msgSplit = msg.split("\\*");
            final String socketId = msgSplit[0];
            final String msgBody = msgSplit[1];

            if ("PING".equals(msgBody)) { // 心跳
                ACTIVE_SOCKET_MAP.put(socketId, System.currentTimeMillis());
                SOCKET_MAP.get(socketId).write(VertxMsgUtils.joinMsg(socketId, "PING"));
            } else {
                // 其他信息，这里简单模拟一下，原样返回给客户端
                SOCKET_MAP.get(socketId).write(VertxMsgUtils.joinMsg(socketId, msgBody));
                log.info("收到客户端消息socketId：{}，msgBody：{}", socketId, msgBody);
            }
        });

        NetServer server = vertx.createNetServer(options);

        ReadStream<NetSocket> stream = server.connectStream();
        stream.handler(netSocket -> {
            String socketId = netSocket.writeHandlerID();
            log.debug("New socket Id:{} ", socketId);
            netSocket.handler(parser::handle);
            netSocket.write(socketId + "*" + "Server\n");

            SOCKET_MAP.put(socketId, netSocket);
            ACTIVE_SOCKET_MAP.put(socketId, System.currentTimeMillis());
        });

        stream.endHandler(end -> {
            log.info("stream end");
        });

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

        server.listen(8081, res -> {
            if (res.succeeded()) {
                log.info("Server start !!!");
            } else {
                res.cause().printStackTrace();
            }
        });
    }


}
