package com.tin.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Title:Server 这是发送消息的服务端
 * Description: 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
 */
public class ServerMQTT {
    //tcp://MQTT安装的服务器地址:MQTT定义的端口号
    public static final String HOST = "tcp://192.168.10.80:1883";
    //定义一个主题
    //public static final String TOPIC = "/chuangkou/gateway/request/";
    public static final String TOPIC = "chuangkou/gateway/request/";
    //定义MQTT的ID，可以在MQTT服务配置中指定
    private static final String clientid = "server11";
    private static final boolean RETAINED = false;

    private MqttClient client;
    private static MqttTopic topic11;
    private String userName = "chuangkou";  //非必须
    private String passWord = "chuangkou88";  //非必须

    public static void main(String[] args) throws Exception {
        ServerMQTT server = new ServerMQTT();
//        String msg = "68880000100200689401010016";
//        String msg = "680100110001006891044237333701D016";
//        String msg = "680100110001006891074237333700320002065016";
        // 广播报文 999999999999
        String msg = "689999999999996891074237333700320002065016";
        server.sendMessage("123444",msg);

        //
        //server.onDestory();
    }

    /**
     * 构造函数
     * @throws MqttException
     */
    public ServerMQTT() throws MqttException {
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = new MqttClient(HOST, clientid, new MemoryPersistence());

        connect();
    }

    /**
     *  用来连接服务器
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(30);
        // 设置会话心跳时间
        options.setKeepAliveInterval(30);
        try {
            //client.setCallback(new PushCallback());
            client.connect(options);
            topic11 = client.getTopic(TOPIC+"/"+clientid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param topic
     * @param message
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public static void publish(MqttTopic topic , MqttMessage message) throws MqttPersistenceException,
            MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! "
                + token.isComplete());
    }


    public void sendMessage(String clieId,String msg)throws Exception{
        //MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(msg);
        //messageBuilder.setHeader(MqttHeaders.TOPIC,"RECEIVE_DATA");
        //Message<String> build = messageBuilder.build();
        MqttMessage message = new MqttMessage();
        message.setQos(1);  //保证消息能到达一次
        message.setRetained(RETAINED);
//        String str = "{\"clieId\":\""+clieId+"\",\"msg\":\""+msg+"\"}";
        String str = msg; //build.toString();
        message.setPayload(str.getBytes());
        try{
            //for (int i = 0; i < 10; i++) {
                topic11 = client.getTopic(TOPIC+"test"+1);;
                publish(topic11 , message);
                Thread.sleep(10);
            //}
            //断开连接
            client.disconnect();
        }catch (Exception e){
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
}
