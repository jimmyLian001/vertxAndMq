package com.idc.common.vertx.gate.timer;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public interface TimerTask {

    void run(Timeout timeout) throws Exception;
}
