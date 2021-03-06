package com.idc.common.vertx.eventbuscluster;

import com.idc.common.annotation.VertxUrl;
import com.idc.common.po.AppResponse;
import com.idc.common.po.Invoker;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.util.NetworkUtil;
import com.idc.common.vertx.eventbuscluster.proxyfactory.JdkProxyFactory;
import com.idc.common.vertx.eventbuscluster.proxyfactory.ProxyFactory;
import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author lian zd
 * @date 2021/08/18
 */
@Component
public class ClusteredVertxServer {
    private static Logger logger = LoggerFactory.getLogger(ClusteredVertxServer.class);
    private static ConcurrentHashMap<String, Invoker<Object>> invokerMap = new ConcurrentHashMap<>();
    private static final ProxyFactory proxyFactory = new JdkProxyFactory();
    private Vertx clusterVertx = null;
    public static final ExecutorService executorService = new ThreadPoolExecutor(8, 16, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    /**
     * context
     */
    @Resource
    private ApplicationContext ctx;


    public Vertx setAndStart(String vertxEventBusName) {
        initAndBuildInvoker();
        ClusterManager mgr = new ZookeeperClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(NetworkUtil.getInterface());
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                clusterVertx = res.result();
                logger.debug("-------------------start deploy clustered event bus------");
                res.result().deployVerticle(new BaseVerticle(vertxEventBusName), new DeploymentOptions());
            } else {
                logger.debug("Failed: " + res.cause());
            }
        });
        return clusterVertx;
    }


    public void initAndBuildInvoker() {
        try {
            this.destroy();
            Map<String, Object> beansWithAnnotation = ctx.getBeansWithAnnotation(Service.class);
            for (Object object : beansWithAnnotation.values()) {
                Class<?> beanClass = Class.forName(object.getClass().getName(), true, Thread.currentThread()
                        .getContextClassLoader());
                VertxUrl annotation = object.getClass().getAnnotation(VertxUrl.class);
                String interfaceName = annotation.interfaceName();
                String resource = annotation.resource();
                Invoker<Object> invoker = proxyFactory.getInvoker(object, (Class) beanClass);
                invokerMap.put(interfaceName + "_" + resource, invoker);
            }
        } catch (Exception e) {
            logger.error("initAndBuildInvoker proxy error:", e);
        }

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

    public CompletableFuture<AppResponse> sendMessageToEventBusAsn(String vertxEventBusName, VertxMessageReq vertxMessageReq, long timeOut) throws ExecutionException, InterruptedException {
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
        return future;
    }

    public static Invoker<Object> getInvoker(String key) {
        return invokerMap.get(key);
    }

    /**
     * ????????????
     */
    public void destroy() {
        try {
            logger.info("cluster destroy begin");
            invokerMap.clear();
            if (clusterVertx != null) {
                Set<String> strings = clusterVertx.deploymentIDs();
                strings.forEach(item -> {
                    clusterVertx.undeploy(item);
                });
            }
            logger.info("cluster destroy success");
        } catch (Exception e) {
            logger.error("cluster destroy error:", e);
        }
    }

    public Vertx getClusterVertx() {
        return clusterVertx;
    }
}
