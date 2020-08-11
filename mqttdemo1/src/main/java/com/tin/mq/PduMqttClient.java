package com.tin.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;

public class PduMqttClient {
    private static final String url = "tcp://192.168.10.80:1883";

    private static final String queueName = "queue.chuangkou.updateResult";


    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = null;
        ActiveMQConnection connection = null;
        try {
            // 1、创建连接工厂
            connectionFactory = new ActiveMQConnectionFactory(url);

            // 2、创建连接对象
            connection = (ActiveMQConnection)connectionFactory.createConnection();
            // 3、启动连接
            connection.start();
            // 4、创建会话
            ActiveMQSession session = (ActiveMQSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            ActiveMQTopic topic = (ActiveMQTopic)session.createTopic(queueName);

            MessageConsumer consumer = session.createConsumer(topic, " ClientID='ckMqttClientId_result'");
            Message message = consumer.receive();
            System.out.println("msg==="+message.toString());

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != connection){
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
