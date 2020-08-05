package com.tin.activemq;

import org.apache.activemq.pool.PooledConnection;

import javax.jms.*;
import java.io.File;
import java.io.FileInputStream;

public class StartUpdateDevice implements Runnable {
    private String disname;
    private String moduleId;

    public StartUpdateDevice(String disname,String moduleId){
        this.disname = disname;
        this.moduleId = moduleId;
    }

    @Override
    public void run() {
        System.out.println("------run-------");
        sendMessage();
    }

    private void sendMessage() {
        try {
            PooledConnection connection = ActiveMQPoolsUtil.getConnection();
            TopicSession topicSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建一个点对点消息队列,队列名
            Destination queue = topicSession.createTopic(this.disname);
            //消息生产者
            MessageProducer messageProducer = topicSession.createProducer(queue);
            Message message = topicSession.createMessage();
            message.setStringProperty("moduleId",moduleId);
            message.setStringProperty("version","2.1.1");
            message.setStringProperty("type", "startUpload");
            messageProducer.send(message);

            System.out.println("发送消息成功！");
            // close
            messageProducer.close();
            //topicSession.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
