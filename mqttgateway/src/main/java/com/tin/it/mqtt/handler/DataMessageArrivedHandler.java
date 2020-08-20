package com.tin.it.mqtt.handler;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.thread.PduDateSettingTask;
import com.tin.it.util.ControlCode;
import com.tin.it.util.PacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConverter;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        logger.info("------------handleMessage---------start---------");
        //logger.info(headers.toString());
        String payload = String.valueOf(message.getPayload());
        logger.info("payload: {}",payload);
        String topic = headers.get("mqtt_receivedTopic").toString();
        logger.info("topic: {}",topic);
        String msg = str2HexStr(payload.getBytes());
        logger.info("msg: {}",msg);
        // 判断控制码
        checkDataMessageType(msg);

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

    private String binaryToHexString(byte[] bytes) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex ;
        }
        return result;
    }

    public String str2HexStr(byte[] bs) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }
}
