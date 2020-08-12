package com.tin.iot.store.message;

import com.tin.iot.common.message.GrozaMessageIdService;
import org.springframework.stereotype.Service;

@Service
public class MessageIdServiceImpl implements GrozaMessageIdService {
    @Override
    public int getNextMessageId() {
        return 0;
    }
}
