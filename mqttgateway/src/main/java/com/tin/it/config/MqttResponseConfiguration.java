package com.tin.it.config;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.mqtt.handler.ResponseMessageArrivedHandler;
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
 * mqtt response configuration
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttResponseConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MqttResponseConfiguration.class);

    @Resource
    private MqttProperties mqttProperties;

    @Bean
    public DefaultMqttPahoClientFactory responseClientFactory() {

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
    @Bean
    public MqttMessageClient mqttMessageClient() {
        MqttMessageClient client = new MqttMessageClient(mqttProperties.getClientId(), responseClientFactory());
        client.setAsync(mqttProperties.getAsync());
        client.setDefaultQos(mqttProperties.getDefaultQos());
        client.setDefaultRetained(false);
        client.setCompletionTimeout(mqttProperties.getCompletionTimeout());
        return client;
    }

    /**
     *  订阅主题：chuangkou/gateway/#
     * @return
     */
    @Bean
    public MessageChannel mqttResponseInputChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }

    /**
     * 配置client,监听的topic: chuangkou/gateway/response/#
     * @return
     */
    @Bean
    public MessageProducer responseInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId()+"_response", responseClientFactory(),
                        Constants.TOPIC_GATEWAY_RESPONSE);
        // 主题通配符设置
        MqttTopic.validate(Constants.TOPIC_GATEWAY_RESPONSE,true);
        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttResponseInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttResponseInputChannel")
    public MessageHandler responseHandler() {
        ResponseMessageArrivedHandler messageHandler = new ResponseMessageArrivedHandler();
        return messageHandler;
    }

//    @Bean
//    public IntegrationFlow mqttInbound() {
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId()+"_response", clientFactory(),
//                        Constants.TOPIC_PLATFORM_REQUEST);
//        // 主题通配符设置
//        MqttTopic.validate(Constants.TOPIC_PLATFORM_REQUEST,true);
//        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(1);
//        adapter.setOutputChannel(mqttResponseInputChannel());
//
//        IntegrationFlowBuilder flowBuilder = IntegrationFlows.from(adapter);
//        return flowBuilder.handle(new ResponseMessageArrivedHandler()).get();
//    }
}
