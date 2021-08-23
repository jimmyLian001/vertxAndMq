package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;

import java.util.concurrent.CompletableFuture;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public interface ExchangeChannel extends Channel{


    /**
     * send request.
     *
     * @param request
     * @return response future
     * @throws RpcException
     */
    CompletableFuture<Object> request(Object request) throws RpcException;

    /**
     * send request.
     *
     * @param request
     * @param timeout
     * @return response future
     * @throws RpcException
     */
    CompletableFuture<Object> request(Object request, int timeout) throws RpcException;


    /**
     * graceful close.
     *
     * @param timeout
     */
    @Override
    void close(int timeout);
}
