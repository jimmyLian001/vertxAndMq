package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.common.DefaultFuture;
import com.idc.common.vertx.gate.common.Request;
import com.idc.common.vertx.gate.server.Server;
import com.idc.common.vertx.gate.timer.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public class HeaderExchangeServer implements ExchangeServer {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Server server;
    private AtomicBoolean closed = new AtomicBoolean(false);

    private static final HashedWheelTimer IDLE_CHECK_TIMER = new HashedWheelTimer(1,
            TimeUnit.SECONDS, 128);


    public HeaderExchangeServer(Server server) {
        Assert.notNull(server, "server == null");
        this.server = server;
//        startIdleCheckTask(getUrl());
    }

    public Server getServer() {
        return server;
    }

    @Override
    public boolean isClosed() {
        return server.isClosed();
    }

    private boolean isRunning() {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {

            /**
             *  If there are any client connections,
             *  our server should be running.
             */

            if (channel.isConnected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() {
        doClose();
        server.close();
    }

    @Override
    public void close(final int timeout) {
//        startClose();
        if (timeout > 0) {
            final long max = (long) timeout;
            final long start = System.currentTimeMillis();
            while (HeaderExchangeServer.this.isRunning()
                    && System.currentTimeMillis() - start < max) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        doClose();
        server.close(timeout);
    }

    private void doClose() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        cancelCloseTask();
    }

    private void cancelCloseTask() {
     /*   if (closeTimerTask != null) {
            closeTimerTask.cancel();
        }*/
    }

    @Override
    public Collection<ExchangeChannel> getExchangeChannels() {
        Collection<ExchangeChannel> exchangeChannels = new ArrayList<ExchangeChannel>();
        Collection<Channel> channels = server.getChannels();
        if (!CollectionUtils.isEmpty(channels)) {
            for (Channel channel : channels) {
                exchangeChannels.add(HeaderExchangeChannel.getOrAddChannel(channel));
            }
        }
        return exchangeChannels;
    }

    @Override
    public ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress) {
        Channel channel = server.getChannel(remoteAddress);
        return HeaderExchangeChannel.getOrAddChannel(channel);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collection<Channel> getChannels() {
        return (Collection) getExchangeChannels();
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return getExchangeChannel(remoteAddress);
    }

    @Override
    public boolean isBound() {
        return server.isBound();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return server.getLocalAddress();
    }

    @Override
    public void send(Object message) throws RpcException {
        if (closed.get()) {
            throw new RpcException("Failed to send message " + message
                    + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        server.send(message);
    }

    @Override
    public void send(Object message, boolean sent) throws RpcException {
        if (closed.get()) {
            throw new RpcException("Failed to send message " + message
                    + ", cause: The server " + getLocalAddress() + " is closed!");
        }
        server.send(message, sent);
    }

    @Override
    public CompletableFuture<Object> request(Object request) throws RpcException {
        return request(request, 60 * 1000);
    }

    @Override
    public CompletableFuture<Object> request(Object request, int timeout) throws RpcException {
        if (closed.get()) {
            throw new RpcException("Failed to send request " + request + ", cause: The channel " + this + " is closed!");
        }
        // create request.
        Request req = (Request) request;
        Iterator<Channel> iterator = this.getChannels().iterator();
        if (!iterator.hasNext()) {
            throw new RpcException("Failed to send request " + ", cause: The channel " + this + " is closed!");
        }

        Channel channel = iterator.next();
        DefaultFuture future = DefaultFuture.newFuture(channel, req, timeout);
        try {
            channel.send(req);
        } catch (RpcException e) {
            future.cancel();
            throw e;
        }
        return future;

    }
}
