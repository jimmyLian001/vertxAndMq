package com.idc.common.vertx.gate;

import com.idc.common.po.AddressPo;
import com.idc.common.po.AppResponse;
import com.idc.common.po.RpcInvocation;
import com.idc.common.vertx.gate.client.NetVertxVerticle;
import com.idc.common.vertx.gate.common.RemoteAddress;
import com.idc.common.vertx.gate.common.Request;
import com.idc.common.vertx.gate.common.VertxRouter;
import com.idc.common.vertx.gate.exchage.ExchangeClient;
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
//        exchangeDelegate.initExchangeClient(serverAddress);
        log.info("exchangeDelegate.initExchangeServer Demo end");
        AddressPo addressPo = new AddressPo();
        addressPo.setName("zidan.lian");
        addressPo.setAddress("上海市浦东新区杨高南路759号陆家嘴世纪金融广场2号楼16楼");
        addressPo.setUserId("0790");
        addressPo.setTel("13127933306");
        Thread.sleep(15000);
        RpcInvocation invocation = new RpcInvocation();
        invocation.setInterfaceName("userAddressInfo");
        invocation.setResource("default");
        invocation.setMethodName("updateAddress");
        AppResponse appResponse = exchangeDelegate.request(addressPo, serverAddress, new VertxRouter("ConnectSvr", "BrokerSvr"), invocation);
        log.info("首个请求结果:{}", appResponse);
    }
}
