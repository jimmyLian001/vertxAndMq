package com.idc.common.vertx.eventbuscluster;

import com.idc.common.po.AppResponse;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.util.NetworkUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lian zd
 * @date 2021/08/18
 */
@Component
public class ClusterVertxClient {
    private static Logger logger = LoggerFactory.getLogger(ClusterVertxClient.class);
    private static Vertx clusterVertx;
    public static final ExecutorService executorService = new ThreadPoolExecutor(8, 16, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public void setAndStart() {
        ClusterManager mgr = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(NetworkUtil.getInterface());
        Vertx.clusteredVertx(options, clusterHandler());
    }


    private Handler<AsyncResult<Vertx>> clusterHandler() {
        return res -> {
            if (res.succeeded()) {
                clusterVertx = res.result();
                logger.info("-------------------start deploy clustered event bus------");
            } else {
                logger.error("Failed: ", res.cause());
            }
        };

    }

    public AppResponse sendMessageToEventBusSyn(String vertxEventBusName, VertxMessageReq vertxMessageReq, long timeOut) throws ExecutionException, InterruptedException {
        CompletableFuture<AppResponse> future = new CompletableFuture<AppResponse>();
        if (clusterVertx != null) {
            vertxMessageReq.setTimeStamp(System.currentTimeMillis());
            clusterVertx.eventBus().<JsonObject>send(vertxEventBusName, JsonObject.mapFrom(vertxMessageReq), new DeliveryOptions().setSendTimeout(timeOut), resultBody -> {
                executorService.submit(new FeatureTask(resultBody, future));
            });

        }
        return future.get();
    }


    public static class FeatureTask implements Runnable {
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
                    appResponse.setValue("1000");
                    logger.error("vertx asyncResult error:", asyncResult.cause());
                } else {
                    appResponse.setValue(asyncResult.result().body());
                    logger.error("vertx asyncResult result:{}", appResponse.getValue());
                }
                System.out.println(1);
            } catch (Exception e) {
                logger.error("FeatureTask run error");
            }
            future.complete(appResponse);


        }
    }
}
