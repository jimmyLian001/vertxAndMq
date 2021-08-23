package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public abstract class AbstractChannel implements Channel {

    @Override
    public void send(Object message, boolean sent) throws RpcException {
        if (isClosed()) {
            throw new RpcException("Failed to send message "
                    + (message == null ? "" : message.getClass().getName()) + ":" + message
                    + ", cause: Channel closed. channel: " + getLocalAddress() + " -> " + getRemoteAddress());
        }
    }

    @Override
    public String toString() {
        return getLocalAddress() + " -> " + getRemoteAddress();
    }
}
