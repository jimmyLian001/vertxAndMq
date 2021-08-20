package com.idc.common.util;

import com.alibaba.fastjson.JSON;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/5/7 ProjectName: vertxDemo
 */
public final class VertxMsgUtils {
    private VertxMsgUtils() {
    }

    public static String joinMsg(String socketId, String body) {
        return socketId + "*" + body + "\n";
    }

    public static String joinMsg(Object body) {
        return JSON.toJSONString(body) + "idcEnd";
    }

}
