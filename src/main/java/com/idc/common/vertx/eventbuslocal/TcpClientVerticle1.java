package com.idc.common.vertx.eventbuslocal;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
public class TcpClientVerticle1 extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start() throws Exception {
        vertx.eventBus().<String>send(
                // 消息地址
                "GET_HELLO_MSG_SERVICE_ADDRESS1",
                // 消息内容
                "event bus calls TcpClientVerticle1",
                result -> {
                    // 异步结果处理
                    if (result.succeeded()) {
                        // 成功的话，返回处理结果给前台，这里的处理结果就是service返回的一段字符串
                        log.info("eventBus receive msg:{}", result.result().body());
                    } else {
                        log.info("eventBus receive msg:{}", result.cause());
                    }
                }
        );
    }

}
