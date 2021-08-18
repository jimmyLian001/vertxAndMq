package com.idc.common.vertx.eventbuslocal;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 描述：Tcp Verticle event bus 访问本地实例 测试类
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/13 ProjectName: vertxAndKafka
 */
@Component
public class TcpVerticleBusDemo {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

//    @PostConstruct
    public void start() {
        try {
            Vertx vertx = Vertx.vertx();
            System.out.println("Tcp Verticle Demo start");
            vertx.deployVerticle(new NetServerVerticleBus());
            Thread.sleep(1000L);
            vertx.deployVerticle(new TcpClientVerticle1());
            System.out.println("Tcp Verticle Demo end1");
            Thread.sleep(500);
            vertx.deployVerticle(new TcpClientVerticle2());
            System.out.println("Tcp Verticle Demo end2");
        } catch (Exception e) {
            log.error("Vertx.clusteredVertx error:", e);
        }
    }
}

