package com.tin.fuse;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class MQTTFutureClient {

    private static final Logger logger = LoggerFactory.getLogger(MQTTFutureClient.class);

    private final static String CONNECTION_STRING = "tcp://192.168.10.80:1883";
    private final static boolean CLEAN_START = true;
    // 低耗网络，但是又需要及时获取数据，心跳30s
    private final static String CLIENT_ID = "client8899";
    private final static short KEEP_ALIVE = 30;
    private final static String DEVICE_ID = "000100000035";
    public static Topic[] topics = {
            new Topic("mqtt/aaa", QoS.EXACTLY_ONCE),
            new Topic("mqtt/bbb", QoS.AT_LEAST_ONCE),
            new Topic("mqtt/ccc", QoS.AT_MOST_ONCE) };

    public final static long RECONNECTION_ATTEMPT_MAX = 6;
    public final static long RECONNECTION_DELAY = 2000;

    // 发送最大缓冲为2M
    public final static int SEND_BUFFER_SIZE = 2 * 1024 * 1024;

    public static void main(String[] args) {
        // 创建MQTT对象
        MQTT mqtt = new MQTT();
        //BlockingConnection connection = null;
        //FutureConnection connection = null;
//        CallbackConnection connection = null;
        try {
            // 设置mqtt broker的ip和端口
            mqtt.setHost(CONNECTION_STRING);
            // 连接前清空会话信息
            mqtt.setCleanSession(CLEAN_START);
            // 设置重新连接的次数
            mqtt.setReconnectAttemptsMax(RECONNECTION_ATTEMPT_MAX);
            // 设置重连的间隔时间
            mqtt.setReconnectDelay(RECONNECTION_DELAY);
            // 设置心跳时间
            mqtt.setKeepAlive(KEEP_ALIVE);
            // 设置缓冲的大小
            //mqtt.setSendBufferSize(SEND_BUFFER_SIZE);
            //服务器认证用户名
            mqtt.setUserName("chuangkou");
            //服务器认证密码
            mqtt.setPassword("chuangkou88");
            //设置客户端id
            mqtt.setClientId(CLIENT_ID);
            // mqtt版本
            mqtt.setVersion("3.3.1");

            /*//获取mqtt的连接对象BlockingConnection
            connection = mqtt.blockingConnection();
            //MQTT连接的创建
            connection.connect();
            //创建相关的MQTT 的主题列表
            Topic[] topics = {new Topic("mqtt/#", QoS.AT_LEAST_ONCE)};
            //订阅相关的主题信息
            byte[] qoses = connection.subscribe(topics);
            logger.info(String.valueOf(qoses));
            //
            while(true){
                //接收订阅的消息内容
                Message message = connection.receive();
                //获取订阅的消息内容
                byte[] payload = message.getPayload();
                // process the message then:
                logger.info("MQTTClient Message  Topic="+message.getTopic()+" Content :"+new String(payload));
                //签收消息的回执
                message.ack();

                Thread.sleep(2000);
            }*/

            //
            /*connection = mqtt.futureConnection();
            connection.connect();
            connection.subscribe(topics);
            int count = 0;
            while (true) {
                count++;
                Future<Message> futrueMessage = connection.receive();
                //Message message = futrueMessage.await(1000, TimeUnit.NANOSECONDS);
                Message message = futrueMessage.await();
                logger.info("MQTTFutureClient.Receive Message " + "Topic Title :" + message.getTopic() + " context :"
                        + String.valueOf(message.getPayloadBuffer()));
            }*/

            // callconnection
            final CallbackConnection connection = mqtt.callbackConnection();
            connection.listener(new ExtendedListener() {
                @Override
                public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Callback<Callback<Void>> callback) {
                    logger.info(" onPublish 1 ");
                    // 处理来自topic的消息
                    //logger.info("receive topic:{} and message:{}", utf8Buffer.toString(), new String(buffer.getData()));
                }

                @Override
                public void onConnected() {
                    logger.info(" onConnected ");
                }

                @Override
                public void onDisconnected() {
                    logger.info(" onDisconnected ");
                }

                @Override
                public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Runnable runnable) {
                    logger.info(" onPublish 2");
                    // 处理来自topic的消息
                    //logger.info("receive topic:{} and message:{}", utf8Buffer.toString(), new String(buffer.getData()));
                    runnable.run();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    logger.info(" onFailure ");
                }
            });
            connection.connect(new Callback<Void>() {
                /**
                 * 连接成功时执行的方法
                 * @param aVoid
                 */
                @Override
                public void onSuccess(Void aVoid) {
                    //Topic[] topics = {new Topic(, QoS.AT_LEAST_ONCE)};
                    connection.subscribe(topics, new Callback<byte[]>() {
                        @Override
                        public void onSuccess(byte[] qoses) {
                        }
                        @Override
                        public void onFailure(Throwable value) {
                            value.printStackTrace();
                            System.exit(-2);
                        }
                    });
                }
                @Override
                public void onFailure(Throwable value) {
                    value.printStackTrace();
                    System.exit(-2);
                }
            });

            // Wait forever..
            synchronized (MQTTFutureClient.class) {
                while(true){
                    MQTTFutureClient.class.wait();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            /*if(connection != null){
                try {
                    connection.disconnect(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            logger.info("--------disconnect success-----------");
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            logger.info("--------disconnect failure-----------");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

}
