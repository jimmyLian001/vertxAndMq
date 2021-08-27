package com.idc.common.vertx.gate.common;

import com.idc.common.po.RpcInvocation;

import java.io.Serializable;

/**
 * 描述：Vertx Tcp Message
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/20 ProjectName: vertxAndMq
 */
public class VertxTcpMessage implements Serializable {

    /**
     * 是否心跳
     */
    private boolean heartBeat = false;
    /**
     * 消息内容
     */
    private Object content;
    /**
     * 时间戳
     */
    private long timeStamp;
    /**
     * 消息唯一id
     */
    private String messageId;

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
    private RpcInvocation invocation;
    /**
     * TCP消息状态码
     */
    private String code;
    /**
     * 消息方向0:建立链接，1：客户端发往服务端，2：服务端发往客户端
     */
    private int side;
    /**
     * socketId
     */
    private String socketId;
    /**
     * 0：默认，传输用，1:request,2:response
     */
    private Integer messageType = 0;

    public boolean isHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public RpcInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(RpcInvocation invocation) {
        this.invocation = invocation;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }
    @Override
    public String toString() {
        return "{"
                + "\"heartBeat\":" + heartBeat
                + ",\"content\":" + content
                + ",\"timeStamp\":" + timeStamp
                + ",\"messageId\":\"" + messageId + '\"'
                + ",\"routeOrigin\":\"" + routeOrigin + '\"'
                + ",\"routeDestination\":\"" + routeDestination + '\"'
                + ",\"code\":\"" + code + '\"'
                + ",\"side\":" + side
                + ",\"messageType\":" + messageType
                + ",\"socketId\":" + socketId
                + ",\"invocation\":" + invocation
                + "}";

    }
}
