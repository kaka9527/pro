package com.tin.mqtt;
//
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PduMQTTClient {
    private static final Logger logger = LoggerFactory.getLogger(PduMQTTClient.class);

    public static String MQTT_HOST = "tcp://127.0.0.1:1883";
    public static String MQTT_CLIENTID = "";
    public static String MQTT_USERNAME = "";
    public static String MQTT_PASSWORD = "";
    public static int MQTT_TIMEOUT = 20;
    public static int MQTT_KEEPALIVE = 30;

    private MqttClient client;
    private static volatile PduMQTTClient mqttClient = null;

    public static PduMQTTClient getInstance() {
        if (mqttClient == null) {
            synchronized (PduMQTTClient.class) {
                if (mqttClient == null) {
                    mqttClient = new PduMQTTClient();
                }
            }
        }
        return mqttClient;
    }

    private PduMQTTClient() {
        logger.info("Connect MQTT: " + this);
        connect();
    }

    private void connect() {
        try {
            client = new MqttClient(MQTT_HOST, MQTT_CLIENTID, new MemoryPersistence());
            MqttConnectOptions option = new MqttConnectOptions();
            option.setCleanSession(true);
            //option.setUserName(MQTT_USERNAME);
            //option.setPassword(MQTT_PASSWORD.toCharArray());
            option.setConnectionTimeout(MQTT_TIMEOUT);
            option.setKeepAliveInterval(MQTT_KEEPALIVE);
            option.setAutomaticReconnect(true);
            try {
                client.setCallback(new PduMQTTCallback());
                client.connect(option);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布主题，用于通知<br>
     * 默认qos为1 非持久化
     * @param topic
     * @param data
     */
    public void publish(String topic, String data) {
        publish(topic, data, 1, false);
    }

    /**
     * 发布
     * @param topic
     * @param data
     * @param qos
     * @param retained
     */
    public void publish(String topic, String data, int qos, boolean retained) {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(data.getBytes());
        MqttTopic mqttTopic = client.getTopic(topic);
        if (null == mqttTopic) {
            logger.error("Topic Not Exist");
        }
        MqttDeliveryToken token;
        try {
            token = mqttTopic.publish(message);
            token.waitForCompletion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅某个主题 qos默认为1
     *
     * @param topic
     */
    public void subscribe(String topic) {
        subscribe(topic, 1);
    }

    /**
     * 订阅某个主题
     *
     * @param topic
     * @param qos
     */
    public void subscribe(String topic, int qos) {
        try {
            client.subscribe(topic, qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
