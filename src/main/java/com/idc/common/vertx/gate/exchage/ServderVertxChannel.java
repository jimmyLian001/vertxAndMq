package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.server.ServerVertxVerticle;
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
public class ServderVertxChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(ServderVertxChannel.class);
    /**
     * the cache for netty channel and dubbo channel
     */
    private static final ConcurrentMap<ServerVertxVerticle, ServderVertxChannel> CHANNEL_MAP = new ConcurrentHashMap<ServerVertxVerticle, ServderVertxChannel>();
    /**
     * netty channel
     */
    private final ServerVertxVerticle channel;

    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();


    private ServderVertxChannel(ServerVertxVerticle channel) {
        if (channel == null) {
            throw new IllegalArgumentException("netty channel == null;");
        }
        this.channel = channel;
    }

    /**
     * Get dubbo channel by netty channel through channel cache.
     * Put netty channel into it if dubbo channel don't exist in the cache.
     *
     * @param ch netty channel
     * @return
     */
    public static ServderVertxChannel getOrAddChannel(ServerVertxVerticle ch) {
        if (ch == null) {
            return null;
        }
        ServderVertxChannel ret = CHANNEL_MAP.get(ch);
        if (ret == null) {
            ServderVertxChannel vertxChannel = new ServderVertxChannel(ch);
            if (ch.isConnected()) {
                ret = CHANNEL_MAP.putIfAbsent(ch, vertxChannel);
            }
            if (ret == null) {
                ret = vertxChannel;
            }
        }
        return ret;
    }

    /**
     * Remove the inactive channel.
     *
     * @param ch netty channel
     */
    public static void removeChannelIfDisconnected(ServerVertxVerticle ch) {
        if (ch != null && !ch.isConnected()) {
            CHANNEL_MAP.remove(ch);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        InetSocketAddress socketAddress = new InetSocketAddress(channel.getServer().actualPort());
        return socketAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        //TODO
        return null;

    }

    @Override
    public boolean isConnected() {
        return !isClosed() && channel.isConnected();
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
            //TODO
            channel.send(message);
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
        return !channel.isConnected();
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
            channel.getServer().close();
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
        ServderVertxChannel other = (ServderVertxChannel) obj;
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
        return "VertxChannel [channel=" + channel + "]";
    }
}
