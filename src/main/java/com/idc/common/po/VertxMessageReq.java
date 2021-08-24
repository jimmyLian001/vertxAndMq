package com.idc.common.po;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
public class VertxMessageReq {

    private RpcInvocation invocation;
    private RpcInvocation invocationRemote;
    private long timeStamp;

    private Object content;
    /**
     * 1：请求，2返回
     */
    private Integer side;
    private Integer sequence;

    private String result;
    /**
     * 1：请求，2返回
     */
    private String code;


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

    public RpcInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(RpcInvocation invocation) {
        this.invocation = invocation;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(Integer side) {
        this.side = side;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public RpcInvocation getInvocationRemote() {
        return invocationRemote;
    }

    public void setInvocationRemote(RpcInvocation invocationRemote) {
        this.invocationRemote = invocationRemote;
    }

    @Override
    public String toString() {
        return "{\"VertxMessage\":{"
                + "\"invocation\":" + invocation
                + "\"timeStamp\":" + timeStamp
                + ",\"content\":\""
                + content + '\"'
                + ",\"side\":"
                + side
                + ",\"sequence\":" + sequence
                + ",\"result\":" + result
                + ",\"code\":" + code
                + ",\"routeOrigin\":" + routeOrigin
                + ",\"routeDestination\":" + routeDestination
                + ",\"invocationRemote\":" + invocationRemote
                + "}}";

    }

}
