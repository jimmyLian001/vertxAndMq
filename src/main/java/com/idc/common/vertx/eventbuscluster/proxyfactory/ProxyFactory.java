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
public interface ProxyFactory {

    /**
     * create proxy.
     *
     * @param invoker
     * @return proxy
     */
    <T> T getProxy(Invoker<T> invoker) ;

    <T> T getProxy(Invoker<T> invoker, boolean generic);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type);

}
