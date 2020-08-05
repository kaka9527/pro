package com.tin.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttPubAndSub {

    private static final Logger logger = LoggerFactory.getLogger(MqttPubAndSub.class);

    private static final String host = "tcp://192.168.10.200:1883";
    private static final String userName = "chuangkou";
    private static final String passWord = "chuangkou88";
    private static final String myTopic = "queue.chuangkou.updateResult";

    public static void main(String[] args) {
        // 初始化
        initMqtt();
        String msg = "680100110001006891074237333700320002155016";
        MqttManager.getInstance().publishMessage(myTopic,msg,1,false);
        // 销毁
        onDestroy();
    }
    private static void initMqtt() {
        String[] topics = {myTopic};
        int[] qos = {1};
        MqttManager.init(host, userName, passWord, topics, qos);
        MqttManager.getInstance().setCallback(new MyMqttCallback());

    }

    private static class MyMqttCallback extends PduMqttCallback {

        @Override
        public void subscribedSuccess(String[] mqttTopic) {
            for (String s : mqttTopic) {
                logger.info("subscribedSuccess:" + s);
            }
        }

        @Override
        public void subscribedFail(String message) {
            logger.info("subscribedFail:" + message);
        }

        @Override
        public void deliveryComplete(String message) {
            logger.info("deliveryComplete:" + message);
        }

        @Override
        public void receiveMessage(String topic, String message) {
            logger.info("topic:" + topic + "------receiveMessage:" + message);
        }

        @Override
        public void connectSuccess(boolean reconnect) {
            logger.info("connectSuccess:" + reconnect);
        }

        @Override
        public void connectFail(String message) {
            logger.info("connectFail:" + message);
        }

        @Override
        public void connectLost(String message) {
            logger.info("connectLost:" + message);
        }
    }


    private static void onDestroy() {
        MqttManager.getInstance().onDestroy();
    }
}
