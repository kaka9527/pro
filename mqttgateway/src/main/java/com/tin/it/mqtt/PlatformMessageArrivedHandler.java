package com.tin.it.mqtt;

import com.tin.it.thread.SleepUtils;
import com.tin.it.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import java.util.concurrent.*;

/**
 *
 */
public class PlatformMessageArrivedHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(PlatformMessageArrivedHandler.class);

    ExecutorService threadPool = new ThreadPoolExecutor(1,1,0, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        logger.info(headers.toString());
        String msg = message.getPayload().toString();
        logger.info(msg);
        String topic = headers.get("mqtt_receivedTopic").toString();
        // 更新软件包主题返回的消息
        //threadPool.submit(new PlatformMessageArrivedHandler.MessageArrived(msg));
        logger.info(topic);
    }

    private class MessageArrived implements Runnable {
        private String message;
        public MessageArrived(String message){
            this.message = message;
        }
        @Override
        public void run() {
            logger.info("-------retry start--------");

            logger.info("-------retry end--------");
        }
    }
}
