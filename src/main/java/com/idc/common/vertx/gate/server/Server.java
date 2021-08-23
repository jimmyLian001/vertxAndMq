package com.idc.common.vertx.gate.server;

import com.idc.common.vertx.gate.exchage.Channel;
import com.idc.common.vertx.gate.exchage.EndPoint;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public interface Server extends EndPoint {

    /**
     * is bound.
     *
     * @return bound
     */
    boolean isBound();

    /**
     * get channels.
     *
     * @return channels
     */
    Collection<Channel> getChannels();

    /**
     * get channel.
     *
     * @param remoteAddress
     * @return channel
     */
    Channel getChannel(InetSocketAddress remoteAddress);

}
