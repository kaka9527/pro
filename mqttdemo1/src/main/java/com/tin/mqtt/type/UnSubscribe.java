package com.tin.mqtt.type;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class UnSubscribe {

    private static final Logger log = LoggerFactory.getLogger(UnSubscribe.class);

    private String msgType = "A2";
    private String topicName;

    public UnSubscribe(String topicName) {
        this.topicName = topicName;
    }

    public String getUnSubscribeMsg(){
        StringBuffer buffer = new StringBuffer();
        try {
            buffer.append("0010");
            String tpcName = Hex.encodeHexString(topicName.getBytes("UTF-8"), false);
            buffer.append("00");
            String tpcStr = Integer.toHexString(tpcName.length() / 2);
            buffer.append(tpcStr.length() == 1 ? "0"+tpcStr:tpcStr);
            buffer.append(tpcName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msgInfo = buffer.toString();

        String result = msgType+Integer.toHexString(msgInfo.length()/2)+msgInfo;
        log.info("unsubscribe result: "+result);
        return result;
    }
}
