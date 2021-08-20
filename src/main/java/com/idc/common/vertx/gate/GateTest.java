package com.idc.common.vertx.gate;

import com.idc.common.vertx.gate.client.NetVertxClient;
import com.idc.common.vertx.gate.server.NetVertxServer;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/20 ProjectName: vertxAndMq
 */
@Component
public class GateTest {
    private static final Logger log = LoggerFactory.getLogger(GateTest.class);

    @Autowired
    private NetVertxServer netVertxServer;
    @Autowired
    private NetVertxClient netVertxClient;

    @PostConstruct
    public  void GateTest() throws Exception{
        log.info("Tcp Verticle Demo start");
        Vertx.vertx().deployVerticle(netVertxServer);
        Thread.sleep(2000L);
        Vertx.vertx().deployVerticle(netVertxClient);
        log.info("Tcp Verticle Demo end");
    }
}
