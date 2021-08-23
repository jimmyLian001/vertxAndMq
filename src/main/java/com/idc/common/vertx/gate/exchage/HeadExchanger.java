package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.client.Client;
import com.idc.common.vertx.gate.client.NetVertxClient;
import com.idc.common.vertx.gate.client.NettyClient;
import com.idc.common.vertx.gate.common.RemoteAddress;

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

    @Override
    public ExchangeClient connect(RemoteAddress address) throws RpcException {
        Client client = new NetVertxClient();
        if (address.getProtocol() == 0) {
            client = new NettyClient();
        }
        return new HeaderExchangeClient(client, true);
    }
}
