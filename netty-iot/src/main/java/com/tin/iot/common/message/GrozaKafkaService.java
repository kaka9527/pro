package com.tin.iot.common.message;

import com.tin.iot.internal.InternalMessage;

/**
 * 消息转发,基于kafka
 * @author james
 */
public interface GrozaKafkaService {
    void send(InternalMessage internalMessage);
}
