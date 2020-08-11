package com.tin.it.mqtt.handler;

import com.tin.it.thread.ResponseMessageThread;
import com.tin.it.util.ControlCode;
import com.tin.it.util.PacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import java.util.concurrent.*;

/**
 * 处理返回消息
 */
public class ResponseMessageArrivedHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseMessageArrivedHandler.class);

    ExecutorService threadPool = new ThreadPoolExecutor(1,1,0, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    private ExecutorService subResponseExecutor;
    private LinkedBlockingQueue   subResponseQueue;

    public ResponseMessageArrivedHandler(){
        int coreThreadNum = Runtime.getRuntime().availableProcessors();
        logger.info("coreThreadNum: "+coreThreadNum);
        this.subResponseQueue = new LinkedBlockingQueue(10000);
        this.subResponseExecutor = new ThreadPoolExecutor(coreThreadNum * 2,
                coreThreadNum * 2,
                30000,
                TimeUnit.MILLISECONDS,
                subResponseQueue,
                new ThreadFactoryImpl("SubResponseThread"),
                new RejectHandler("subResponse", 10000));
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        logger.info(headers.toString());
        String msg = message.getPayload().toString();
        logger.info(msg);
        String topic = headers.get("mqtt_receivedTopic").toString();
        // 判断控制码
        checkResponseMessageType(msg);
        logger.info(topic);
    }

    private void checkResponseMessageType(String message){
        // 控制码
        String controlCode = PacketUtil.getControlCode(message);
        // 设备ID
        String deviceCode = PacketUtil.getDeviceCode(message);
        if(ControlCode.CODE_94.equals(controlCode)){
            // 更新软件包主题返回的消息
            subResponseExecutor.submit(new ResponseMessageThread(message));
        }
    }
}
