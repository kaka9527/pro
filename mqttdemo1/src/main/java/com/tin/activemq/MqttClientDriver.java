package com.tin.activemq;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

public class MqttClientDriver {
    static class MqttClientHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals("commit")){
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

    /**
     * 创建一个MqttClient的代理，在commit时休眠100毫秒
     * @return
     */
    public static final MqttClient createMqttClient() {
        return (MqttClient) Proxy.newProxyInstance(MqttClientDriver.class.getClassLoader(),
                new Class[]{MqttClient.class}, new MqttClientHandler());
    }
}
