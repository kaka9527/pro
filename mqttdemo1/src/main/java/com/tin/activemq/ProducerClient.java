package com.tin.activemq;

import javax.jms.JMSException;
import java.util.concurrent.*;

public class ProducerClient {

    private static final String UPLOAD_TOPIC_NAME = "queue.chuangkou.update";
    private static final String BIN_FILE_NAME = "Project.bin";

    private static final String clientId = "Project.bin";

    public static void main(String[] args) {
        try {
            // 发送更新文件
            //new Producer(UPLOAD_TOPIC_NAME,BIN_FILE_NAME);
            //Thread.sleep(1000);
            // 开始更新
            new StartUpdateDevice(UPLOAD_TOPIC_NAME,clientId);

        } catch (Exception e) {
            ActiveMQPoolsUtil.close();
            e.printStackTrace();
        }
    }


}
