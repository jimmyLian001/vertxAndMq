package com.idc.common.vertx.gate.server;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.common.RemoteAddress;
import com.idc.common.vertx.gate.exchage.Channel;
import com.idc.common.vertx.gate.exchage.ChannelHandler;
import com.idc.common.vertx.gate.exchage.ServderVertxChannel;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
public class NetVertxServer extends AbstractServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String socketId;
    private NetClient client;
    private boolean connected = Boolean.FALSE;
    private ServerVertxVerticle netVertxVerticle;
    private static Map<String, Channel> channels = new ConcurrentHashMap<>();


    public NetVertxServer(ChannelHandler handler, RemoteAddress remoteAddress) throws RpcException {
        super(handler, remoteAddress);
    }


    /**
     * Init bootstrap
     *
     * @throws Throwable
     */
    @Override
    protected void doOpen() throws Throwable {
        netVertxVerticle = new ServerVertxVerticle();
        Vertx.vertx().deployVerticle(netVertxVerticle);
        netVertxVerticle.doOpen(8082);
        netVertxVerticle.connect();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8082);
        channelHandler.setChannel(this.getChannel(inetSocketAddress));
        netVertxVerticle.setChannelHandler(channelHandler);
        Channel channel = this.getChannel(inetSocketAddress);
        channels.put("8082", channel);
        channelHandler.setChannel(channel);
    }

    @Override
    protected void doClose() throws Throwable {
        channels.remove(String.valueOf(netVertxVerticle.getServer().actualPort()));
        netVertxVerticle.getServer().close();
    }


    /**
     * is bound.
     *
     * @return bound
     */
    @Override
    public boolean isBound() {
        return false;
    }

    /**
     * get channels.
     *
     * @return channels
     */
    @Override
    public Collection<Channel> getChannels() {
        return null;
    }

    /**
     * get channel.
     *
     * @param remoteAddress
     * @return channel
     */
    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        ServerVertxVerticle c = netVertxVerticle;
        if (c == null || !c.isConnected()) {
            return null;
        }
        return ServderVertxChannel.getOrAddChannel(c);
    }

    @Override
    public void send(Object message) {

    }

    /**
     * is closed.
     *
     * @return closed
     */
    @Override
    public boolean isClosed() {
        return false;
    }
}
