package com.taskadapter.connector.basecamp.classic.transport.throttling;

/**
 * Primitiviest throttler, which limits a rate between two request starts.
 * 
 */
public final class IntervalThrottler implements BasicThrottler {
    /**
     * Interval between two calls.
     */
    private final int intervalMillis;

    /**
     * Next acquire time.
     */
    private long nextAcquire;

    /**
     * Locked flag.
     */
    private boolean locked;

    private final Object lock = new Object();

    public IntervalThrottler(int intervalMillis) {
        if (intervalMillis <= 0) {
            throw new IllegalArgumentException(
                    "Interval must be positive but is " + intervalMillis);
        }
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void start() throws InterruptedException {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            while (locked || now < nextAcquire) {
                lock.wait();
                now = System.currentTimeMillis();
            }

            nextAcquire = now + intervalMillis;
            locked = true;
        }
    }

    @Override
    public void end() {
        synchronized (lock) {
            locked = false;
            lock.notify();
        }
    }

}
