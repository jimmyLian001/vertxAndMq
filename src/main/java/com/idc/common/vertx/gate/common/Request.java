package com.idc.common.vertx.gate.common;

import com.idc.common.po.RpcInvocation;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public class Request {

    private long id;

    private Object data;

    private RpcInvocation invocation;

    public String getRouteOrigin() {
        return routeOrigin;
    }

    public void setRouteOrigin(String routeOrigin) {
        this.routeOrigin = routeOrigin;
    }

    public String getRouteDestination() {
        return routeDestination;
    }

    public void setRouteDestination(String routeDestination) {
        this.routeDestination = routeDestination;
    }

    private String routeOrigin;
    private String routeDestination;

    private static final AtomicLong INVOKE_ID = new AtomicLong(0);

    public Request() {
        this.id = newId();
    }

    public Request(long id) {
        this.id = id;
    }

    private static long newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        if (INVOKE_ID.get() == Integer.MAX_VALUE) {
            INVOKE_ID.getAndSet(0);
        }
        return INVOKE_ID.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public RpcInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(RpcInvocation invocation) {
        this.invocation = invocation;
    }
}
