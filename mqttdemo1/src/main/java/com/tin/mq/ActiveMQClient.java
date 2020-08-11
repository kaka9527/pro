package com.tin.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQTopic;


import javax.jms.*;

public class ActiveMQClient {
    private static final String url = "tcp://192.168.10.80:61616";

    private static final String queueName = "queue.chuangkou.updateResult";


    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = null;
        ActiveMQConnection connection = null;
        try {
            // 1、创建连接工厂
            connectionFactory = new ActiveMQConnectionFactory(url);
            //connectionFactory.setUserName("chuangkou");
            //connectionFactory.setPassword("chuangkou88");
            // 2、创建连接对象
            connection = (ActiveMQConnection)connectionFactory.createConnection();
            // 3、启动连接
            connection.start();
            // 4、创建会话
            ActiveMQSession session = (ActiveMQSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            ActiveMQTopic topic = (ActiveMQTopic)session.createTopic(queueName);

            MessageProducer producer = session.createProducer(topic);
            TopicPublisher publisher = session.createPublisher(topic);

            BytesMessage bytesMessage = session.createBytesMessage();
//            MqttMessage mqttMessage = new MqttMessage();
//            mqttMessage.setPayload("test 1111111".getBytes());
//            mqttMessage.setQos(1);
//            mqttMessage.setRetained(true);
            //byte[] bytes = "test 1111111".getBytes();
            //bytesMessage.writeBytes(bytes);

            TextMessage textMessage = session.createTextMessage();
            textMessage.setText("{'aa':'bb','cc':111}");
            producer.send(textMessage);
            //publisher.publish(bytesMessage);
            //publisher.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != connection){
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
