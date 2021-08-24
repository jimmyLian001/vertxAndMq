package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.ClusteredVertxServer;
import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.client.Client;
import com.idc.common.vertx.gate.client.NetVertxClient;
import com.idc.common.vertx.gate.client.NettyClient;
import com.idc.common.vertx.gate.common.Const;
import com.idc.common.vertx.gate.common.RemoteAddress;
import com.idc.common.vertx.gate.server.NetVertxServer;
import com.idc.common.vertx.gate.server.Server;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public class HeadExchanger implements Exchanger {

    public static final String NAME = "header";
    private ChannelHandler channelHandler = new DefaultChannelHandler();
    private ClusteredVertxServer clusteredVertxServer;

    public HeadExchanger(ClusteredVertxServer clusteredVertxServer) {
        this.clusteredVertxServer = clusteredVertxServer;
    }

    public void init(String gateName) {
        clusteredVertxServer.setAndStart(Const.eventBusPre + gateName);
        channelHandler.setClusteredVertxServer(clusteredVertxServer);
    }


    @Override
    public ExchangeClient connect(RemoteAddress address) throws RpcException {
        Client client = new NetVertxClient(channelHandler);
        if (address.getProtocol() == 0) {
            client = new NettyClient();
        }
        return new HeaderExchangeClient(client, true);
    }

    @Override
    public ExchangeServer bound(RemoteAddress address) {
        Server server = new NetVertxServer(channelHandler, address);
        return new HeaderExchangeServer(server);
    }
}
