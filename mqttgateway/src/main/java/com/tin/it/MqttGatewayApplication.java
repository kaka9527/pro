package com.tin.it;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqttGatewayApplication {
    /**
     * 消息服务器功能：
     * 1.对外提供接口
     * 2.消息转换
     * 3.发布、消费消息
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(MqttGatewayApplication.class,args);
    }

}
