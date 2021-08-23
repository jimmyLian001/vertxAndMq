package com.idc.common.vertx.gate.client;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.exchage.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public abstract class AbstractClient implements Client {

    protected static final String CLIENT_THREAD_POOL_NAME = "DubboClientHandler";
    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);
    private final Lock connectLock = new ReentrantLock();
    private final boolean needReconnect;
    protected volatile ExecutorService executor;

    public AbstractClient() throws RpcException {
        needReconnect = true;

        try {
            doOpen();
        } catch (Throwable t) {
            close();
            throw new RpcException("Failed to start " + getClass().getSimpleName() + " connect to the server " +
                    getRemoteAddress() + ", cause: " + t.getMessage(), t);
        }
        try {
            // connect.
            connect();
            if (logger.isInfoEnabled()) {
                logger.info("Start " + getClass().getSimpleName() + " connect to the server " + getRemoteAddress());
            }
        } catch (RpcException t) {

            logger.warn("Failed to start " + getClass().getSimpleName() + " "
                    + " connect to the server " + getRemoteAddress() + " (check == false, ignore and retry later!), cause: " + t.getMessage(), t);
        } catch (Throwable t) {
            close();
            throw new RpcException(
                    "Failed to start " + getClass().getSimpleName() + " "
                            + " connect to the server " + getRemoteAddress() + ", cause: " + t.getMessage(), t);
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        Channel channel = getChannel();
        if (channel == null) {
//            return getUrl().toInetSocketAddress();
        }
        return channel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        Channel channel = getChannel();
        if (channel == null) {
//            return InetSocketAddress.createUnresolved(NetUtils.getLocalHost(), 0);
        }
        return channel.getLocalAddress();
    }

    @Override
    public boolean isConnected() {
        Channel channel = getChannel();
        if (channel == null) {
            return false;
        }
        return channel.isConnected();
    }

    @Override
    public Object getAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null) {
            return null;
        }
        return channel.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        Channel channel = getChannel();
        if (channel == null) {
            return;
        }
        channel.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null) {
            return;
        }
        channel.removeAttribute(key);
    }

    @Override
    public boolean hasAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null) {
            return false;
        }
        return channel.hasAttribute(key);
    }

    @Override
    public void send(Object message) throws RpcException {
        send(message, true);
    }

    @Override
    public void send(Object message, boolean sent) throws RpcException {
        if (needReconnect && !isConnected()) {
            connect();
        }
        Channel channel = getChannel();
        //TODO Can the value returned by getChannel() be null? need improvement.
        if (channel == null || !channel.isConnected()) {
            throw new RpcException("message can not send, because channel is closed .");
        }
        channel.send(message, sent);
    }

    protected void connect() throws RpcException {

        connectLock.lock();

        try {

            if (isConnected()) {
                return;
            }

            doConnect();

            if (!isConnected()) {
                throw new RpcException("Failed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName());

            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Succeed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + " "
                            + ", channel is " + this.getChannel());
                }
            }

        } catch (RpcException e) {
            throw e;

        } catch (Throwable e) {
            throw new RpcException("Failed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + " "
                    + ", cause: " + e.getMessage(), e);

        } finally {
            connectLock.unlock();
        }
    }

    public void disconnect() {
        connectLock.lock();
        try {
            try {
                Channel channel = getChannel();
                if (channel != null) {
                    channel.close();
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
            try {
                doDisConnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        } finally {
            connectLock.unlock();
        }
    }

    @Override
    public void reconnect() throws RpcException {
        if (!isConnected()) {
            connectLock.lock();
            try {
                if (!isConnected()) {
                    disconnect();
                    connect();
                }
            } finally {
                connectLock.unlock();
            }
        }
    }

    @Override
    public boolean isClosed(){
        //TODO
        return false;
    }

    @Override
    public void close() {

        try {
//            super.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            if (executor != null) {
//                ExecutorUtil.shutdownNow(executor, 100);
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            disconnect();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            doClose();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public void close(int timeout) {
//        ExecutorUtil.gracefulShutdown(executor, timeout);
        close();
    }

    @Override
    public String toString() {
        return getClass().getName() + " [" + getLocalAddress() + " -> " + getRemoteAddress() + "]";
    }

    /**
     * Open client.
     *
     * @throws Throwable
     */
    protected abstract void doOpen() throws Throwable;

    /**
     * Close client.
     *
     * @throws Throwable
     */
    protected abstract void doClose() throws Throwable;

    /**
     * Connect to server.
     *
     * @throws Throwable
     */
    protected abstract void doConnect() throws Throwable;

    /**
     * disConnect to server.
     *
     * @throws Throwable
     */
    protected abstract void doDisConnect() throws Throwable;

    /**
     * Get the connected channel.
     *
     * @return channel
     */
    protected abstract Channel getChannel();
}
