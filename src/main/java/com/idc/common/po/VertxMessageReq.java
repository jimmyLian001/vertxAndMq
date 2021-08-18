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
    private long timeStamp;

    private String content;
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

    public RpcInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(RpcInvocation invocation) {
        this.invocation = invocation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
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
                + "}}";

    }

}
