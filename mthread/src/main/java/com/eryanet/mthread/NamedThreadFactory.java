package com.eryanet.mthread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger mCount = new AtomicInteger(1);
    private String threadName;

    public NamedThreadFactory() {
    }

    public NamedThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadName + "-pool-thread-" + mCount.getAndIncrement());
    }

}