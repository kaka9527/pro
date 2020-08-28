package com.tin.it.mqtt.handler;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class MqttMessageHandler extends MqttPahoMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttMessageHandler.class);


    public MqttMessageHandler(String clientId, MqttPahoClientFactory clientFactory) {
        super(clientId, clientFactory);
        logger.info("---------MqttMessageHandler-----------");
    }

    @Override
    protected void publish(String topic, Object mqttMessage, Message<?> message) {
        super.publish(topic, mqttMessage, message);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        logger.info("------------handleMessage---------start---------");
        //logger.info(headers.toString());
        String payload = String.valueOf(message.getPayload());
        logger.info("payload2: {}",payload);
//        String msg = str2HexStr(payload.getBytes());
//        logger.info("msg: {}",msg);
        //super.messageArrived(topic, message);
    }
}
