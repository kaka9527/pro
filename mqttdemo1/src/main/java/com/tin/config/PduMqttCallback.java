package com.tin.config;//package com.tin.it.config;
//
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class PduMqttCallback implements MqttCallback {
//
//    private static final Logger logger = LoggerFactory.getLogger(PduMqttCallback.class);
//
//    @Override
//    public void connectionLost(Throwable cause) {
//        logger.info("断开连接，建议重连" + this);
//        //断开连接，建议重连
//    }
//
//    @Override
//    public void deliveryComplete(IMqttDeliveryToken token) {
//        logger.info(token.isComplete() + "");
//    }
//
//    @Override
//    public void messageArrived(String topic, MqttMessage message) throws Exception {
//        logger.info("Topic: " + topic);
//        logger.info("Message: " + new String(message.getPayload()));
//    }
//}
