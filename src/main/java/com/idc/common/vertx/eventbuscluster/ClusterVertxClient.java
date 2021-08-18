package com.idc.common.vertx.eventbuscluster;

import com.idc.common.po.AppResponse;
import com.idc.common.po.Result;
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

/**
 * @author lian zd
 * @date 2021/08/18
 */
@Component
public class ClusterVertxClient {
    private static Logger logger = LoggerFactory.getLogger(ClusterVertxClient.class);
    private Vertx clusterVertx = null;

    public Vertx setAndStart() {

        ClusterManager mgr = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(NetworkUtil.getInterface());
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                clusterVertx = res.result();
                logger.info("-------------------start deploy clustered event bus------");
            } else {
                logger.error("Failed: ", res.cause());
            }
        });
        return clusterVertx;
    }

    public void sendMessageToEventBusSyn(String vertxEventBusName, VertxMessageReq vertxMessageReq, long timeOut) {
        AppResponse appResponse = new AppResponse();
        if (clusterVertx != null) {
            vertxMessageReq.setTimeStamp(System.currentTimeMillis());
            clusterVertx.eventBus().<JsonObject>send("vertx.cluster.replyHello", JsonObject.mapFrom(vertxMessageReq), new DeliveryOptions().setSendTimeout(timeOut), resultBody -> {
                if (resultBody.failed()) {
                    logger.error("Fail for the process!");
                    return;
                }
                logger.info("收到服务端响应结果：{}", resultBody.result().body().encode());
            });

        }
    }
}
