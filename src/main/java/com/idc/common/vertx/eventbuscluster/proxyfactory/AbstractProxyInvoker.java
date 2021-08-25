package com.idc.common.vertx.eventbuscluster.proxyfactory;

import com.idc.common.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
public abstract class AbstractProxyInvoker<T> implements Invoker<T> {
    Logger logger = LoggerFactory.getLogger(AbstractProxyInvoker.class);

    private final T proxy;

    private final Class<T> type;


    public AbstractProxyInvoker(T proxy, Class<T> type) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy == null");
        }
        if (type == null) {
            throw new IllegalArgumentException("interface == null");
        }
        if (!type.isInstance(proxy)) {
            throw new IllegalArgumentException(proxy.getClass().getName() + " not implement interface " + type);
        }
        this.proxy = proxy;
        this.type = type;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }


    @Override
    public Result invoke(Invocation invocation) {
        try {
            Object value = doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
            CompletableFuture<Object> future = wrapWithFuture(value, invocation);
            AsyncRpcResult asyncRpcResult = new AsyncRpcResult();
            future.whenComplete((obj, t) -> {
                AppResponse result = new AppResponse();
                if (t != null) {
                    if (t instanceof CompletionException) {
                        result.setException(t.getCause());
                    } else {
                        result.setException(t);
                    }
                } else {
                    result = (AppResponse) obj;
                }
                asyncRpcResult.complete(result);
            });
            return asyncRpcResult;
        } catch (Throwable e) {
            throw new RpcException("Failed to invoke remote proxy method " + invocation.getMethodName() + " to " + ", cause: " + e.getMessage(), e);
        }
    }

    private CompletableFuture<Object> wrapWithFuture(Object value, Invocation invocation) {
        return CompletableFuture.completedFuture(value);
    }

    protected abstract Object doInvoke(T proxy, String methodName,
                                       Class<?>[] parameterTypes,
                                       Object[] arguments) throws Throwable;

}
