package com.tin.it.thread;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.mqtt.UpdateFileMessage;
import com.tin.it.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SendBinDataThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SendBinDataThread.class);

    private String filePath;
    private String moduleId;
    private MqttMessageClient mqttMessageClient;

    public SendBinDataThread(MqttMessageClient mqttMessageClient, String filePath, String moduleId){
        this.filePath = filePath;
        this.moduleId = moduleId;
        this.mqttMessageClient = mqttMessageClient;
    }

    @Override
    public void run() {
        try{
            // 发送数据报文
            HashMap<String, String> dataMessage = UpdateFileMessage.getDataMessage(filePath, moduleId);
            sendDataMessage(dataMessage);
        }catch (Exception e) {
            logger.error(" error ",e);
        }

    }

    /**
     * 发送bin数据包
     * @param dataMessage
     */
    private void sendDataMessage(HashMap<String, String> dataMessage){
        Iterator<Map.Entry<String, String>> entries = dataMessage.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            try {
                String msg = entry.getValue();
                mqttMessageClient.sendMessage(Constants.UPDATE_TOPIC_NAME,msg);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                logger.error("key is "+entry.getKey() + " on error.",e);
                continue;
            }
        }
        //
        Constants.RETRY_STATE = true;
    }
}
