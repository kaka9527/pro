package com.tin.mqtt.type;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Publish {

    private static final Logger log = LoggerFactory.getLogger(Publish.class);
    // qos:0
//    private String msgType = "30";
    // qos:1
    private String msgType = "32";
    private String topicName;
    private String payLoad;

    public Publish(String topicName, String payLoad) {
        this.topicName = topicName;
        this.payLoad = payLoad;
    }

    public String getPublishMsg(){
        StringBuffer buffer = new StringBuffer();
        try {
            // topic
            String topic = Hex.encodeHexString(topicName.getBytes("UTF-8"), false);
            buffer.append("00");
            String topicStr = Integer.toHexString(topic.length() / 2);
            //log.info("pwdStr: "+pwdStr);
            buffer.append(topicStr.length() == 1 ? "0"+topicStr:topicStr);
            buffer.append(topic);

            // topic
            String pld = Hex.encodeHexString(payLoad.getBytes("UTF-8"), false);
            buffer.append("00");
            String pldStr = Integer.toHexString(pld.length() / 2);
            //log.info("pwdStr: "+pwdStr);
            buffer.append(pldStr.length() == 1 ? "0"+pldStr:pldStr);
            buffer.append(pld);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msgInfo = buffer.toString();
        String result = msgType+Integer.toHexString(msgInfo.length()/2)+msgInfo;
        log.info("publish result: "+result);
        return result;
    }
}
