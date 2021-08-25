package com.idc.common.vertx.gate.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述：默认常用常量
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public class Const {

    public static String eventBusPre = "vertx.cluster.";
    public static final ExecutorService executorService = new ThreadPoolExecutor(8, 16, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
}
