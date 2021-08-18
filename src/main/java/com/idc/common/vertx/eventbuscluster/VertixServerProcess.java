package com.idc.common.vertx.eventbuscluster;

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
        Response res = new Response();
        try {
            CompletionStage<Object> future = vertxMessageHandle.reply(message);
            future.whenComplete((appResult, t) -> {
                try {
                    if (t == null) {
                        res.setStatus(Response.OK);
                        res.setResult(appResult);
                    } else {
                        res.setStatus(Response.SERVICE_ERROR);
                        res.setErrorMessage("处理失败：" + t.getMessage());
                    }
                    message.reply(JsonObject.mapFrom(res));
                } catch (Exception e) {
                    logger.warn("Send result to consumer failed, channel is " + message + ", msg is " + e);
                } finally {
                    // HeaderExchangeChannel.removeChannelIfDisconnected(channel);
                }
            });
        } catch (Exception e) {
            logger.error("doRemoteInvocation error:", e);
            res.setStatus(Response.SERVICE_NOT_FOUND);
            res.setErrorMessage("处理失败：" + e.getMessage());
            message.reply(JsonObject.mapFrom(res));
        }
    }


}
