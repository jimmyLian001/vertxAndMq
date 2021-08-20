package com.idc.common.vertx.tcpvertix;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 描述：Tcp Verticle 测试类
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/13 ProjectName: vertxAndKafka
 */
@Component
public class TcpVerticleDemo {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NetServerVerticle netServerVerticle;

    @Autowired
    private TcpClientVerticle tcpClientVerticle;

//    @PostConstruct
    public void start() throws InterruptedException {
        log.info("Tcp Verticle Demo start");
        Vertx.vertx().deployVerticle(netServerVerticle);
        Thread.sleep(1000L);
        Vertx.vertx().deployVerticle(tcpClientVerticle);
        log.info("Tcp Verticle Demo end");
    }
}
