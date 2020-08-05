package com.tin.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;

import java.util.concurrent.ScheduledExecutorService;

public class ClientMQTT {
    public static final String HOST = "tcp://192.168.10.80:1883";
    // 使用通配符的主题，订阅所有topic/chuangkou/updateResult/的消息
    //public static final String TOPIC1 = "topic/chuangkou/updateResult/#";  //"queue.chuangkou.gateway";
    public static final String TOPIC1 = "chuangkou/gateway/request/#";
    private static final String clientid = "server12";
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = "chuangkou";    //非必须
    private String passWord = "chuangkou88";  //非必须
    private ScheduledExecutorService scheduler;

    private static final boolean RETAINED = true;

    public static void main(String[] args) {
        ClientMQTT clientMQTT = new ClientMQTT();
        clientMQTT.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        //clientMQTT.onDestory();
    }

    private void start() {
        try {
            final MemoryPersistence memoryPersistence = new MemoryPersistence();
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, memoryPersistence);
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(30);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(30);
            //设置断开后重新连接
            options.setAutomaticReconnect(true);
            // 设置回调
            client.setCallback(new PushCallback());
            // 允许主题通配符
            MqttTopic.validate(TOPIC1,true);
            //MqttTopic topic = client.getTopic(TOPIC1);
            //final int qos = 1;
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //遗嘱
            //options.setWill(topic, "close".getBytes(), qos, true);
            client.connect(options);
            //订阅消息
            int[] Qos = {2};//0：最多一次 、1：最少一次 、2：只有一次
            //String[] topic1 = new String[10000];
//            StringBuffer tpc = new StringBuffer();
//            for (int i = 0; i < 10000; i++) {
//                tpc.append(",");
//                tpc.append(TOPIC1 + "/test"+i);
//            }
//            String[] topic1 = tpc.substring(1).split(",");
            String[] topic1 = {TOPIC1};
            MqttTopic.validate(TOPIC1,true);
            client.subscribe(topic1, Qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDestory(){
        if(client != null){
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }



    public void test(){

    }

}
