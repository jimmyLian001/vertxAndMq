package com.idc.common.vertx.gate.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 描述：远程连接地址
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public class RemoteAddress {

    public RemoteAddress() {
    }

    public RemoteAddress(String host, int port) {
        this.port = port;
        this.host = host;
    }

    private int port;
    private String host;

    /**
     * 协议0：netty,1:vertx
     */
    private int protocol = 1;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    private String getPrimeKey() {
        String primeKey = "";
        if (StringUtils.isNotBlank(this.host)) {
            primeKey = primeKey + this.host;
        }
        return primeKey + this.port;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((StringUtils.isBlank(getPrimeKey())) ? 0 : this.getPrimeKey().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object instanceof JSONObject) {
            object = JSON.parseObject(JSON.toJSONString(object), RemoteAddress.class);
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        RemoteAddress userInfo = (RemoteAddress) object;
        if (StringUtils.isNotBlank(this.getPrimeKey()) && this.getPrimeKey().equals(userInfo.getPrimeKey())) {
            return true;
        }
        return false;

    }


}
