package com.tin.it.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理设备上报消息（上线、心跳、预警数据）
 */
public class DataMessageThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DataMessageThread.class);
    private String message;

    public DataMessageThread(String message){
        this.message = message;
    }

    @Override
    public void run() {
        logger.info("-------retry start--------");

        logger.info("-------retry end--------");
    }
}
