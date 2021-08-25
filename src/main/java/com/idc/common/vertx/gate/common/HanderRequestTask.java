package com.idc.common.vertx.gate.common;

import com.idc.common.po.AppResponse;
import com.idc.common.vertx.gate.exchage.ExchangeChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/23 ProjectName: vertxAndMq
 */
public class HanderRequestTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(HanderRequestTask.class);
    private final ExchangeChannel channel;
    private CompletableFuture<AppResponse> future;
    private Request req;

    public HanderRequestTask(ExchangeChannel channel, CompletableFuture<AppResponse> future, Request req) {
        this.channel = channel;
        this.future = future;
        this.req = req;
    }

    @Override
    public void run() {
        AppResponse appResponse = new AppResponse();
        try {
            appResponse = future.get();
            appResponse.setId(req.getId());
            appResponse.setSocketAddress(req.getSocketAddress());
            appResponse.setRouteOrigin(req.getRouteOrigin());
            appResponse.setRouteDestination(req.getRouteDestination());
            channel.send(appResponse);
        } catch (Exception e) {
            appResponse.setId(req.getId());
            appResponse.setSocketAddress(req.getSocketAddress());
            appResponse.setRouteOrigin(req.getRouteOrigin());
            appResponse.setRouteDestination(req.getRouteDestination());
            appResponse.setException(e);
            appResponse.setValue("vertx handle response error");
            future.complete(appResponse);
            channel.send(appResponse);
            logger.error("FeatureTask run error");
        }

    }
}
