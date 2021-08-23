package com.idc.common.vertx.gate.client;

import com.idc.common.vertx.gate.exchage.Channel;
import com.idc.common.vertx.gate.exchage.ChannelHandler;
import com.idc.common.vertx.gate.exchage.DefaultChannelHandler;
import com.idc.common.vertx.gate.exchage.VertxChannel;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
public class NetVertxClient extends AbstractClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String socketId;
    private NetClient client;
    private boolean connected = Boolean.FALSE;
    private NetVertxVerticle netVertxVerticle;
    private ChannelHandler channelHandler;

    public NetVertxClient(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    /**
     * Init bootstrap
     *
     * @throws Throwable
     */
    @Override
    protected void doOpen() throws Throwable {
        netVertxVerticle = new NetVertxVerticle();
        Vertx.vertx().deployVerticle(netVertxVerticle);
    }

    /**
     * Init bootstrap
     *
     * @throws Throwable
     */
    @Override
    protected void doConnect() throws Throwable {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8082);
        netVertxVerticle.connect(socketAddress.getPort(), socketAddress.getHostString());
        netVertxVerticle.setChannelHandler(channelHandler);
    }

    @Override
    protected void doClose() throws Throwable {
        netVertxVerticle.getNetSocket().close();
    }

    @Override
    protected void doDisConnect() throws Throwable {
        try {
            VertxChannel.removeChannelIfDisconnected(netVertxVerticle);
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
    }

    @Override
    protected Channel getChannel() {
        NetVertxVerticle c = netVertxVerticle;
        if (c == null || !c.isConnected()) {
            return null;
        }
        return VertxChannel.getOrAddChannel(c);
    }

    @Override
    public boolean isConnected() {
        return netVertxVerticle.isConnected();
    }

}
