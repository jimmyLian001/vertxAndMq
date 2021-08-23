package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;

import java.util.concurrent.CompletableFuture;

/**
 * 描述：TCP socket 交互client
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public interface ExchangeClient extends ExchangeChannel{

    /**
     * reconnect.
     */
    void reconnect() throws RpcException;

}
