package com.idc.common.vertx.eventbuscluster;

import com.idc.common.po.AppResponse;
import com.idc.common.po.RpcInvocation;
import com.idc.common.po.VertxMessageReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 描述：ClusterVertixDemo
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
@Component
public class ClusterVertixDemo {
    private static Logger logger = LoggerFactory.getLogger(ClusterVertxClient.class);


    @Autowired
    private ClusteredVertxServer clusteredVertxServer;

    @Autowired
    private ClusterVertxClient clusterVertxClient;

    private String eventBusName = "vertx.cluster.replyHello";

    //    @PostConstruct
    public void initServerCluster() throws Exception {
        clusteredVertxServer.setAndStart(eventBusName);
    }

    @PostConstruct
    public void initCluster() throws Exception {
        clusterVertxClient.setAndStart();
        VertxMessageReq vertxMessageReq = new VertxMessageReq();
        vertxMessageReq.setTimeStamp(System.currentTimeMillis());
        vertxMessageReq.setContent("hello,this is from vertx event bus client");
        vertxMessageReq.setSide(1);
        vertxMessageReq.setSequence(201);
        Thread.sleep(1000);
        AppResponse result = clusterVertxClient.replyHello(vertxMessageReq, eventBusName);
        logger.info("send and get hello result:{}", result);
        vertxMessageReq.setContent("zidan.lian");
        vertxMessageReq.setSequence(202);
        AppResponse addressInfo = clusterVertxClient.getAddressInfo(vertxMessageReq, eventBusName);
        logger.info("address info  result:{}", addressInfo);
    }

}
