package com.tin.it.mqtt.handler;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataMessageListener implements IMqttMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(DataMessageListener.class);

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        logger.info("topic: "+s);
        logger.info("payload: "+mqttMessage.getPayload().toString());
        logger.info("payload2: "+new String(mqttMessage.getPayload()));
    }
}

