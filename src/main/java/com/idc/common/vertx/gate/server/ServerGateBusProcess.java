package com.idc.common.vertx.gate.server;

import com.idc.common.po.VertxMessageReq;

/**
 * 描述：网关服务端总线转发
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public interface ServerGateBusProcess {

    Object transfer(VertxMessageReq params);
}
