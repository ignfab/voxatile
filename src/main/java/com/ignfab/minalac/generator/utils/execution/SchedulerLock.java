package com.ignfab.minalac.generator.utils.execution;

public class SchedulerLock {
    private boolean done = false;

    public synchronized void notifyDone() {
        done = true;
        notifyAll();
    }

    public synchronized boolean waitDone(long timeout) throws InterruptedException {
        done = false;
        wait(timeout);
        return done;
    }
}
