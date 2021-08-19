package com.idc.common.vertx.eventbuscluster;

import com.alibaba.fastjson.JSONObject;
import com.idc.common.po.AsyncRpcResult;
import com.idc.common.po.Invoker;
import com.idc.common.po.RpcInvocation;
import com.idc.common.po.VertxMessageReq;
import com.idc.common.vertx.eventbuscluster.proxyfactory.RpcException;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
public class VertxMessageHandle {

    private static Logger logger = LoggerFactory.getLogger(ClusterVertxClient.class);

    public CompletableFuture<Object> reply(Message<JsonObject> message) throws ClassNotFoundException {
        logger.info("接收客户端消息：{}", message.body());
        VertxMessageReq req = JSONObject.parseObject(message.body().encode(), VertxMessageReq.class);
        RpcInvocation invocation = req.getInvocation();
        Class<?>[] interfaceClass = {Class.forName("com.idc.common.po.VertxMessageReq", true, Thread.currentThread()
                .getContextClassLoader())};
        invocation.setParameterTypes(interfaceClass);
        req.setInvocation(null);
        invocation.setArguments(new Object[]{req});
        Invoker<Object> invoker = ClusteredVertxServer.getInvoker(invocation.getInterfaceName() + "_" + invocation.getResource());
        if (invoker == null) {
            throw new RpcException(404, "could not find designed interface");
        } else {
            AsyncRpcResult asyncRpcResult = (AsyncRpcResult) invoker.invoke(invocation);
            return asyncRpcResult.completionFuture().thenApply(Function.identity());
        }


    }
}
