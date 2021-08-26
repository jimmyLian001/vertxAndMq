package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.server.Server;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public interface ExchangeServer extends Server {

    /**
     * get channels.
     *
     * @return channels
     */
    Collection<ExchangeChannel> getExchangeChannels();

    /**
     * get channel.
     *
     * @param remoteAddress
     * @return channel
     */
    ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress);

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
}
