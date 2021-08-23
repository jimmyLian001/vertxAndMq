package com.idc.common.vertx.eventbuscluster;

import com.alibaba.fastjson.JSON;
import com.idc.common.po.AppResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
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
public class FeatureTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(FeatureTask.class);
    private AsyncResult<Message<JsonObject>> asyncResult;
    private CompletableFuture<AppResponse> future;

    public FeatureTask(AsyncResult<Message<JsonObject>> asyncResult, CompletableFuture<AppResponse> future) {
        this.asyncResult = asyncResult;
        this.future = future;
    }

    @Override
    public void run() {
        AppResponse appResponse = new AppResponse();
        try {
            if (asyncResult.failed()) {
                appResponse.setException(asyncResult.cause());
                appResponse.setValue("vertx request error");
                appResponse.setErrorMessage(asyncResult.cause().getMessage());
            } else {
                appResponse = JSON.parseObject(asyncResult.result().body().encode(), AppResponse.class);
            }
        } catch (Exception e) {
            appResponse.setException(e);
            appResponse.setValue("vertx handle response error");
            logger.error("FeatureTask run error");
        }
        future.complete(appResponse);


    }
}
