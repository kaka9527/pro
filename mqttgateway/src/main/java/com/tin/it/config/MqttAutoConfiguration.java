package com.tin.it.config;

import com.tin.it.mqtt.MqttMessageArrivedHandler;
import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.mqtt.PlatformMessageArrivedHandler;
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
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;

import javax.annotation.Resource;

/**
 * mqtt auto configuration
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MqttAutoConfiguration.class);

    @Resource
    private MqttProperties mqttProperties;

    @Bean
    public DefaultMqttPahoClientFactory clientFactory() {

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
        MqttMessageClient client = new MqttMessageClient(mqttProperties.getClientId(), clientFactory());
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
    @Bean
    public MessageChannel mqttInputChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }

    /**
     *  chuangkou/gateway/#
     * @return
     */
    @Bean
    public MessageChannel mqttPlatformInputChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }

    /**
     * 配置client,监听的topic
     * @return
     */
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId()+"_result", clientFactory(),
                        Constants.TOPIC_GATEWAY_REQUEST);
        // 主题通配符设置
        MqttTopic.validate(Constants.TOPIC_GATEWAY_REQUEST,true);
        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public MessageProducer platformInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId()+"_platform", clientFactory(),
                        Constants.TOPIC_PLATFORM_REQUEST);
        // 主题通配符设置
        MqttTopic.validate(Constants.TOPIC_PLATFORM_REQUEST,true);
        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttPlatformInputChannel());
        return adapter;
    }

    /**
     * 通过通道获取数据
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        MqttMessageArrivedHandler messageHandler = new MqttMessageArrivedHandler();
        return messageHandler;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttPlatformInputChannel")
    public MessageHandler platformHandler() {
        PlatformMessageArrivedHandler messageHandler = new PlatformMessageArrivedHandler();
        return messageHandler;
    }

//    @Bean
//    public IntegrationFlow mqttInbound() {
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId()+"_platform", clientFactory(),
//                        Constants.TOPIC_PLATFORM_REQUEST);
//        // 主题通配符设置
//        MqttTopic.validate(Constants.TOPIC_PLATFORM_REQUEST,true);
//        adapter.setCompletionTimeout(mqttProperties.getCompletionTimeout());
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(1);
//        adapter.setOutputChannel(mqttPlatformInputChannel());
//
//        IntegrationFlowBuilder flowBuilder = IntegrationFlows.from(adapter);
//        return flowBuilder.handle(new PlatformMessageArrivedHandler()).get();
//    }
}
