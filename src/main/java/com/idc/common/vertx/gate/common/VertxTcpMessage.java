package com.idc.common.vertx.gate.common;

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
    /**
     * 路由（总线地址）
     */
    private String route;
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    @Override
    public String toString() {
        return "{"
                + "\"heartBeat\":" + heartBeat
                + ",\"content\":" + content
                + ",\"timeStamp\":" + timeStamp
                + ",\"messageId\":\"" + messageId + '\"'
                + ",\"route\":\"" + route + '\"'
                + ",\"code\":\"" + code + '\"'
                + ",\"side\":" + side
                + ",\"socketId\":" + socketId
                + "}";

    }
}
