package com.idc.common.vertx.web;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * 描述：启动vertx web bus
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/13 ProjectName: vertxAndKafka
 */
@Component
public class SpringService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * context
     */
    @Resource
    private ApplicationContext ctx;

//    @PostConstruct
    public void start(){
        log.info("vertx start begin.");
        Vertx vertx = Vertx.vertx();
        //启动Spring模块
        vertx.deployVerticle(new WebServerVerticle(ctx));
        //部署服务器模块
        vertx.deployVerticle(new WebVerticle());
        log.info("vertx start end.");
    }


    public String getHello(){
        return "hello from web bus spring";
    }

}
