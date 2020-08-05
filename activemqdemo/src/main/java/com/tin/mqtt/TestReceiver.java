package com.tin.mqtt;

import com.tin.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 测试订阅
 */
@Component
public class TestReceiver {
    private static final Logger logger = LoggerFactory.getLogger(TestReceiver.class);

    /*// queue模式的消费者
    @JmsListener(destination="${spring.activemq.queue-name}", containerFactory="queueListener")
    public void readActiveQueue(String message) {
        System.out.println("queue接受到：" + message);
    }*/

    /*@JmsListener(destination = Constants.UPDATE_TOPIC_NAME*//*selector = "ClientID='ckMqttClientId_result'",containerFactory="topicListener"*//*)
    public void receive2(String message) {
        logger.info(String.valueOf(message.getBytes()));
        logger.info("Two Receive: " + message);
    }*/

    @JmsListener(destination = "test")
    public void receive3(String message) {
        logger.info("Three Receive: " + message);
    }
}
