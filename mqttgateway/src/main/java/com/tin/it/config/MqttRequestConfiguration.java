package com.tin.it.config;

import com.tin.it.mqtt.handler.RequestMessageArrivedHandler;
import com.tin.it.util.Constants;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.annotation.Resource;

/**
 * mqtt request configuration
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttRequestConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MqttRequestConfiguration.class);

    @Resource
    private MqttProperties mqttProperties;

    @Bean
    public DefaultMqttPahoClientFactory requestClientFactory() {

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
     * 接收通道1
     * @return
     */
    @Bean
    public MessageChannel mqttRequestInputChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }

    /**
     * 配置client,监听的topic: chuangkou/platform/request/#
     * @return
     */
    @Bean
    public MessageProducer inboundRequest() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId()+"_request", requestClientFactory(),
                        Constants.TOPIC_PLATFORM_REQUEST);
        // 主题通配符设置
        MqttTopic.validate(Constants.TOPIC_PLATFORM_REQUEST,true);
        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(Constants.QOS_IDENTITY);
        adapter.setOutputChannel(mqttRequestInputChannel());
        return adapter;
    }

    /**
     * 通过通道获取数据
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttRequestInputChannel")
    public MessageHandler requestHandler() {
        RequestMessageArrivedHandler messageHandler = new RequestMessageArrivedHandler();
        return messageHandler;
    }

}
