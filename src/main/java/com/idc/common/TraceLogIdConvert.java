package com.idc.common;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.DynamicConverter;

/**
 * 描述：日志id转换器
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2020/10/17
 */
public class TraceLogIdConvert extends DynamicConverter<ILoggingEvent> {
    public TraceLogIdConvert() {
    }

    @Override
    public String convert(ILoggingEvent event) {
        try {
            return (String)event.getMDCPropertyMap().get("TRACE_LOG_ID");
        } catch (Exception var3) {
            return "get mdc property error";
        }
    }
}
