package com.idc.common.vertx.gate;

import com.idc.common.vertx.gate.client.NetVertxVerticle;
import com.idc.common.vertx.gate.common.RemoteAddress;
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
    private RemoteExchangeDelegate exchangeDelegate;

    @PostConstruct
    public void GateTest() throws Exception {
        log.info("TexchangeDelegate.initExchangeServer Demo start");
        RemoteAddress serverAddress = new RemoteAddress("127.0.0.1", 8082);
        exchangeDelegate.initExchangeServer(serverAddress);
        Thread.sleep(3000);
        exchangeDelegate.initExchangeClient(serverAddress);
        log.info("exchangeDelegate.initExchangeServer Demo end");
    }
}
