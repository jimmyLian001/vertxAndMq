package com.idc.common.po;

import java.io.Serializable;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
public class VertxMessageRes implements Serializable {

    private long timeStamp;
    private String result;
    /**
     * 1：请求，2返回
     */
    private String code;
    private String msg;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "{\"VertxMessageRes\":{"
                + "\"timeStamp\":"
                + timeStamp
                + ",\"result\":\""
                + result + '\"'
                + ",\"code\":\""
                + code + '\"'
                + ",\"msg\":\""
                + msg + '\"'
                + "}}";

    }
}
