package com.idc.common.vertx.eventbuscluster;

import com.idc.common.annotation.VertxUrl;
import com.idc.common.po.Invoker;
import com.idc.common.util.NetworkUtil;
import com.idc.common.vertx.eventbuscluster.proxyfactory.JdkProxyFactory;
import com.idc.common.vertx.eventbuscluster.proxyfactory.ProxyFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lian zd
 * @date 2021/08/18
 */
@Component
public class ClusteredVertxServer {
    private static Logger logger = LoggerFactory.getLogger(ClusteredVertxServer.class);
    private static ConcurrentHashMap<String, Invoker<Object>> invokerMap = new ConcurrentHashMap<>();
    private static final ProxyFactory proxyFactory = new JdkProxyFactory();
    /**
     * context
     */
    @Resource
    private ApplicationContext ctx;


    public void setAndStart() {
        initAndBuildInvoker();

        ClusterManager mgr = new ZookeeperClusterManager();
        //mgr.setVertx(vertx);
        VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(NetworkUtil.getInterface());
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                logger.debug("-------------------start deploy clustered event bus------");
                res.result().deployVerticle(new BaseVerticle("vertx.cluster.replyHello"), new DeploymentOptions());
            } else {
                logger.debug("Failed: " + res.cause());
            }
        });

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

    public static Invoker<Object> getInvoker(String key) {
        return invokerMap.get(key);
    }

    public void destroy() {
        invokerMap.clear();
    }
}
