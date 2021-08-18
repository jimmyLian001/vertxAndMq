package com.idc.common.util;

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
        return socketId+"*"+body+"\n";
    }

}
