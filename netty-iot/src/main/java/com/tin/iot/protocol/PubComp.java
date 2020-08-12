package com.tin.iot.protocol;

import com.tin.iot.common.message.GrozaDupPubRelMessageStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author james
 * @date 2018年10月21日19:08
 * PUBCOMP连接处理
 */
public class PubComp {

    private GrozaDupPubRelMessageStoreService grozaDupPubRelMessageStoreService;

    private static final Logger log = LoggerFactory.getLogger(PubComp.class);

    public PubComp(GrozaDupPubRelMessageStoreService grozaDupPubRelMessageStoreService){
        this.grozaDupPubRelMessageStoreService = grozaDupPubRelMessageStoreService;
    }

    public void processPubComp(Channel channel, MqttMessageIdVariableHeader variableHeader){
        int messageId = variableHeader.messageId();
        log.info("PUBCOMP - clientId: {}, messageId: {}", (String) channel.attr(AttributeKey.valueOf("clientId")).get(), messageId);
        grozaDupPubRelMessageStoreService.remove((String)channel.attr(AttributeKey.valueOf("clientId")).get(), variableHeader.messageId());
    }
}
