package com.zzhl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 订单管理(生成/解析)
 * <p>Created: 2017-02-16</p>
 *
 * @author andy
 **/
public class OrderManager {
    private static final Logger logger = LoggerFactory.getLogger(OrderManager.class);

    private static final OrderManager orderManager = new OrderManager();

    final transient ReentrantLock lock = new ReentrantLock();

    public static OrderManager getInstance() {
        return orderManager;
    }

    /**
     * 生成订单号
     *
     * @return 订单号
     */
    public String createOrderNo() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return String.valueOf(System.currentTimeMillis()) + ThreadLocalRandom.current().nextInt(100, 1000);
        } finally {
            lock.unlock();
        }
    }

}
