package com.idc.common.vertx.eventbuscluster;

import com.idc.common.po.AppResponse;
import com.idc.common.po.Response;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

/**
 * 描述：触发自动报价与匹配
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/7/26 ProjectName: sc-app-idc
 */
public class VertixServerProcess implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Message<JsonObject> message;
    private VertxMessageHandle vertxMessageHandle;

    public VertixServerProcess(Message<JsonObject> message) {
        this.message = message;
        vertxMessageHandle = new VertxMessageHandle();
    }

    @Override
    public void run() {
        try {
            doRemoteInvocation();
        } catch (Exception e) {
            logger.error("VertixServerProcess run error for remote service:", e);
        }
    }


    private void doRemoteInvocation() {
        try {
            CompletionStage<Object> future = vertxMessageHandle.reply(message);
            future.whenComplete((appResult, t) -> {
                AppResponse response = new AppResponse();
                try {
                    if (t == null) {
                        response = (AppResponse) appResult;
                    } else {
                        response.setStatus(Response.SERVICE_ERROR);
                        response.setErrorMessage("处理失败：" + t.getMessage());
                    }
                } catch (Exception e) {
                    response.setStatus(Response.BAD_RESPONSE);
                    response.setErrorMessage("system error:" + e.getMessage());
                    logger.warn("Send result to consumer failed, channel is " + message + ", msg is " + e);
                } finally {
                    // HeaderExchangeChannel.removeChannelIfDisconnected(channel);
                }
                message.reply(JsonObject.mapFrom(response));
            });
        } catch (Exception e) {
            logger.error("doRemoteInvocation error:", e);
            AppResponse response = new AppResponse();
            response.setStatus(Response.SERVICE_NOT_FOUND);
            response.setErrorMessage("处理失败：" + e.getMessage());
            message.reply(JsonObject.mapFrom(response));
        }
    }


}
