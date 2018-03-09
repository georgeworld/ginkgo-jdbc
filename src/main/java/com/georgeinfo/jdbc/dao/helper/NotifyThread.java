/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 拥有向主线程通知功能的线程
 *
 * @author George <Georgeinfo@163.com>
 */
public abstract class NotifyThread extends Thread {

    private AtomicInteger threadCompletedCounter;

    public abstract void doRun();

    @Override
    public void run() {
        try {
            doRun();
        } finally {
            threadCompletedCounter.incrementAndGet();
        }
    }

    public void setThreadCompletedCounter(AtomicInteger threadCompletedCounter) {
        this.threadCompletedCounter = threadCompletedCounter;
    }

}
