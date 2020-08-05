package com.tin.it.service.impl;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.mqtt.UpdateFileMessage;
import com.tin.it.service.UpdateDeviceProducerService;
import com.tin.it.thread.SendBinDataThread;
import com.tin.it.util.Constants;
import com.tin.it.vo.MessageInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Service
public class UpdateDeviceProducerServiceImpl implements UpdateDeviceProducerService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateDeviceProducerServiceImpl.class);
    //  消息属性
    private static final String BIN_FILE_NAME = "fileName";

    private String fileName = "";

    ExecutorService singleThreadExecutor = new ThreadPoolExecutor(1,1,0, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    @Resource
    private MqttMessageClient mqttMessageClient;

    /**
     * 预留上传文件接口
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {
        return null;
    }

    @Override
    public void startUpdate(MessageInfoVO msg) {
        logger.info("startUpdate start.");
        logger.info("fileName=="+this.fileName);
        if (null != msg) {
            try {
                String moduleId = msg.getModuleId();
                String version = "2.1.1"; //msg.getMsg();
                String filePath = Constants.BIN_FILE_DIR + "1595492436354.bin"; // this.fileName;
                logger.info(moduleId+":"+version);
                String msgType = msg.getMessageType() == null ? "": msg.getMessageType();
                if(Constants.MSG_TYPE_CHECK.equals(msgType)){
                    sendCheckMessage(moduleId,version,1);
                }
                if(Constants.MSG_TYPE_REQUEST.equals(msgType)){
                    sendCheckMessage(moduleId,version,2);
                }
                if(Constants.MSG_TYPE_UPDATE.equals(msgType)){
                    // 发送数据报文
                    singleThreadExecutor.execute(new SendBinDataThread(mqttMessageClient,filePath,moduleId));
                }

            } catch (Exception e) {
                logger.error("发送升级包异常",e);
            }
        }
        logger.info("startUpdate Finish.");
    }

    /**
     * 发送版本、请求数据包
     */
    private boolean sendCheckMessage(String moduleId, String version,int type){
        try {
            // 发送检查版本报文
            if(type == 1){
                String versionMsg = UpdateFileMessage.getCheckVersionMessage(moduleId, version);
                mqttMessageClient.sendMessage(Constants.UPDATE_TOPIC_NAME,versionMsg);
                Constants.RETRY_STATE = true;
            }
            if(type == 2){
                // 发送更新请求报文
                String requestMessage = UpdateFileMessage.getUpdateRequestMessage(moduleId);
                mqttMessageClient.sendMessage(Constants.UPDATE_TOPIC_NAME,requestMessage);
                Constants.RETRY_STATE = true;
            }
        } catch (Exception e) {
            logger.error("sendCheckMessage error ",e);
            return false;
        }
        return true;
    }

    /**
     * 发送更新数据包
     * @param dataMessage
     */
    /*private void sendDataMessage(HashMap<String, String> dataMessage){
        Iterator<Map.Entry<String, String>> entries = dataMessage.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            try {
                String msg = entry.getValue();
                mqttMessageClient.sendMessage(Constants.UPDATE_TOPIC_NAME,msg);
                TimeUnit.SECONDS.sleep(100);
            } catch (Exception e) {
                logger.error("key is "+entry.getKey() + " on error.",e);
                continue;
            }
        }
        Constants.RETRY_STATE = true;
    }*/
}
