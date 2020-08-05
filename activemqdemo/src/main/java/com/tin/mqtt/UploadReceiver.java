package com.tin.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.tin.util.Constants;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 更新设备
 */
@Component
public class UploadReceiver {

    private static final Logger logger = LoggerFactory.getLogger(UploadReceiver.class);

    // 设备更新主题
    private static final String UPLOAD_TOPIC_NAME = "queue.chuangkou.upload";
    // 更新数据包主题
    private static final String UPDATE_TOPIC_NAME = "queue.chuangkou.update";
    //  消息属性
    private static final String BIN_FILE_NAME = "fileName";

    private String fileName = "";

    private final static int PACKET_SIZE = 2000;

    @Autowired
    JmsTemplate jmsTemplate;

    /**
     * topic模式的消费者 topicListener
     */
    @JmsListener(destination = UPLOAD_TOPIC_NAME,selector = "type='prepareUpload'",containerFactory="topicListener")
    public void receiveForUpload(Message message) {
        logger.info("receiveForUpload in.");
        // 接收更新bin文件
        OutputStream out = null;
        try {
            if (null != message && message instanceof BytesMessage) {
                //
                BytesMessage bytesMessage = (BytesMessage) message;
                // 文件名
                String fileName = bytesMessage.getStringProperty(BIN_FILE_NAME);
                //LOG.info("fileName=="+fileName);
                // 存放位置
                out = new FileOutputStream(Constants.BIN_FILE_DIR + fileName);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = bytesMessage.readBytes(bytes)) != -1) {
                    out.write(bytes, 0, len);
                }

                // 获得回执地址
                Destination callBackDest = message.getJMSReplyTo();
                //LOG.info("Destination=="+callBackDest.toString());
                // 创建回执消息
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code","1");
                jsonObject.put("msg","ok");

                // 以上收到消息之后，从新创建生产者，然后在回执过去
                jmsTemplate.convertAndSend(callBackDest,jsonObject.toJSONString());
                // 消息确认
                message.acknowledge();

                //LOG.info("One Receive OK!");
                // close
                out.flush();
                out.close();

                this.fileName = fileName;
            }
        } catch (Exception e) {
            logger.error("准备更新异常",e);
        }finally {
            if(null != out){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    logger.error("关闭流异常",e);
                }
            }
        }
        logger.info("receiveForUpload Finish.");
    }

    @JmsListener(destination = UPLOAD_TOPIC_NAME,selector = "type='startUpload'",containerFactory="topicListener")
    public void startUpdate(Message message) {
        logger.info("startUpdate start.");
        logger.info("fileName=="+this.fileName);
        if (null != message) {
            try {
                String moduleId = message.getStringProperty("moduleId");
                String version = message.getStringProperty("version");
                String filePath = Constants.BIN_FILE_DIR + "1595492436354.bin"; // this.fileName;
                logger.info(moduleId+":"+version);

                // 发送检查版本报文
                String versionMsg = UpdateFileMessage.getCheckVersionMessage(moduleId, version);
                boolean rt = sendCheckMessage(versionMsg,1);
                if(rt){
                    // 发送更新请求报文
                    String requestMessage = UpdateFileMessage.getUpdateRequestMessage(moduleId);
                    rt = sendCheckMessage(requestMessage,2);
                }
                if(rt){
                    // 发送数据报文
                    HashMap<String, String> dataMessage = UpdateFileMessage.getDataMessage(filePath, moduleId);
                    sendDataMessage(dataMessage);
                }
            } catch (Exception e) {
                logger.error("发送升级包异常",e);
            }
        }
        logger.info("startUpdate Finish.");
    }

    /**
     * 发送版本、请求数据包
     * @param msg
     */
    private boolean sendCheckMessage(String msg,int type){
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeBytes(msg.getBytes());
                // 返回消息
                ActiveMQTopic mqTopic = new ActiveMQTopic(UPDATE_TOPIC_NAME+".result");
                bytesMessage.setJMSReplyTo(mqTopic);
                return bytesMessage;
            }
        };
        Message replyMsg = jmsTemplate.sendAndReceive(UPDATE_TOPIC_NAME,messageCreator);
        boolean bool = false;
        try {
            byte[] body = replyMsg.getBody(byte[].class);
            logger.info("replyMsg=="+new String(body));
            bool = retryStatus(new String(body),type);
        } catch (Exception e) {
            logger.error("error 返回消息处理错误",e);
            return bool;
        }
        return bool;
    }

    /**
     * 发送更新数据包
     * @param dataMessage
     */
    private void sendDataMessage(HashMap<String, String> dataMessage){
        Iterator<Map.Entry<String, String>> entries = dataMessage.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            try {
                // 创建消息
                MessageCreator messageCreator = new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        BytesMessage bytesMessage = session.createBytesMessage();
                        // 获取数据报文
                        String msg = entry.getValue();
                        bytesMessage.writeBytes(msg.getBytes());
                        // 返回消息
                        ActiveMQTopic mqTopic = new ActiveMQTopic(UPDATE_TOPIC_NAME+".result");
                        bytesMessage.setJMSReplyTo(mqTopic);
                        return bytesMessage;
                    }
                };
                // 发送报文
                Message replyMsg = jmsTemplate.sendAndReceive(UPDATE_TOPIC_NAME,messageCreator);
                logger.info(replyMsg.toString());
                retrySendMessage(replyMsg);
            } catch (Exception e) {
                logger.error("key is "+entry.getKey() + " on error.",e);
                continue;
            }
        }
    }

    /**
     * 重发消息
     * @param replyMsg
     */
    private void retrySendMessage(Message replyMsg){
        // ACK：成功  NCK：重发
        if(null != replyMsg && replyMsg instanceof BytesMessage){
            try {
                BytesMessage message = (BytesMessage)replyMsg;
                byte[] bt = new byte[(int) message.getBodyLength()];
                String rtMsg = new String(bt);
                logger.info("rtMsg=="+rtMsg);
                boolean rt = retryStatus(rtMsg,3);
                if(rt){
                    // 重发bin数据包
                    String packetTotal = getRetryStr(rtMsg,12,16);
                    String packet = getRetryStr(rtMsg,16,20);
                    logger.info(packetTotal+"==bin=="+packet);
                }
            } catch (Exception e) {
                logger.error("bin 数据包重发错误",e);
            }
        }

    }

    /**
     * 判断返回消息
     */
    private boolean retryStatus(String rtMsg,int type){
        String rt0 = "";
        String rt1 = "";
        String ninetyOne = "91";
        String ninetyFour = "94";
        String one = "01";
        String six = "06";
        if(type == 1){
            rt0 = getRetryStr(rtMsg,0,2);
            rt1 = getRetryStr(rtMsg,4,6);
            if(ninetyFour.equals(rt0) && one.equals(rt1)){
                return true;
            }
        }
        if(type == 2){
            rt0 = getRetryStr(rtMsg,0,2);
            rt1 = getRetryStr(rtMsg,12,14);
            if(ninetyOne.equals(rt0) && one.equals(rt1)){
                return true;
            }
        }
        if(type == 3){
            rt0 = getRetryStr(rtMsg,0,2);
            rt1 = getRetryStr(rtMsg,20,22);
            if(ninetyOne.equals(rt0) && six.equals(rt1)){
                return true;
            }
        }
        return false;
    }

    private String getRetryStr(String rtMsg,int startIndex,int endIndex){
        String msg = rtMsg;
        if(null != rtMsg && rtMsg.length() > 16){
        // 68 880000100200 68 94 01 01 00 16
//        01：可以更新
//        00：不更新
            msg = rtMsg.substring(16);
            msg = msg.substring(startIndex,endIndex);

            // 68 010011000100 68 91 04 423733375016
//        01准备好
//        00没有准备好

        // 68 010011000100 68 91 07	42373337 0160 0001 15 50 16
        /**
         * 接收成功ACK=0X15
         * 接收失败NCK=0X06
         */
        }
        return msg;
    }
}
