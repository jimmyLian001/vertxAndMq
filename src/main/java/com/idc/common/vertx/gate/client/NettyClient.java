package com.idc.common.vertx.gate.client;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.exchage.NettyChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public class NettyClient extends AbstractClient {


    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    /**
     * netty client bootstrap
     */
    private static final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors() + 1, 32), new DefaultThreadFactory("NettyClientWorker", true));

    private static final String SOCKS_PROXY_HOST = "socksProxyHost";

    private static final String SOCKS_PROXY_PORT = "socksProxyPort";

    private static final String DEFAULT_SOCKS_PROXY_PORT = "1080";

    private Bootstrap bootstrap;

    /**
     * current channel. Each successful invocation of {@link NettyClient#doConnect()} will
     * replace this with new channel and close old channel.
     * <b>volatile, please copy reference to use.</b>
     */
    private volatile Channel channel;

    /**
     * The constructor of NettyClient.
     * It wil init and start netty.
     */
    public NettyClient() throws RpcException {
        // you can customize name and type of client thread pool by THREAD_NAME_KEY and THREADPOOL_KEY in CommonConstants.
        // the handler will be warped: MultiMessageHandler->HeartbeatHandler->handler
        super(null);
    }

    /**
     * Init bootstrap
     *
     * @throws Throwable
     */
    @Override
    protected void doOpen() throws Throwable {

    }

    @Override
    protected void doConnect() throws Throwable {
        long start = System.currentTimeMillis();
        this.channel = new EpollDatagramChannel();
    }

    @Override
    protected void doDisConnect() throws Throwable {
        try {
            NettyChannel.removeChannelIfDisconnected(channel);
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
    }

    @Override
    protected void doClose() throws Throwable {
        // can't shutdown nioEventLoopGroup because the method will be invoked when closing one channel but not a client,
        // but when and how to close the nioEventLoopGroup ?
        // nioEventLoopGroup.shutdownGracefully();
    }

    @Override
    protected com.idc.common.vertx.gate.exchage.Channel getChannel() {
        Channel c = channel;
        if (c == null || !c.isActive()) {
            return null;
        }
        return NettyChannel.getOrAddChannel(c);
    }

}
