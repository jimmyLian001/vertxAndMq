package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.ClusteredVertxServer;
import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public interface ChannelHandler {

    /**
     * on channel connected.
     *
     * @param channel channel.
     */
    void connected(Channel channel) throws RpcException;

    /**
     * on channel disconnected.
     *
     * @param channel channel.
     */
    void disconnected(Channel channel) throws RpcException;

    /**
     * on message sent.
     *
     * @param channel channel.
     * @param message message.
     */
    void sent(Channel channel, Object message) throws RpcException;

    /**
     * on message received.
     *
     * @param channel channel.
     * @param message message.
     */
    void received(Channel channel, Object message) throws RpcException;

    /**
     * on exception caught.
     *
     * @param channel   channel.
     * @param exception exception.
     */
    void caught(Channel channel, Throwable exception) throws RpcException;

    Channel getChannel();

    void setChannel(Channel channel);

    void setClusteredVertxServer(ClusteredVertxServer clusteredVertxServer);

}
