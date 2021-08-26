package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public class NettyChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannel.class);
    /**
     * the cache for channel
     */
    private static final ConcurrentMap<io.netty.channel.Channel, NettyChannel> CHANNEL_MAP = new ConcurrentHashMap<io.netty.channel.Channel, NettyChannel>();
    /**
     * netty channel
     */
    private final io.netty.channel.Channel channel;

    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();


    private NettyChannel(io.netty.channel.Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == null;");
        }
        this.channel = channel;
    }

    /**
     * Get channel by netty channel through channel cache.
     * Put netty channel into it if channel don't exist in the cache.
     *
     * @param ch       channel
     * @param url
     * @param handler handler that contain netty's handler
     * @return
     */
    public static NettyChannel getOrAddChannel(io.netty.channel.Channel ch) {
        if (ch == null) {
            return null;
        }
        NettyChannel ret = CHANNEL_MAP.get(ch);
        if (ret == null) {
            NettyChannel nettyChannel = new NettyChannel(ch);
            if (ch.isActive()) {
                ret = CHANNEL_MAP.putIfAbsent(ch, nettyChannel);
            }
            if (ret == null) {
                ret = nettyChannel;
            }
        }
        return ret;
    }

    /**
     * Remove the inactive channel.
     *
     * @param ch netty channel
     */
    public static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            CHANNEL_MAP.remove(ch);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public boolean isConnected() {
        return !isClosed() && channel.isActive();
    }

    @Override
    public void send(Object message) {
        // whether the channel is closed
        this.send(message, true);
    }

    /**
     * Send message by netty and whether to wait the completion of the send.
     *
     * @param message message that need send.
     * @param sent    whether to ack async-sent
     * @throws RpcException throw RpcException if wait until timeout or any exception thrown by method body that surrounded by try-catch.
     */
    @Override
    public void send(Object message, boolean sent) {
        // whether the channel is closed
        super.send(message, sent);

        boolean success = true;
        int timeout = 0;
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            if (sent) {
                // wait timeout ms
                timeout = 60 * 1000;
                success = future.await(timeout);
            }
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new RpcException("Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage(), e);
        }
        if (!success) {
            throw new RpcException("Failed to send message " + message + " to " + getRemoteAddress()
                    + "in timeout(" + timeout + "ms) limit");
        }
    }

    @Override
    public boolean isClosed() {
        return !channel.isOpen();
    }

    @Override
    public void close(int time) {

    }

    @Override
    public void close() {
        try {
//            super.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            removeChannelIfDisconnected(channel);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            attributes.clear();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Close netty channel " + channel);
            }
            channel.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        // The null value is unallowed in the ConcurrentHashMap.
        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NettyChannel other = (NettyChannel) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NettyChannel [channel=" + channel + "]";
    }
}
