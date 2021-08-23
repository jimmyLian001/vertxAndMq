package com.idc.common.vertx.gate.timer;

/**
 * 描述：
 * <p>
 *
 * @author : lian zd
 * @version : Version:1.0.0
 * @date : 2021/8/22 ProjectName: vertxAndMq
 */
public interface Timeout {

    /**
     * Returns the {@link Timer} that created this handle.
     */
    Timer timer();

    /**
     * Returns the {@link TimerTask} which is associated with this handle.
     */
    TimerTask task();

    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated
     * with this handle has been expired.
     */
    boolean isExpired();

    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated
     * with this handle has been cancelled.
     */
    boolean isCancelled();

    /**
     * Attempts to cancel the {@link TimerTask} associated with this handle.
     * If the task has been executed or cancelled already, it will return with
     * no side effect.
     *
     * @return True if the cancellation completed successfully, otherwise false
     */
    boolean cancel();
}
