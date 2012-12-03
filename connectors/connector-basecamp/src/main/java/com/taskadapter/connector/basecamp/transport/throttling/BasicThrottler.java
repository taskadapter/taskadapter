package com.taskadapter.connector.basecamp.transport.throttling;

/**
 * Throttling controller. Limits numer of calls to a "start" method. All
 * handlers must call an end method after a "throttled" action.
 */
public interface BasicThrottler {
    public void start() throws InterruptedException;

    public void end();
}
