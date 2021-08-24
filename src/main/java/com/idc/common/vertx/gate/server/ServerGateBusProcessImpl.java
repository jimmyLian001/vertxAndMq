package com.idc.common.vertx.gate.server;

import com.idc.common.annotation.VertxUrl;
import com.idc.common.po.RpcInvocation;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.vertx.gate.RemoteExchangeDelegate;
import com.idc.common.vertx.gate.common.RemoteAddress;
import com.idc.common.vertx.gate.common.VertxRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
@Service
@VertxUrl(interfaceName = "ServerGateBusTransfer")
public class ServerGateBusProcessImpl implements ServerGateBusProcess {

    @Autowired
    private RemoteExchangeDelegate exchangeDelegate;

    @Override
    public void transfer(VertxMessageReq params) {
        //TODO loadBalance
        exchangeDelegate.request(params.getContent(), new RemoteAddress("127.0.0.1", 8082),
                new VertxRouter(params.getRouteOrigin(), params.getRouteDestination()), params.getInvocationRemote());
    }
}
