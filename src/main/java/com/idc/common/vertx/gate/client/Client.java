package com.idc.common.vertx.gate.client;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import com.idc.common.vertx.gate.exchage.Channel;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public interface Client extends Channel {
    /**
     * reconnect.
     */
    void reconnect() throws RpcException;
}
