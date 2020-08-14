package com.tin.iot.store.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tin.iot.common.message.GrozaKafkaService;
import com.tin.iot.internal.InternalMessage;
import com.tin.iot.protocol.Connect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author james
 * @description kafka消息生产者处理类
 */
@Service
public class KafkaServiceImpl implements GrozaKafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private static Gson gson = new GsonBuilder().create();

    @Override
    public void send(InternalMessage internalMessage){
        log.info("send: "+internalMessage);
        //kafkaTemplate.send(internalMessage.getTopic(),gson.toJson(internalMessage));
    }
}
