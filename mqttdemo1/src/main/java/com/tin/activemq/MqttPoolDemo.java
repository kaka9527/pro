package com.tin.activemq;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MqttPoolDemo {
    static MqttClientPool pool = new MqttClientPool(10);
    // 保证所有MqttClientRunner能够同时开始
    static CountDownLatch start = new CountDownLatch(1);
    // main线程将会等待所有MqttClientRunner结束后才能继续执行
    static CountDownLatch end;

    public static void main(String[] args) throws Exception {
// 线程数量，可以修改线程数量进行观察
        int threadCount = 10;
        end = new CountDownLatch(threadCount);
        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new MqttClientRunner(count, got, notGot),
                    "MqttClientRunnerThread");
            thread.start();
        }
        start.countDown();
        end.await();
        System.out.println("total invoke: " + (threadCount * count));
        System.out.println("got MqttClient: " + got);
        System.out.println("not got MqttClient " + notGot);
    }

    static class MqttClientRunner implements Runnable {
        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public MqttClientRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (Exception ex) {
            }
            while (count > 0) {
                try {
                    // 从线程池中获取连接，如果1000ms内无法获取到，将会返回null
                    // 分别统计连接获取的数量got和未获取到的数量notGot
                    MqttClient mqttClient = pool.fetchMqttClient(1000);
                    if (mqttClient != null) {
                        try {
                            mqttClient.connect();
                        } finally {
                            pool.releaseMqttClient(mqttClient);
                            got.incrementAndGet();
                        }
                    } else {
                        notGot.incrementAndGet();
                    }
                } catch (Exception ex) {
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }
}
