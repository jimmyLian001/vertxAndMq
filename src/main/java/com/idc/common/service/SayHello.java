package com.idc.common.service;

import com.idc.common.po.VertxMessageReq;
import com.idc.common.po.VertxMessageRes;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
public interface SayHello {
     VertxMessageRes replyHello(VertxMessageReq req);
}
