package com.idc.common.vertx.eventbuscluster;

import com.alibaba.fastjson.util.TypeUtils;
import com.idc.common.po.*;
import com.idc.common.util.VertxMsgUtils;
import com.idc.common.vertx.gate.common.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

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

    private String eventBusName = "vertx.cluster.ConnectSvr";

    //    @PostConstruct
    public void initServerCluster() throws Exception {
        clusteredVertxServer.setAndStart(eventBusName);
    }

    //    @PostConstruct
    public void initCluster() throws Exception {
        clusterVertxClient.setAndStart();
        VertxMessageReq vertxMessageReq = new VertxMessageReq();
        vertxMessageReq.setTimeStamp(System.currentTimeMillis());
        vertxMessageReq.setContent("hello,this is from vertx event bus client");
        vertxMessageReq.setSide(1);
        vertxMessageReq.setSequence(201);
        Thread.sleep(3000);
        AppResponse result = clusterVertxClient.replyHello(vertxMessageReq, eventBusName);
        logger.info("send and get hello result:{}", result.getValue());
        vertxMessageReq.setContent("zidan.lian");
        vertxMessageReq.setSequence(202);
        AppResponse addressInfo = clusterVertxClient.getAddressInfo(vertxMessageReq, eventBusName);
        logger.info("address info result:{}", addressInfo.getValue());
        AddressPo addressPo = new AddressPo();
        addressPo.setName("zidan.lian");
        addressPo.setAddress("上海市浦东新区杨高南路759号陆家嘴世纪金融广场2号楼16楼");
        addressPo.setUserId("0790");
        addressPo.setTel("13127933306");
        vertxMessageReq.setContent(addressPo);
        vertxMessageReq.setSequence(203);
        AppResponse addressUpdateResult = clusterVertxClient.updateAddressInfo(vertxMessageReq, eventBusName);
        logger.info("address info update result:{}", addressUpdateResult.getValue());
        if (!addressUpdateResult.hasException() && addressUpdateResult.getStatus() == Response.OK) {
            AddressPo addressResult = TypeUtils.castToJavaBean(addressUpdateResult.getValue(), AddressPo.class);
            System.out.println(addressResult);
        }
/*        final ExecutorService executorService = new ThreadPoolExecutor(26, 32, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            executorService.submit(new FeatureTask(vertxMessageReq, begin));
        }*/
    }

    public class FeatureTask implements Runnable {
        private VertxMessageReq vertxMessageReq;
        private long start;

        public FeatureTask(VertxMessageReq vertxMessageReq, long start) {
            this.vertxMessageReq = vertxMessageReq;
            this.start = start;
        }

        @Override
        public void run() {
            AppResponse addressUpdateResult1 = null;
            try {
                vertxMessageReq.setTimeStamp(System.currentTimeMillis());
                addressUpdateResult1 = clusterVertxClient.updateAddressInfo(vertxMessageReq, eventBusName);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("address info update result:{},时间差:{}", addressUpdateResult1, System.currentTimeMillis() - start);


        }
    }

    @PostConstruct
    public void initGateClient() throws Exception {
        clusterVertxClient.setAndStart();
        VertxMessageReq vertxMessageReq = new VertxMessageReq();
        vertxMessageReq.setTimeStamp(System.currentTimeMillis());
        vertxMessageReq.setContent("hello,this is from vertx event bus client");
        vertxMessageReq.setSide(1);
        vertxMessageReq.setSequence(201);
        Thread.sleep(3000);
        AddressPo addressPo = new AddressPo();
        addressPo.setName("zidan.lian");
        addressPo.setAddress("上海市浦东新区杨高南路759号陆家嘴世纪金融广场2号楼16楼");
        addressPo.setUserId("0790");
        addressPo.setTel("13127933306");
        vertxMessageReq.setContent(addressPo);
        vertxMessageReq.setSequence(203);
        RpcInvocation invocationRemote = new RpcInvocation();
        invocationRemote.setInterfaceName("userAddressInfo");
        invocationRemote.setResource("default");
        invocationRemote.setMethodName("updateAddress");
        vertxMessageReq.setInvocationRemote(invocationRemote);
        vertxMessageReq.setInvocation(VertxMsgUtils.getGateClientInvocation());
        vertxMessageReq.setRouteDestination("ConnectSvr");
        vertxMessageReq.setRouteOrigin("BrokerSvr");
        logger.info("address info update begin");
        AppResponse addressUpdateResult = clusterVertxClient.sendMessageToEventBusSyn(Const.eventBusPre + "BrokerGate", vertxMessageReq, 30 * 1000);
        logger.info("address info update result:{}", addressUpdateResult.getValue());
        final ExecutorService executorService = new ThreadPoolExecutor(26, 32, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            executorService.submit(new FeatureGateTask(vertxMessageReq, begin));
        }

    }

    public class FeatureGateTask implements Runnable {
        private VertxMessageReq vertxMessageReq;
        private long start;

        public FeatureGateTask(VertxMessageReq vertxMessageReq, long start) {
            this.vertxMessageReq = vertxMessageReq;
            this.start = start;
        }

        @Override
        public void run() {
            AppResponse addressUpdateResult1 = null;
            try {
                logger.info("address info update begin");
                AppResponse  addressUpdateResult = clusterVertxClient.sendMessageToEventBusSyn(Const.eventBusPre + "BrokerGate", vertxMessageReq, 30 * 1000);
                logger.info("address info update result:{}", addressUpdateResult.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("address info update result:{},时间差:{}", addressUpdateResult1, System.currentTimeMillis() - start);


        }
    }

}
