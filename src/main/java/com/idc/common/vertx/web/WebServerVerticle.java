package com.idc.common.vertx.web;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 描述：springVerticle作为事件总线中的后台处理程序，接收事件总线消息，并调用springService完成服务处理。
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/13 ProjectName: vertxAndKafka
 */
public class WebServerVerticle extends AbstractVerticle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private SpringService springService;

    public static final String GET_HELLO_MSG_SERVICE_ADDRESS = "get_hello_msg_service";

    public WebServerVerticle(ApplicationContext ctx) {
        this.springService = (SpringService) ctx.getBean("springService");
    }

    @Override
    public void start() throws Exception {
        // 唤起事件总线，注册一个事件处理者，或者直译叫事件消费者
        vertx.eventBus().consumer(GET_HELLO_MSG_SERVICE_ADDRESS).handler(msg -> {
            // 获取事件内容后，调用service服务
            log.info("bus msg body is:{}", msg.body());
            String helloMsg = springService.getHello();
            if (!msg.body().equals("event bus calls spring service")) {
                helloMsg = helloMsg + "2";
            }
            log.info("msg from hello service is:{}", helloMsg);
            // 将service返回的字符串，回应给消息返回体
            msg.reply(helloMsg);
        });
    }
}
