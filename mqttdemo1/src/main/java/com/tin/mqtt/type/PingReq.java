package com.tin.mqtt.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class PingReq {

    private static final Logger log = LoggerFactory.getLogger(PingReq.class);

    public String getPingReqMsg(){
        return "C000";
    }
}
