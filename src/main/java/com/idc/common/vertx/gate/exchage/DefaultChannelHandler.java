package com.idc.common.vertx.gate.exchage;

import com.idc.common.po.AppResponse;
import com.idc.common.po.Response;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.vertx.eventbuscluster.ClusteredVertxServer;
import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.common.DefaultFuture;
import com.idc.common.vertx.gate.common.Request;
import com.idc.common.vertx.gate.common.VertxTcpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public class DefaultChannelHandler implements ChannelHandler {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultChannelHandler.class);

    private final ChannelHandler handler;

    public ClusteredVertxServer getClusteredVertxServer() {
        return clusteredVertxServer;
    }

    @Override
    public void setClusteredVertxServer(ClusteredVertxServer clusteredVertxServer) {
        this.clusteredVertxServer = clusteredVertxServer;
    }

    private ClusteredVertxServer clusteredVertxServer;

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    private Channel channel;

    public DefaultChannelHandler() {
        this.handler = this;
        clusteredVertxServer = new ClusteredVertxServer();
    }

    public DefaultChannelHandler(ChannelHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        clusteredVertxServer = new ClusteredVertxServer();
        this.handler = handler;
    }

    static void handleResponse(Channel channel, Response response) throws RpcException {
        if (response != null && !response.isHeartbeat()) {
            DefaultFuture.received(channel, response);
        }
    }

    private static boolean isClientSide(Channel channel) {
        return true;
    }

    void handleRequest(final ExchangeChannel channel, Request req) throws RpcException {
        Response res = new Response(req.getId());
        // find handler by message class.
        try {
            VertxMessageReq vertxMessageReq = new VertxMessageReq();
            vertxMessageReq.setTimeStamp(System.currentTimeMillis());
            vertxMessageReq.setContent(req.getData());
            vertxMessageReq.setSide(1);
            vertxMessageReq.setInvocation(req.getInvocationRemote());
            AppResponse appResponse = clusteredVertxServer.sendMessageToEventBusSyn(req.getRouteDestination(), vertxMessageReq, 30 * 1000);
            res.setResult(appResponse);
            channel.send(res);
        } catch (Throwable e) {
            res.setStatus(Response.SERVICE_ERROR);
            res.setErrorMessage(e.getMessage());
            channel.send(res);
        }
    }

    @Override
    public void connected(Channel channel) throws RpcException {
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.connected(exchangeChannel);
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void disconnected(Channel channel) throws RpcException {
       /* channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());*/
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.disconnected(exchangeChannel);
        } finally {
            DefaultFuture.closeChannel(channel);
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void sent(Channel channel, Object message) throws RpcException {
        Throwable exception = null;
        try {
//            channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
            ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
            try {
                handler.sent(exchangeChannel, message);
            } finally {
                HeaderExchangeChannel.removeChannelIfDisconnected(channel);
            }
        } catch (Throwable t) {
            exception = t;
        }
        if (message instanceof Request) {
            Request request = (Request) message;
            DefaultFuture.sent(channel, request);
        }
        if (exception != null) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            } else if (exception instanceof RpcException) {
                throw (RpcException) exception;
            } else {
                throw new RpcException(exception);
            }
        }
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
//        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        final ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            if (message instanceof Request) {
                // handle request.
                Request request = (Request) message;
                handleRequest(exchangeChannel, request);
            } else if (message instanceof Response) {
                handleResponse(channel, (Response) message);
            } else if (message instanceof String) {
                Exception e = new Exception("Dubbo client can not supported string message: " + message + " in channel: " + channel + ", url: ");
                logger.error(e.getMessage(), e);
            } else {
                handler.received(exchangeChannel, message);
            }
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void receivedResult(Channel channel, Object message) throws RpcException {
//        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        final ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            if (message instanceof Response) {
                handleResponse(channel, (Response) message);
            } else if (message instanceof String) {
                Exception e = new Exception(" client can not supported string message: " + message + " in channel: " + channel + ", url: ");
                logger.error(e.getMessage(), e);
            } else {
                handler.received(exchangeChannel, message);
            }
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RpcException {
  /*      if (exception instanceof RpcException) {
            ExecutionException e = (ExecutionException) exception;
            Object msg = e.getRequest();
            if (msg instanceof Request) {
                Request req = (Request) msg;
                if (req.isTwoWay() && !req.isHeartbeat()) {
                    Response res = new Response(req.getId(), req.getVersion());
                    res.setStatus(Response.SERVER_ERROR);
                    res.setErrorMessage(StringUtils.toString(e));
                    channel.send(res);
                    return;
                }
            }
        }*/
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.caught(exchangeChannel, exception);
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    public ChannelHandler getHandler() {
        return handler;
    }
}
