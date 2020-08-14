package com.tin.mqtt.type;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * SUBSCRIBE连接处理
 */
public class Subscribe {
    private static final Logger log = LoggerFactory.getLogger(Subscribe.class);

    private String msgType = "82";
    private String clientId;
    private String topicName;
    private int qos;

    public Subscribe(String clientId,String topicName, int qos) {
        this.clientId = clientId;
        this.topicName = topicName;
        this.qos = qos;
    }

    public String getSubscribeMsg(){
        StringBuffer buffer = new StringBuffer();

        try {
            // clientId
//            String cltId = Hex.encodeHexString(clientId.getBytes("UTF-8"), false);
            buffer.append("0010");
//            String cltStr = Integer.toHexString(cltId.length() / 2);
//            //log.info("cltStr: "+cltStr);
//            buffer.append(cltStr.length() == 1 ? "0"+cltStr:cltStr);
//            buffer.append(cltId);

            // topic
            String topic = Hex.encodeHexString(topicName.getBytes("UTF-8"), false);
            buffer.append("00");
            //log.info("topic: "+topic);
            //log.info("topic: "+topic.length());
            //log.info("len: "+(topic.length() / 2));
            String topicStr = Integer.toHexString((topic.length() / 2));
            buffer.append(topicStr.length() == 1 ? "0"+topicStr:topicStr);
            buffer.append(topic);
            if(qos == 0){
                buffer.append("00");
            }else if(qos == 1){
                buffer.append("01");
            }else {
                buffer.append("02");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String msgInfo = buffer.toString();
        String result = msgType+Integer.toHexString(msgInfo.length()/2)+msgInfo;
        log.info("subscribe result: "+result);
        return result;
    }
}
