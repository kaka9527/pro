package com.tin.it.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class MqttMessageClient extends MqttPahoMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttMessageClient.class);

    public MqttMessageClient(String clientId, MqttPahoClientFactory clientFactory) {
        super(clientId, clientFactory);
    }

    /**
     * 发送消息
     * @param topic
     * @param content
     */
    public void sendMessage(String topic,String content){
        MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(content);
        messageBuilder.setHeader(MqttHeaders.TOPIC,topic);
        Message<String> message = messageBuilder.build();
        this.handleMessage(message);
    }

    public void sendMessage(String topic,byte[] bytes){
        MessageBuilder<byte[]> builder = MessageBuilder.withPayload(bytes);
        builder.setHeader(MqttHeaders.TOPIC,topic);
        Message<byte[]> message = builder.build();
        this.handleMessage(message);
    }
}
