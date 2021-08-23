package com.idc.common.vertx.gate;

import com.idc.common.po.AppResponse;
import com.idc.common.po.Response;
import com.idc.common.po.RpcInvocation;
import com.idc.common.vertx.gate.common.RemoteAddress;
import com.idc.common.vertx.gate.common.Request;
import com.idc.common.vertx.gate.common.VertxRouter;
import com.idc.common.vertx.gate.exchage.ExchangeClient;
import com.idc.common.vertx.gate.exchage.ExchangeServer;
import com.idc.common.vertx.gate.exchage.HeadExchanger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
@Component
public class RemoteExchangeDelegate {

    private Map<RemoteAddress, ExchangeClient> clientMap = new ConcurrentHashMap<>();
    private ExchangeServer exchangeServer = null;
    private HeadExchanger headExchanger = new HeadExchanger();

    public ExchangeClient initExchangeClient(RemoteAddress address) {
        ExchangeClient exchangeClient = clientMap.get(address);
        if (exchangeClient == null) {
            synchronized (this) {
                exchangeClient = clientMap.get(address);
                if (exchangeClient == null) {
                    clientMap.put(address, createClient(address));
                }
            }
        }
        return exchangeClient;
    }


    public ExchangeServer initExchangeServer(RemoteAddress address) {
        if (exchangeServer == null) {
            synchronized (this) {
                if (exchangeServer == null) {
                    exchangeServer = createServer(address);
                }
            }
        }
        return exchangeServer;
    }

    private ExchangeClient createClient(RemoteAddress address) {
        return headExchanger.connect(address);
    }

    private ExchangeServer createServer(RemoteAddress address) {
        return headExchanger.bound(address);
    }


    public AppResponse request(Object object, RemoteAddress address, VertxRouter vertxRouter, RpcInvocation invocation) {
        AppResponse appResponse = new AppResponse();
        appResponse.setRouteOrigin(vertxRouter.getRouteOrigin());
        appResponse.setRouteDestination(vertxRouter.getRouteDestination());
        try {
            ExchangeClient exchangeClient = clientMap.get(address);
            if (exchangeClient != null) {
                Request request = new Request();
                request.setData(object);
                request.setRouteDestination(vertxRouter.getRouteDestination());
                request.setRouteOrigin(vertxRouter.getRouteOrigin());
//                request.setI
                CompletableFuture<Object> requestFuture = exchangeClient.request(request);
                Response response = (Response) requestFuture.get();
                appResponse.setStatus(response.getStatus());
                appResponse.setValue(response.getResult());
                appResponse.setErrorMessage(response.getErrorMessage());
            } else {
                appResponse.setStatus(Response.CHANNEL_INACTIVE);
                appResponse.setErrorMessage("remote channel has not init");
            }
        } catch (Exception e) {
            appResponse.setStatus(Response.BAD_RESPONSE);
            appResponse.setException(e);
            appResponse.setErrorMessage(e.getMessage());
        }
        return appResponse;
    }


}
