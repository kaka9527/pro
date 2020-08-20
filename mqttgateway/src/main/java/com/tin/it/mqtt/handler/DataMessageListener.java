package com.tin.it.mqtt.handler;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class DataMessageListener implements IMqttMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(DataMessageListener.class);

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        //logger.info("topic1: "+s);
        byte[] payload = mqttMessage.getPayload();
        logger.info("length1: "+payload.length);
        String msg1 = binaryToHexString(payload);
        logger.info("msg12: {}",msg1);
    }

    /**
     * 转十六进制
     * @param bytes
     * @return
     */
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
}

