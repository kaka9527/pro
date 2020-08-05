package com.tin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UpdateConsumerGateway {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConsumerGateway.class);

    /**
     * MQTT消息处理器（消费者）
     */
    /*@Bean
    @ServiceActivator(inputChannel = MqttReceiverConfig.CHANNEL_NAME_IN)
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                message.getHeaders();
                String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                logger.info("topic=="+topic);
                String msg = message.getPayload().toString();
                logger.info("received=="+msg);
            }
        };
    }*/
}
