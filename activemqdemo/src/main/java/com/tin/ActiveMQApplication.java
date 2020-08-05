package com.tin;

import org.apache.activemq.xbean.BrokerFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EnableJms
public class ActiveMQApplication {
    /**
     * 消息服务器功能：
     * 1.对外提供rest接口
     * 2.消息转换 （接口 -- 设备，设备 -- 接口）
     * 3.发布、消费消息（接口 -- 消息转换 -- 发布消息 -- 设备 -- 消费消息，
     *      设备 -- 指令 -- 消费消息 -- 消息转换 -- 消息回执）
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ActiveMQApplication.class,args);
    }

    @Bean
    public BrokerFactoryBean activemq() throws Exception {
        BrokerFactoryBean broker = new BrokerFactoryBean();
        broker.setConfig(new ClassPathResource("activemq.xml"));
        broker.setStart(true);
        return broker;
    }

    /*@Bean
    private static void test(){
        //TcpTransport transport = new TcpTransport();
        //MQTTCodec mqttCodec = new MQTTCodec("tcp://192.168.10.80:1883");

        PduMQTTClient.MQTT_HOST = "tcp://192.168.10.80:1883";
        PduMQTTClient.MQTT_CLIENTID = "clientId";
        PduMQTTClient.MQTT_USERNAME = "username";
        PduMQTTClient.MQTT_PASSWORD = "password";
        PduMQTTClient client = PduMQTTClient.getInstance();
        client.subscribe("/#");
    }*/

    /*@Bean
    public CallbackConnection mqttClient() throws Exception{
        MQTT mqtt = new MQTT();
        mqtt.setHost("localhost",1883);
        mqtt.setClientId("server11");
        mqtt.setVersion("3.1.1");
        CallbackConnection callbackConnection = mqtt.callbackConnection();
        return callbackConnection;
    }*/

    /*@Bean
    public BrokerFactoryBean activemq2() throws Exception {
        BrokerFactoryBean broker = new BrokerFactoryBean();
        broker.setConfig(new ClassPathResource("activemq2.xml"));
        broker.setStart(true);
        return broker;
    }*/

    // 在Queue模式中，对消息的监听需要对containerFactory进行配置
    /*@Bean("queueListener")
    public JmsListenerContainerFactory<?> queueJmsListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(false);
        return factory;
    }*/

    //在Topic模式中，对消息的监听需要对containerFactory进行配置
    @Bean("topicListener")
    public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }
}
