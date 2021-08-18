package com.idc.common.service.impl;

import com.idc.common.annotation.VertxUrl;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.po.VertxMessageRes;
import com.idc.common.service.SayHello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * </pre>
 */
@Service
@VertxUrl(interfaceName = "SayHello")
public class SayHelloImpl implements SayHello {
    private static Logger logger = LoggerFactory.getLogger(SayHelloImpl.class);

    @Override
    public VertxMessageRes replyHello(VertxMessageReq params) {
        logger.info("接收客户端消息：{}", params);
        VertxMessageRes result = new VertxMessageRes();
        result.setCode("200");
        result.setMsg("[回复]客户端你好!");
        result.setTimeStamp(System.currentTimeMillis());
        return result;
    }
}
