package com.tin.config;//package com.tin.it.config;
//
//import com.tin.it.util.Constants;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttTopic;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.core.MessageProducer;
//import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
//import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
//import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
//import org.springframework.messaging.*;
//
//@Configuration
//public class MqttReceiverConfig {
//    private static final Logger logger = LoggerFactory.getLogger(MqttReceiverConfig.class);
//    /**
//     * 订阅的bean名称
//     */
//    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
//
//    public MqttPahoMessageDrivenChannelAdapter adapter;
//
//    // 客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
//    private static final byte[] WILL_DATA;
//    static {
//        WILL_DATA = "offline".getBytes();
//    }
//
//    @Value("${spring.mqtt.client.username}")
//    private String username;
//    @Value("${spring.mqtt.client.password}")
//    private String password;
//    @Value("${spring.mqtt.client.serverURIs}")
//    private String serverURIs;
//    @Value("${spring.mqtt.client.clientId}")
//    private String clientId;
//    @Value("${spring.mqtt.client.keepAliveInterval}")
//    private int keepAliveInterval;
//    @Value("${spring.mqtt.client.connectionTimeout}")
//    private int connectionTimeout;
//
//    @Value("${spring.mqtt.consumer.defaultQos}")
//    private int defaultConsumerQos;
//    @Value("${spring.mqtt.consumer.completionTimeout}")
//    private long completionTimeout;
//    @Value("${spring.mqtt.consumer.consumerTopics}")
//    private String[] consumerTopics;
//
//    /**
//     * MQTT连接器选项
//     */
//    @Bean
//    public MqttConnectOptions getReceiverMqttConnectOptions(){
//        MqttConnectOptions options = new MqttConnectOptions();
//        // 设置连接的用户名
//        if(!username.trim().equals("")){
//            options.setUserName(username);
//        }
//        // 设置连接的密码
//        options.setPassword(password.toCharArray());
//        //
//        options.setCleanSession(true);
//        // 设置连接的地址
//        options.setServerURIs(serverURIs.split(","));
//        // 设置超时时间 单位为秒
//        options.setConnectionTimeout(connectionTimeout);
//        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
//        // 但这个方法并没有重连的机制
//        options.setKeepAliveInterval(keepAliveInterval);
//        options.setAutomaticReconnect(true);
//        //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//        //options.setWill(topic, "close".getBytes(), 0, true);
//        return options;
//    }
//
//    /**
//     * MQTT客户端
//     */
//    @Bean
//    public MqttPahoClientFactory receiverMqttClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        factory.setConnectionOptions(getReceiverMqttConnectOptions());
//
//        return factory;
//    }
//
//
//}
