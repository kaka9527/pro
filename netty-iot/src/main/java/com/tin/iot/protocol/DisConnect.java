package com.tin.iot.protocol;

import com.tin.iot.common.message.GrozaDupPubRelMessageStoreService;
import com.tin.iot.common.message.GrozaDupPublishMessageStoreService;
import com.tin.iot.common.session.GrozaSessionStoreService;
import com.tin.iot.common.session.SessionStore;
import com.tin.iot.common.subscribe.GrozaSubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author james
 * @DISCONNECT 连接处理
 */
public class DisConnect {

    private GrozaSessionStoreService grozaSessionStoreService;

    private GrozaSubscribeStoreService grozaSubscribeStoreService;

    private GrozaDupPublishMessageStoreService grozaDupPublishMessageStoreService;

    private GrozaDupPubRelMessageStoreService grozaDupPubRelMessageStoreService;

    private static final Logger log = LoggerFactory.getLogger(DisConnect.class);

    public DisConnect(GrozaSessionStoreService grozaSessionStoreService,
                      GrozaSubscribeStoreService grozaSubscribeStoreService,
                      GrozaDupPublishMessageStoreService grozaDupPublishMessageStoreService,
                      GrozaDupPubRelMessageStoreService grozaDupPubRelMessageStoreService){
        this.grozaSessionStoreService = grozaSessionStoreService;
        this.grozaSubscribeStoreService = grozaSubscribeStoreService;
        this.grozaDupPublishMessageStoreService = grozaDupPublishMessageStoreService;
        this.grozaDupPubRelMessageStoreService = grozaDupPubRelMessageStoreService;
    }

    public void processDisConnect(Channel channel,MqttMessage msg){
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore sessionStore = grozaSessionStoreService.get(clientId);
        if (sessionStore!=null && sessionStore.isCleanSession()){
            grozaSubscribeStoreService.removeForClient(clientId);
            grozaDupPublishMessageStoreService.removeByClient(clientId);
            grozaDupPubRelMessageStoreService.removeByClient(clientId);
        }
        log.info("DISCONNECT - clientId: {}, cleanSession: {}", clientId, sessionStore.isCleanSession());
        grozaSessionStoreService.remove(clientId);
        channel.close();
    }

}
