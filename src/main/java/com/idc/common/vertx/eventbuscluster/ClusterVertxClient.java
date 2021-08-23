package com.idc.common.vertx.eventbuscluster;

import com.alibaba.fastjson.JSON;
import com.idc.common.po.AppResponse;
import com.idc.common.po.RpcInvocation;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.util.NetworkUtil;
import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
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
                logger.info("-----start deploy clustered event bus------");
            } else {
                logger.error("Failed create Vertx cluster: ", res.cause());
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

        } else {
            AppResponse appResponse = new AppResponse();
            appResponse.setValue("400");
            appResponse.setException(new RpcException("Cluster Vertx has not init finished,please wait!"));
            future.complete(appResponse);
        }
        return future.get();
    }

    /**
     * say hello
     *
     * @param vertxMessageReq 请求参数
     * @param eventBusName    总线名称
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public AppResponse replyHello(VertxMessageReq vertxMessageReq, String eventBusName) throws ExecutionException, InterruptedException {
        RpcInvocation invocation = new RpcInvocation();
        invocation.setInterfaceName("SayHello");
        invocation.setResource("default");
        invocation.setMethodName("replyHello");
        vertxMessageReq.setInvocation(invocation);
        return this.sendMessageToEventBusSyn(eventBusName, vertxMessageReq, 60 * 1000);
    }

    /**
     * say hello
     *
     * @param vertxMessageReq 请求参数
     * @param eventBusName    总线名称
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public AppResponse getAddressInfo(VertxMessageReq vertxMessageReq, String eventBusName) throws ExecutionException, InterruptedException {
        RpcInvocation invocation = new RpcInvocation();
        invocation.setInterfaceName("userAddressInfo");
        invocation.setResource("default");
        invocation.setMethodName("getAddress");
        vertxMessageReq.setInvocation(invocation);
        return this.sendMessageToEventBusSyn(eventBusName, vertxMessageReq, 60 * 1000);
    }

    /**
     * updateAddressInfo
     *
     * @param vertxMessageReq 请求参数
     * @param eventBusName    总线名称
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public AppResponse updateAddressInfo(VertxMessageReq vertxMessageReq, String eventBusName) throws ExecutionException, InterruptedException {
        RpcInvocation invocation = new RpcInvocation();
        invocation.setInterfaceName("userAddressInfo");
        invocation.setResource("default");
        invocation.setMethodName("updateAddress");
        vertxMessageReq.setInvocation(invocation);
        return this.sendMessageToEventBusSyn(eventBusName, vertxMessageReq, 60 * 1000);
    }

}
