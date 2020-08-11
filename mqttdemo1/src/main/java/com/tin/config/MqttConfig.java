package com.tin.config;//package com.tin.it.config;
//
//import com.tin.it.util.Constants;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.core.MessageProducer;
//import org.springframework.integration.mq.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mq.core.MqttPahoClientFactory;
//import org.springframework.integration.mq.inbound.MqttPahoMessageDrivenChannelAdapter;
//import org.springframework.integration.mq.outbound.MqttPahoMessageHandler;
//import org.springframework.integration.mq.support.DefaultPahoMessageConverter;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//
///**
// * mq 配置
// */
//@Configuration
//public class MqttConfig {
//
//    public static final String OUTBOUND_CHANNEL = "outboundChannel";
//
//    public static final String INBOUND_CHANNEL = "queue.chuangkou.update";
//
//    public static final String RECEIVED_TOPIC_KEY = "mqtt_receivedTopic";
//
//    @Value("${spring.mq.client.username}")
//    private String username;
//    @Value("${spring.mq.client.password}")
//    private String password;
//    @Value("${spring.mq.client.serverURIs}")
//    private String[] serverURIs;
//    @Value("${spring.mq.client.clientId}")
//    private String clientId;
//    @Value("${spring.mq.client.keepAliveInterval}")
//    private int keepAliveInterval;
//    @Value("${spring.mq.client.connectionTimeout}")
//    private int connectionTimeout;
//
//    @Value("${spring.mq.producer.defaultQos}")
//    private int defaultProducerQos;
//    @Value("${spring.mq.producer.defaultRetained}")
//    private boolean defaultRetained;
//    @Value("${spring.mq.producer.defaultTopic}")
//    private String defaultTopic;
//
//    @Value("${spring.mq.consumer.defaultQos}")
//    private int defaultConsumerQos;
//    @Value("${spring.mq.consumer.completionTimeout}")
//    private long completionTimeout;
//    @Value("${spring.mq.consumer.consumerTopics}")
//    private String[] consumerTopics;
//
//    /* 客户端 */
//    @Bean
//    public MqttConnectOptions getMqttConnectOptions() {
//        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        //mqttConnectOptions.setUserName(username);
//        //mqttConnectOptions.setPassword(password.toCharArray());
//        mqttConnectOptions.setServerURIs(serverURIs);
//        mqttConnectOptions.setKeepAliveInterval(keepAliveInterval);
//        mqttConnectOptions.setConnectionTimeout(connectionTimeout);
//
//        return mqttConnectOptions;
//    }
//
//    @Bean
//    public MqttPahoClientFactory getMqttClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        factory.setConnectionOptions(getMqttConnectOptions());
//
//        return factory;
//    }
//
//    /* 发布者 */
//    @Bean
//    public MessageChannel outboundChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = Constants.UPDATE_TOPIC_NAME)
//    public MessageHandler getMqttProducer() {
//        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "_producer", getMqttClientFactory());
//        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic(defaultTopic);
//        messageHandler.setDefaultRetained(defaultRetained);
//        messageHandler.setDefaultQos(defaultProducerQos);
//
//        return messageHandler;
//    }
//
//    /* 订阅者 */
//    @Bean
//    public MessageChannel inboundChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public MessageProducer getMqttConsumer() {
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter(clientId + "_consumer", getMqttClientFactory(), consumerTopics);
//        adapter.setCompletionTimeout(completionTimeout);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(defaultConsumerQos);
//        adapter.setOutputChannel(inboundChannel());
//        return adapter;
//    }
//}
