package com.tin.it.config;

import com.tin.it.mqtt.handler.DataMessageListener;
import com.tin.it.util.Constants;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

import javax.annotation.Resource;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MqttResponseConfiguration.class);

    @Resource
    private MqttProperties mqttProperties;

    public MqttConnectOptions mqttConnectOptions() {

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        String username = mqttProperties.getUsername();
        String password = mqttProperties.getPassword();
        if(username != null) {
            connectOptions.setUserName(username);
        }
        if (password != null) {
            connectOptions.setPassword(password.toCharArray());
        }
        String[] serverURIs = mqttProperties.getServerURIs();
        if (serverURIs == null || serverURIs.length == 0) {
            logger.error("serverURIs can not be null",new NullPointerException());
        }

        Properties sslClientProps = new Properties();
//        sslClientProps.setProperty(SSLSocketFactoryFactory.SSLPROTOCOL, mqttProperties.getProtocol());
//        sslClientProps.setProperty(SSLSocketFactoryFactory.KEYSTORE, mqttProperties.getKeyStore());
//        sslClientProps.setProperty(SSLSocketFactoryFactory.KEYSTOREPWD, mqttProperties.getKeyStorePassword());
//        sslClientProps.setProperty(SSLSocketFactoryFactory.KEYSTORETYPE, mqttProperties.getKeyStoreType());
//        sslClientProps.setProperty(SSLSocketFactoryFactory.TRUSTSTORE, mqttProperties.getTrustStore());
//        sslClientProps.setProperty(SSLSocketFactoryFactory.TRUSTSTOREPWD, mqttProperties.getTrustStorePassword());
//        sslClientProps.setProperty(SSLSocketFactoryFactory.TRUSTSTORETYPE, mqttProperties.getTrustStoreType());
        connectOptions.setSSLProperties(sslClientProps);

        connectOptions.setCleanSession(mqttProperties.getCleanSession());
        connectOptions.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        connectOptions.setServerURIs(serverURIs);
        connectOptions.setMaxInflight(1000);

        return connectOptions;
    }

    @Bean("TestMqttClient")
    public MqttClient mqttClient(){
        MemoryPersistence persistence = new MemoryPersistence();
        String[] serverURIs = mqttProperties.getServerURIs();
        MqttClient mqttClient = null;
        try {
            mqttClient = new MqttClient(serverURIs[0],mqttProperties.getClientId()+"_mqttClient",persistence);
            mqttClient.connect(mqttConnectOptions());
            mqttClient.subscribe(Constants.TOPIC_PLATFORM_REQUEST,new DataMessageListener());
        } catch (Exception e) {
            logger.error(" mqttClient error: ",e);
        }
        return mqttClient;
    }
}
