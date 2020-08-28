package com.tin.it.config;

import com.tin.it.mqtt.MqttMessageClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

import javax.annotation.Resource;

/**
 * mqtt data configuration
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttDataConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MqttDataConfiguration.class);

    @Resource
    private MqttProperties mqttProperties;

    @Bean
    public DefaultMqttPahoClientFactory dataClientFactory() {

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        String username = mqttProperties.getUsername();
        String password = mqttProperties.getPassword();
        if(username != null) {
            connectOptions.setUserName(username);
        }
        if (password != null) {
            connectOptions.setPassword(password.toCharArray());
        }
        String[] serverURIs = mqttProperties.getServerURIs();
        if (serverURIs == null || serverURIs.length == 0) {
            logger.error("serverURIs can not be null",new NullPointerException());
        }
        connectOptions.setCleanSession(mqttProperties.getCleanSession());
        connectOptions.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        connectOptions.setServerURIs(serverURIs);
        connectOptions.setMaxInflight(1000);

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(connectOptions);

        return factory;
    }

    /**
     * 消息客户端
     * @return
     */
    @Bean("MqttMessageClient")
    public MqttMessageClient mqttMessageClient() {
        MqttMessageClient client = new MqttMessageClient(mqttProperties.getClientId()+"_send", dataClientFactory());
        client.setAsync(mqttProperties.getAsync());
        client.setDefaultQos(mqttProperties.getDefaultQos());
        client.setDefaultRetained(false);
        client.setCompletionTimeout(mqttProperties.getCompletionTimeout());
        return client;
    }

    /**
     * 接收通道1
     * @return
     */
//    @Bean
//    public MessageChannel mqttDataInputChannel() {
//        DirectChannel directChannel = new DirectChannel();
//        return directChannel;
//    }

    /**
     * 配置client,监听的topic: chuangkou/gateway/data/#
     * @return
     */
//    @Bean
//    public MessageProducer inboundData() {
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId()+"_data", dataClientFactory(),
//                        Constants.TOPIC_GATEWAY_DATA);
//        // 主题通配符设置
//        MqttTopic.validate(Constants.TOPIC_GATEWAY_DATA,true);
//        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(Constants.QOS_IDENTITY);
//        adapter.setOutputChannel(mqttDataInputChannel());
//        return adapter;
//    }

    /**
     * 通过通道获取数据
     */
//    @Bean
//    @ServiceActivator(inputChannel = "mqttDataInputChannel")
//    public MessageHandler dataHandler() {
//        DataMessageArrivedHandler messageHandler = new DataMessageArrivedHandler();
//        return messageHandler;
//    }

}
