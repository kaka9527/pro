package com.tin.iot.protocol;

import com.tin.iot.common.message.GrozaDupPublishMessageStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author james
 * @date 2018年10月21日 19:02
 * PUBACK连接处理
 */
public class PubAck {

    private GrozaDupPublishMessageStoreService grozaDupPublishMessageStoreService;

    private static final Logger log = LoggerFactory.getLogger(PubAck.class);

    public PubAck(GrozaDupPublishMessageStoreService grozaDupPublishMessageStoreService){
        this.grozaDupPublishMessageStoreService = grozaDupPublishMessageStoreService;
    }

    public void processPubAck(Channel channel, MqttMessageIdVariableHeader variableHeader){
        int messageId = variableHeader.messageId();
        log.info("PUBACK - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
        grozaDupPublishMessageStoreService.remove((String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);

    }
}
