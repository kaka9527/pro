package com.tin.mq;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttManager {

    private static final Logger logger = LoggerFactory.getLogger(MqttManager.class);
    
    private String serverUri;
    private String userName;
    private String passWord;
    private static volatile MqttManager mqttManager = null;
    private PduMqttCallback callback;
    private MqttClient mqttClient;
    private String[] mqttTopic;
    private int[] qos;
    private boolean autoReconnect = true;


    /**
     * 初始化
     *
     * @param serverUri mqtt域名
     * @param userName  账号
     * @param passWord  密码
     */
    public static MqttManager init(String serverUri, String userName, String passWord) {
        return init(serverUri,userName,passWord,null,null);
    }

    /**
     * 初始化
     *
     * @param serverUri mqtt域名
     * @param userName  账号
     * @param passWord  密码
     * @param topics    主题
     * @param qos       QOS ＝　0/1/2  　最多一次　　最少一次　多次
     * @return
     */
    public static MqttManager init(String serverUri, String userName, String passWord, String[] topics,
                                   int[] qos) {
        if (mqttManager == null) {
            synchronized (MqttManager.class) {
                if (mqttManager == null) {
                    mqttManager = new MqttManager(serverUri, userName, passWord, topics, qos);
                }
            }
        }
        return mqttManager;
    }


    /**
     * 修改订阅主题
     *
     * @param topics 主题
     * @param qos    QOS ＝　0/1/2  　最多一次　　最少一次　多次
     */
    public void setTopicAndQos(String[] topics, int[] qos) {
        if (mqttClient != null || !mqttClient.isConnected()) {
            try {
                mqttClient.unsubscribe(mqttTopic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        mqttTopic = topics;
        this.qos = qos;
        subscribeToTopic();
    }

    /**
     * 设置是否自动重连,默认为true
     *
     * @param isAuto
     */
    public void setAutoReconnect(boolean isAuto) {
        autoReconnect = isAuto;
    }


    public MqttManager(String serverUri, String userName, String passWord, String[] topics, int[] qos) {
        this.serverUri = serverUri;
        this.userName = userName;
        this.passWord = passWord;
        this.mqttTopic = topics;
        this.qos = qos;
        initConnect();
    }

    public void setCallback(PduMqttCallback callback) {
        this.callback = callback;
    }

    private boolean isConnect() {
        if (mqttClient != null) {
            return mqttClient.isConnected();
        }
        return false;
    }

    private void initConnect() {

        if (isConnect()) {
            return;
        }
        try {
            final MemoryPersistence memoryPersistence = new MemoryPersistence();
            mqttClient = new MqttClient(serverUri, MqttClient.generateClientId(),memoryPersistence);
            mqttClient.setCallback(new MyMqttCallbackExtended());
            //mqtt连接参数设置
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            //设置自动重连
            mqttConnectOptions.setAutomaticReconnect(autoReconnect);
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            mqttConnectOptions.setCleanSession(false);
            //设置连接的用户名
            mqttConnectOptions.setUserName(userName);
            //设置连接的密码
            mqttConnectOptions.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            mqttConnectOptions.setConnectionTimeout(120);
            // 设置会话心跳时间 单位为秒 服务器会每隔20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            mqttConnectOptions.setKeepAliveInterval(30);

            mqttClient.connect(mqttConnectOptions);
        } catch (Exception e) {
            logger.info("connect--onFailure:" + e.toString());
            e.printStackTrace();
            if (callback != null) {
                callback.connectFail(e.toString());
            }
        }
    }

    private class MyMqttCallbackExtended implements MqttCallbackExtended {

        /**
         * 连接完成回调
         *
         * @param reconnect true 断开重连,false 首次连接
         * @param serverURI 服务器URI
         */
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            logger.info("连接成功connectComplete:是否重连+" + reconnect + "-----serverURI:" + serverURI);
            if (callback != null) {
                callback.connectSuccess(reconnect);
            }
            subscribeToTopic();
        }

        @Override
        public void connectionLost(Throwable cause) {
            logger.info("connectionLost:断开");
            if (callback != null) {
                callback.connectLost("connectionLost:断开");
            }
            cause.printStackTrace();
        }

        /**
         * 消息接收，如果在订阅的时候没有设置IMqttMessageListener，那么收到消息则会在这里回调。
         * 如果设置了IMqttMessageListener，则消息回调在IMqttMessageListener中
         *
         * @param topic
         * @param message
         */
        @Override
        public void messageArrived(String topic, MqttMessage message) {
            logger.info(message.getId() + "-->receive message: " + message.toString());
            if (callback != null) {
                callback.receiveMessage(topic, message.toString());
            }

        }

        /**
         * 交付完成回调。在publish消息的时候会收到此回调.
         * qos:
         * 0 发送完则回调
         * 1 或 2 会在对方收到时候回调
         *
         * @param token
         */
        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            if (callback != null) {
                try {
                    if(null != token){
                        callback.deliveryComplete(String.valueOf(token.getMessage()));
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            logger.info(token.toString());
        }
    }

    private void subscribeToTopic() {
        if (mqttClient == null || !mqttClient.isConnected() || mqttTopic == null || qos == null) {
            return;
        }
        try {
            //  订阅的主题，和qos
            mqttClient.subscribe(mqttTopic[0], qos[0], new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    logger.info("接收消息主题 : " + topic);
                    logger.info("接收消息Qos : " + mqttMessage.getQos());
                    logger.info("接收消息内容 : " + new String(mqttMessage.getPayload()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.subscribedFail(e.toString());
            }
        }
    }

    public static MqttManager getInstance() {
        if (mqttManager == null) {
            throw new NullPointerException("请先调用init方法进行初始化");
        }
        return mqttManager;
    }

    /**
     * 发送
     *
     * @param topic    发送的主题
     * @param msg      发送的消息
     * @param qos      QOS ＝　0/1/2  　最多一次　　最少一次　多次
     * @param retained 是否保留消息，为true时,后来订阅该主题的仍然收到该消息
     */
    public void publishMessage(String topic, String msg, int qos, boolean retained) {
        if (isConnect()) {
            try {
                mqttClient.publish(topic, msg.getBytes(), qos, retained);
                logger.info("Mqtt 发送消息：" + msg);
                if (!mqttClient.isConnected()) {
                    logger.info(" messages in buffer.");
                }
            } catch (MqttException e) {
                logger.info("Error Publishing: " + e.toString());
            }
        }
    }

    public void onDestroy() {
        if (mqttClient == null) {
            return;
        }
        try {
            mqttClient.disconnect();
            mqttClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
