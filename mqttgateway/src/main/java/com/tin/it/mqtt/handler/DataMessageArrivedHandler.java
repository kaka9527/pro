package com.tin.it.mqtt.handler;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.thread.JobPduDateTimeSetThread;
import com.tin.it.thread.PduDateSettingTask;
import com.tin.it.util.ControlCode;
import com.tin.it.util.PacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * 处理设备上报的消息
 */
public class DataMessageArrivedHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(DataMessageArrivedHandler.class);

    private ExecutorService subDataExecutor;
    private LinkedBlockingQueue   subDataQueue;

    @Resource
    MqttMessageClient mqttMessageClient;

    public DataMessageArrivedHandler(){
        int coreThreadNum = Runtime.getRuntime().availableProcessors();
        logger.info("coreThreadNum: "+coreThreadNum);
        this.subDataQueue = new LinkedBlockingQueue(10000);
        this.subDataExecutor = new ThreadPoolExecutor(coreThreadNum * 2,
                coreThreadNum * 2,
                30000,
                TimeUnit.MILLISECONDS,
                subDataQueue,
                new ThreadFactoryImpl("SubDataThread"),
                new RejectHandler("subData", 10000));
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        //logger.info(headers.toString());
        String msg = message.getPayload().toString();
        logger.info(msg);
        String topic = headers.get("mqtt_receivedTopic").toString();
        // 判断控制码
        checkDataMessageType(msg);

        logger.info(topic);
    }

    private void checkDataMessageType(String message){
        String controlCode = PacketUtil.getControlCode(message);
        // 设备ID
        String deviceCode = PacketUtil.getDeviceCode(message);
        // 从站上线请求校时
        if(ControlCode.CODE_88.equals(controlCode)) {
            try {
                // 缺少数据标判断
                // 发送校时 日期报文
                subDataExecutor.submit(new PduDateSettingTask(mqttMessageClient, deviceCode)).get();
            } catch (Exception e) {
                logger.error(" PduDateSettingTask error ",e);
            }
        }
    }
}
