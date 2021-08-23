package com.idc.common.po;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/17 ProjectName: vertxAndKafka
 */
public class Response {

    public static final String HEARTBEAT_EVENT = null;

    public static final String READONLY_EVENT = "R";

    /**
     * ok.
     */
    public static final int OK = 200;

    /**
     * client side timeout.
     */
    public static final byte CLIENT_TIMEOUT = 30;

    /**
     * server side timeout.
     */
    public static final byte SERVER_TIMEOUT = 31;

    /**
     * channel inactive, directly return the unfinished requests.
     */
    public static final int CHANNEL_INACTIVE = 35;

    /**
     * request format error.
     */
    public static final byte BAD_REQUEST = 40;

    /**
     * response format error.
     */
    public static final int BAD_RESPONSE = 501;

    /**
     * service not found.
     */
    public static final int SERVICE_NOT_FOUND = 404;

    /**
     * service error.
     */
    public static final int SERVICE_ERROR = 500;

    /**
     * internal server error.
     */
    public static final byte SERVER_ERROR = 80;

    /**
     * internal server error.
     */
    public static final byte CLIENT_ERROR = 90;

    /**
     * server side threadpool exhausted and quick return.
     */
    public static final byte SERVER_THREADPOOL_EXHAUSTED_ERROR = 100;

    private long id = 0;

    private String version;

    private int status = OK;

    private boolean event = false;

    private String errorMessage;

    private Object result;

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

    private boolean heartbeat ;

    public Response() {
    }

    public Response(long id) {
        this.id = id;
    }

    public Response(long id, String version) {
        this.id = id;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = true;
        result = event;
    }

    public void setEvent(boolean mEvent) {
        this.event = mEvent;
    }

    public boolean isHeartbeat() {
        return event && HEARTBEAT_EVENT == result;
    }

    @Deprecated
    public void setHeartbeat(boolean isHeartbeat) {
        if (isHeartbeat) {
            setEvent(HEARTBEAT_EVENT);
        }
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object msg) {
        result = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String msg) {
        errorMessage = msg;
    }

    @Override
    public String toString() {
        return "Response [id=" + id + ", version=" + version + ", status=" + status + ", event=" + event
                + ", error=" + errorMessage + ", result=" + (result == this ? "this" : result) + "]";
    }
}
