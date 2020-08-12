package com.tin.iot.protocol;

import com.tin.iot.common.subscribe.GrozaSubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UnSubscribe {

    private GrozaSubscribeStoreService grozaSubscribeStoreService;

    private static final Logger log = LoggerFactory.getLogger(UnSubscribe.class);

    public UnSubscribe(GrozaSubscribeStoreService grozaSubscribeStoreService){
        this.grozaSubscribeStoreService = grozaSubscribeStoreService;
    }

    public void processUnSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        List<String> topicFilters = msg.payload().topics();
        String clinetId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        topicFilters.forEach(topicFilter -> {
            grozaSubscribeStoreService.remove(topicFilter, clinetId);
            log.info("UNSUBSCRIBE - clientId: {}, topicFilter: {}", clinetId, topicFilter);
        });
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                null);
        channel.writeAndFlush(unsubAckMessage);
    }
}
