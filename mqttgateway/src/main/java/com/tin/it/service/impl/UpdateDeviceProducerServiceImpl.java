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
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.*;

import static com.tin.it.util.Constants.BIN_FILE_NAME;

@Service
public class UpdateDeviceProducerServiceImpl implements UpdateDeviceProducerService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateDeviceProducerServiceImpl.class);
    //  消息属性
    //private static final String BIN_FILE_NAME = "fileName";

    //private String fileName = "";

    ExecutorService singleThreadExecutor = new ThreadPoolExecutor(1,1,0, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    @Resource
    private MqttMessageClient mqttMessageClient;

    private final static int PACKET_SIZE = 2000;

    /**
     * bin 数据包
     */
    private ConcurrentHashMap<String, byte[]> binData;

    /**
     * 预留上传文件接口
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {
        //将文件缓冲到本地
        boolean localFile = createLocalFile(Constants.BIN_FILE_DIR, file);
        if(!localFile){
            logger.error("Create local file failed!");
            return "error";
        }
        logger.info("Create local file successfully");
        //
        String filePath = Constants.BIN_FILE_DIR + Constants.BIN_FILE_NAME;
        binData = getBinData(filePath);
        return "ok";
    }

    @Override
    public void startUpdate(MessageInfoVO msg) {
        logger.info("startUpdate start.");
        if (null != msg) {
            try {
                String moduleId = msg.getModuleId();
                String version = "2.1.1"; //msg.getMsg();

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
                    singleThreadExecutor.execute(new SendBinDataThread(mqttMessageClient,binData,moduleId));
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
            logger.error(" sendCheckMessage error ",e);
            return false;
        }
        return true;
    }

    /**
     * 获取bin文件数据
     * @param filePath
     * @return
     */
    private ConcurrentHashMap<String, byte[]> getBinData(String filePath){
        File file = new File(filePath);
        if(null != binData && !binData.isEmpty()){
            binData.clear();
        }
        ConcurrentHashMap<String, byte[]> binBytes = null;
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
                int n = -1;
                int x = 0;
                while ((n = dis.read(bytes)) != -1){
                    x = x + 1;
                    String key = Integer.toHexString(y) + "_" + Integer.toHexString(x);
                    binBytes.put(key,bytes);
                }
                // close
                dis.close();
                fis.close();
            } catch (Exception e) {
                logger.error(" 读取bin文件异常 ",e);
            } finally {
                try {
                    if (null != dis) {
                        dis.close();
                    }
                } catch (IOException e) {
                    logger.error(" 关闭流异常1 ",e);
                }
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException e) {
                    logger.error(" 关闭流异常2 ",e);
                }
            }
        }
        return binBytes;
    }

    /**
     * 通过上传的文件名，缓冲到本地，后面才能解压、验证
     * @param filePath 临时缓冲到本地的目录
     * @param file
     */
    private boolean createLocalFile(String filePath,MultipartFile file) {
        File localFile = new File(filePath);
        //先创建目录
        localFile.mkdirs();

        String fileName = Constants.BIN_FILE_NAME;
        String path = filePath + fileName;

        logger.info("createLocalFile path = {}", path);

        localFile = new File(path);
        FileOutputStream fos = null;
        InputStream in = null;
        try {

            if(localFile.exists()){
                //如果文件存在删除文件
                boolean delete = localFile.delete();
                if (delete == false){
                    logger.error("Delete exist file \"{}\" failed!!!",path,new Exception("Delete exist file \""+path+"\" failed!!!"));
                }
            }
            //创建文件
            if(!localFile.exists()){
                //如果文件不存在，则创建新的文件
                localFile.createNewFile();
                logger.info("Create file successfully,the file is {}",path);
            }

            //创建文件成功后，写入内容到文件里
            fos = new FileOutputStream(localFile);
            in = file.getInputStream();
            byte[] bytes = new byte[1024];
            int len = -1;
            while((len = in.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }

            fos.flush();
            logger.info("Reading uploaded file and buffering to local successfully!");
        } catch (Exception e) {
            logger.error(" error ",e);
            return false;
        } finally {
            try {
                if(fos != null) {
                    fos.close();
                }
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("InputStream or OutputStream close error : {}", e);
                return false;
            }
        }

        return true;
    }
}
