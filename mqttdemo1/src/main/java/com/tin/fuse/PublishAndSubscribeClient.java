package com.tin.fuse;

import org.fusesource.mqtt.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishAndSubscribeClient {
    private static final Logger log = LoggerFactory.getLogger(PublishAndSubscribeClient.class);
    // mqtt服务
    public static String HOST = "tcp://192.168.10.80:1883";
    // 客户端id
    public static String CLIENT_ID = "test_clientId";
    // 用户名
    public static String USERNAME = "chuangkou";
    // 密码
    public static String PASSWORD = "chuangkou88";
    // 客户端与服务端消息传递的最大间隔。它能够使服务器检测到客户端的网络是否已经丢失，而无需等待TCP/IP超时
    public static short KEEP_ALIVE = 20;

    // 更新数据包主题
    public static final String UPDATE_TOPIC_NAME = "queue.chuangkou.update";

    // 更新数据包返回主题
    public static final String UPDATE_RESULT_TOPIC_NAME = "queue.chuangkou.updateResult";

    public static void main(String[] args) throws Exception {
        // 配置MQTT连接
        MQTT mqtt = new MQTT();
        mqtt.setHost(HOST);
        mqtt.setClientId(CLIENT_ID);
        mqtt.setUserName(USERNAME);
        mqtt.setPassword(PASSWORD);
        // 是否清除session，false:MQTT服务器保存于客户端会话的的主题与确认位置,true:MQTT服务器不保存于客户端会话的的主题与确认位置
        mqtt.setCleanSession(true);
        mqtt.setKeepAlive(KEEP_ALIVE);
        // 设置重新连接的次数
        mqtt.setConnectAttemptsMax(1);
        // 设置重新连接的间隔
        mqtt.setReconnectDelay(4);
        // 获取mqtt的连接对象BlockingConnection
        CallbackConnection connection = mqtt.callbackConnection();
        // 添加接收消息的监听
        /*connection.listener(new Listener() {
            @Override
            public void onConnected() {
                System.out.println("----onConnected----");
            }

            @Override
            public void onDisconnected() {
                System.out.println("----onDisconnected----");
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer payload, Runnable ack) {
                // 处理来自topic的消息
                log.info("receive topic:{} and message:{}", topic.toString(), new String(payload.getData()));
                ack.run();
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("----onFailure----");
            }
        });*/

        // 添加连接的事件，并在连接成功时候订阅主题，发送消息
        connection.connect(new Callback<Void>() {
            // 连接成功时执行的方法
            @Override
            public void onSuccess(Void aVoid) {
                final String topic = UPDATE_TOPIC_NAME;
                // 订阅主题
                connection.subscribe(new Topic[]{new Topic(topic, QoS.AT_LEAST_ONCE)}, null);
                final String message = "hello everyone!";
                log.info("MQTT CallbackServer publish topic={},message:{}", topic, message);
                // 参数说明：1.主题 2.消息 3.服务质量(0,1,2) 4.发布保留标识，表示服务器是否要保留发布的消息 5.发布成功回调
                // 服务质量：
                // 0：最多一次，即：<=1 ;  1:至少一次，即：>=1;  2:一次，即：=1
                // 发布消息
                connection.publish(topic, message.getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
                    public void onSuccess(Void v) {
                        // the publish operation completed successfully.
                        log.info("----publish message is success!----");
                    }

                    public void onFailure(Throwable value) {
                        log.info("----publish message is failure!----");
                        value.printStackTrace();
                    }
                });
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 连接失败时执行的方法
            @Override
            public void onFailure(Throwable value) {
                // If we could not connect to the server.
                log.info("MQTTCallbackServer.CallbackConnection.connect.onFailure 连接失败......{}", value.getMessage());
                value.printStackTrace();
            }
        });

        // 等待消息发送和处理
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 断开连接
        connection.disconnect(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                log.info("--------disconnect success-----------");
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.info("--------disconnect failure-----------");
            }
        });

    }
}
