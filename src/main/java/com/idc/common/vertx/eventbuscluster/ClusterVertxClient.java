package com.idc.common.vertx.eventbuscluster;

import com.idc.common.po.VertxMessageReq;
import com.idc.common.util.NetworkUtil;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Administrator on 2016/12/29.
 */
@Component
public class ClusterVertxClient {
    private static Logger logger = LoggerFactory.getLogger(ClusterVertxClient.class);
    private Vertx clusterVertx = null;

    public void setAndStart() {

        VertxMessageReq vertxMessageReq = new VertxMessageReq();
        vertxMessageReq.setSide(1);
        vertxMessageReq.setContent("练子丹向你打招呼..." + UUID.randomUUID().toString());

        ClusterManager mgr = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(NetworkUtil.getInterface());
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                clusterVertx = res.result();
                logger.debug("-------------------start deploy clustered event bus------");
                AtomicReference<Integer> sequence = new AtomicReference<>(0);
                clusterVertx.setPeriodic(2000, callBack -> {
                    vertxMessageReq.setTimeStamp(System.currentTimeMillis());
                    sequence.set(sequence.get() + 1);
                    vertxMessageReq.setSequence(sequence.get());
                    clusterVertx.eventBus().<JsonObject>send("vertx.cluster.replyHello", JsonObject.mapFrom(vertxMessageReq), new DeliveryOptions().addHeader("method", "replyHello").setSendTimeout(60000), resultBody -> {
                        if (resultBody.failed()) {
                            logger.error("Fail for the process!");
                            return;
                        }
                        logger.info("收到服务端响应结果：{}", resultBody.result().body().encode());
                    });
                });
            } else {
                logger.error("Failed: ", res.cause());
            }
        });

    }
}
