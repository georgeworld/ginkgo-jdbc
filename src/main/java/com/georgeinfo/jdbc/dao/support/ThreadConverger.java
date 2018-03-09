/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.support;

import com.georgeinfo.jdbc.dao.helper.NotifyThread;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程汇聚器
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public interface ThreadConverger {

    /**
     * 注册线程
     *
     * @param thread 被注册的线程，拥有执行完主动向主线程通知的功能
     */
    public void regThread(NotifyThread thread);

    /**
     * 线程注册计数器
     *
     * @return
     */
    public AtomicInteger getThreadRegisterCounter();

    /**
     * 线程完成计数器
     *
     * @return
     */
    public AtomicInteger getThreadCompletedCounter();

    /**
     * 线程注册计数器与线程完成计数器比较
     *
     * @param threadRegisterCounter 线程注册计数器
     * @param threadCompletedCounter 线程完成计数器
     * @return 线程注册计数器是否与线程完成计数器相等
     */
    public boolean counterCompare(AtomicInteger threadRegisterCounter, AtomicInteger threadCompletedCounter);
}
