package com.tin.mqtt.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @DISCONNECT 连接处理
 */
public class DisConnect {

    private static final Logger log = LoggerFactory.getLogger(DisConnect.class);

    public String getDisConnectMsg(){
        return "E000";
    }

}
