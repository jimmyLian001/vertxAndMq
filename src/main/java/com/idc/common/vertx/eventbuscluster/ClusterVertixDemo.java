package com.idc.common.vertx.eventbuscluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
@Component
public class ClusterVertixDemo {

    @Autowired
    private ClusteredVertxServer clusteredVertxServer;

    @Autowired
    private ClusterVertxClient clusterVertxClient;

    @PostConstruct
    public void initCluster() throws Exception {
        clusteredVertxServer.setAndStart();
        Thread.sleep(1000);
//        clusterVertxClient.setAndStart();
    }

}
