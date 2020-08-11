package com.tin.it.mqtt.handler;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.mqtt.UpdateMessageUtil;
import com.tin.it.thread.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * 处理请求的消息
 */
public class RequestMessageArrivedHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestMessageArrivedHandler.class);

    private ExecutorService subRequestExecutor;
    private LinkedBlockingQueue   subRequestQueue;

    @Resource
    MqttMessageClient mqttMessageClient;

    public RequestMessageArrivedHandler(){
        int coreThreadNum = Runtime.getRuntime().availableProcessors();
        this.subRequestQueue = new LinkedBlockingQueue(10000);
        this.subRequestExecutor = new ThreadPoolExecutor(coreThreadNum * 2,
                coreThreadNum * 2,
                30000,
                TimeUnit.MILLISECONDS,
                subRequestQueue,
                new ThreadFactoryImpl("SubRequestThread"),
                new RejectHandler("subRequest", 10000));
    }


    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        logger.info(headers.toString());
        String msg = message.getPayload().toString();
        logger.info(msg);
        String topic = headers.get("mqtt_receivedTopic").toString();
        // 更新软件包主题返回的消息
        subRequestExecutor.submit(new MessageArrived(msg));
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
            try{
                logger.info(message);
            }catch (Exception e) {
                logger.error(" error ",e);
            }
            logger.info("-------retry end--------");
        }
    }

}
