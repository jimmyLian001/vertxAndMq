package com.idc.common.vertx.eventbuscluster;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Name: BaseVerticle
 * @Author: Neil.Zhou
 * @Version: V1.00
 * @CreateDate: 2016/05/14
 * @Description: Definition of BaseVerticle for system
 */
public class BaseVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(BaseVerticle.class);
    private String busAddress;

    public BaseVerticle(String eventBusAddress) {
        this.busAddress = eventBusAddress;
    }
    public static final ExecutorService executor = new ThreadPoolExecutor(8, 16, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    /**
     * 系统
     *
     * @return
     */
    private Handler<Message<JsonObject>> msgHandler() {
        return msg -> {
            executor.submit(new VertixServerProcess(msg));
        };

    }

    /**
     * 注册事件驱动并
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        super.start();
        vertx.eventBus().<JsonObject>consumer(busAddress).handler(msgHandler());
    }
}
