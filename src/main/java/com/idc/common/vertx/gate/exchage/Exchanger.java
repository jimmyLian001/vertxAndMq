package com.idc.common.vertx.gate.exchage;

import com.idc.common.vertx.gate.common.RemoteAddress;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public interface Exchanger {
    ExchangeClient connect(RemoteAddress address);
}
