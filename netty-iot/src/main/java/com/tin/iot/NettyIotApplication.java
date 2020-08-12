package com.tin.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tin.iot"})
public class NettyIotApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyIotApplication.class, args);
    }
}
