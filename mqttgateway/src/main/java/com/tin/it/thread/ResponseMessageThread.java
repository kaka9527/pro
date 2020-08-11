package com.tin.it.thread;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.mqtt.UpdateFileMessage;
import com.tin.it.mqtt.UpdateMessageUtil;
import com.tin.it.util.Constants;
import com.tin.it.util.PacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理设备返回消息
 */
public class ResponseMessageThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DataMessageThread.class);
    private String message;

    private final static int PACKET_SIZE = 2000;

    private ConcurrentHashMap<String, byte[]> binData = getBinData();

    @Resource
    MqttMessageClient mqttMessageClient;

    public ResponseMessageThread(String message){
        this.message = message;
    }
    @Override
    public void run() {
        logger.info("-------retry start--------");
        boolean bool = UpdateMessageUtil.retryStatus(this.message,1);
        boolean state = UpdateMessageUtil.retryStatus(this.message, 2);
        boolean rt = UpdateMessageUtil.retryStatus(this.message,3);
        if(bool || state || rt){
            SleepUtils.second(5);
        }
        if(rt){
            // 发送重复消息
            sendRetryMessage(message);
        }
        logger.info("-------retry end--------");
    }

    private void sendRetryMessage(String msg){
        // 重发bin数据包
        String packetTotal = UpdateMessageUtil.getRetryStr(this.message,12,16);
        String packet = UpdateMessageUtil.getRetryStr(this.message,16,20);
        String key = packetTotal + "_" + packet;
        String deviceCode = PacketUtil.getDeviceCode(msg);
        logger.info(packetTotal+"==bin=="+packet);
        HashMap<String, String> dataMessage = UpdateFileMessage.getDataMessage(binData, deviceCode);
        if(dataMessage.containsKey(key)){
            String binMsg = dataMessage.get(key);
            mqttMessageClient.sendMessage(Constants.TOPIC_GATEWAY_REQUEST,binMsg);
            logger.info(" sendRetryMessage finish. ");
        }
    }

    /**
     * 获取bin文件数据
     * @return
     */
    private ConcurrentHashMap<String,byte[]> getBinData(){
        String filePath = Constants.BIN_FILE_DIR + "Project.bin";
        File file = new File(filePath);
        ConcurrentHashMap<String,byte[]> binBytes = null;
        if(file.exists() && file.isFile()){
            FileInputStream fis = null;
            DataInputStream dis = null;
            try {
                fis = new FileInputStream(file);
                dis = new DataInputStream(fis);
                // 文件大小
                int len = (int)file.length();

                // 计算总包数
                int y = len / PACKET_SIZE;
                int s = len % PACKET_SIZE;
                if(s > 0 && s < PACKET_SIZE){
                    y = y + 1;
                }
                logger.info("包数："+ y);

                // 数据包
                binBytes = new ConcurrentHashMap<>(y);
                byte [] bytes = new byte[PACKET_SIZE];
                int n = 0;
                int x = 0;
                while ((n = dis.read(bytes)) != -1){
                    x = x + 1;
                    String key = Integer.toHexString(y) + "_"+Integer.toHexString(x);
                    binBytes.put(key,bytes);
                }
                // close
                dis.close();
                fis.close();
            } catch (Exception e) {
                logger.error("读取bin文件异常",e);
            } finally {
                try {
                    if (null != dis) {
                        dis.close();
                    }
                } catch (IOException e) {
                    logger.error("关闭流异常1",e);
                }
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException e) {
                    logger.error("关闭流异常2",e);
                }
            }
        }
        return binBytes;
    }
}
