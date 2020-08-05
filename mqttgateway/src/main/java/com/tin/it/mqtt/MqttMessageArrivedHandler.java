package com.tin.it.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tin.it.thread.SendBinDataThread;
import com.tin.it.thread.SleepUtils;
import com.tin.it.util.Constants;
import com.tin.it.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import java.util.HashMap;
import java.util.concurrent.*;

public class MqttMessageArrivedHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttMessageArrivedHandler.class);

    ExecutorService threadPool = new ThreadPoolExecutor(1,1,0, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        logger.info(headers.toString());
        String msg = message.getPayload().toString();
        logger.info(msg);
        String topic = headers.get("mqtt_receivedTopic").toString();
        // 更新软件包主题返回的消息
        if(Constants.UPDATE_RESULT_TOPIC_NAME.equals(topic)){
            if (Constants.RETRY_STATE) {
                threadPool.submit(new MessageArrived(msg));
            }
        }
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
                boolean bool = UpdateMessageUtil.retryStatus(this.message,1);
                boolean state = UpdateMessageUtil.retryStatus(this.message, 2);
                boolean rt = UpdateMessageUtil.retryStatus(this.message,3);
                if(bool || state || rt){
                    SleepUtils.second(5);
                }
                if(rt){
                    // 重发bin数据包
                    String packetTotal = UpdateMessageUtil.getRetryStr(this.message,12,16);
                    String packet = UpdateMessageUtil.getRetryStr(this.message,16,20);
                    logger.info(packetTotal+"==bin=="+packet);
                }
            }catch (Exception e) {
                logger.error(" error ",e);
            }
            logger.info("-------retry end--------");
        }
    }

}
