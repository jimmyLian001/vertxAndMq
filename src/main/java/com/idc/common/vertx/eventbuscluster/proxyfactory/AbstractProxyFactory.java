package com.idc.common.vertx.eventbuscluster.proxyfactory;

import com.idc.common.po.Invoker;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
public abstract class AbstractProxyFactory implements ProxyFactory {


    @Override
    public <T> T getProxy(Invoker<T> invoker) {
        return getProxy(invoker, false);
    }

    @Override
    public <T> T getProxy(Invoker<T> invoker, boolean generic){
        Class<?>[] interfaces = null;
        return getProxy(invoker, interfaces);
    }

    public abstract <T> T getProxy(Invoker<T> invoker, Class<?>[] types);
}
