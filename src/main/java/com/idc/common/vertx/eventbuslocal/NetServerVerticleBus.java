package com.idc.common.vertx.eventbuslocal;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * 描述：tcp 服务端
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
@Component
public class NetServerVerticleBus extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void start() throws Exception {
        // 唤起事件总线，注册一个事件处理者，或者直译叫事件消费者
        vertx.eventBus().consumer("GET_HELLO_MSG_SERVICE_ADDRESS1").handler(msg -> {
            // 获取事件内容后，调用service服务
            log.info("bus msg body is:{}", msg.body());
            // 将service返回的字符串，回应给消息返回体
            msg.reply(msg.body() + UUID.randomUUID().toString());
        });
    }


}
