package com.idc.common.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * 描述：负责接收前端http请求，并将消息发布到事件总线上，等待后台处理程序处理完该事件后，返回事件处理结果
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/13 ProjectName: vertxAndKafka
 */
public class WebVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/spring/hello1").handler(
                // 唤起vert.x的事件总线，并发送一个简单消息
                routingContext -> vertx.eventBus().<String>send(
                        WebServerVerticle.GET_HELLO_MSG_SERVICE_ADDRESS,// 消息地址
                        "event bus calls spring service",// 消息内容
                        result -> {// 异步结果处理
                            if (result.succeeded()) {// 成功的话，返回处理结果给前台，这里的处理结果就是service返回的一段字符串
                                routingContext.response().putHeader("content-type", "application/json").end(result.result().body());
                            } else {
                                routingContext.response().setStatusCode(400).end(result.cause().toString());
                            }
                        }
                )
        );
        router.route("/spring/hello2").handler(
                // 唤起vert.x的事件总线，并发送一个简单消息
                routingContext -> vertx.eventBus().<String>send(
                        WebServerVerticle.GET_HELLO_MSG_SERVICE_ADDRESS,// 消息地址
                        "event bus calls spring service2",// 消息内容
                        result -> {// 异步结果处理
                            if (result.succeeded()) {// 成功的话，返回处理结果给前台，这里的处理结果就是service返回的一段字符串
                                routingContext.response().putHeader("content-type", "application/json").end(result.result().body());
                            } else {
                                routingContext.response().setStatusCode(400).end(result.cause().toString());
                            }
                        }
                )
        );
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
