package com.tin.fuse;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.mqtt.client.*;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class MQTTFutureServer {

    private static final Logger logger = LoggerFactory.getLogger(MQTTFutureServer.class);

    private final static String CONNECTION_STRING = "tcp://192.168.10.200:1883";
    private final static boolean CLEAN_START = true;
    private final static String CLIENT_ID = "server8899";
    private final static short KEEP_ALIVE = 30;// 低耗网络，但是又需要及时获取数据，心跳30s

    public  static Topic[] topics = {
            new Topic("mq/aaa", QoS.EXACTLY_ONCE),
            new Topic("mq/bbb", QoS.AT_LEAST_ONCE),
            new Topic("mq/ccc", QoS.AT_MOST_ONCE)};

    public final  static long RECONNECTION_ATTEMPT_MAX=6;
    public final  static long RECONNECTION_DELAY=2000;

    public final static int SEND_BUFFER_SIZE=2*1024*1024;//发送最大缓冲为2M


    public static void main(String[] args)   {
        MQTT mqtt = new MQTT();
        //CallbackConnection connection = null;
//        BlockingConnection connection = null;
        try {
            //==MQTT设置说明
            //设置服务端的ip
            mqtt.setHost(CONNECTION_STRING);
            //连接前清空会话信息 ,若设为false，MQTT服务器将持久化客户端会话的主体订阅和ACK位置，默认为true
            mqtt.setCleanSession(CLEAN_START);
            //设置心跳时间 ,定义客户端传来消息的最大时间间隔秒数，服务器可以据此判断与客户端的连接是否已经断开，从而避免TCP/IP超时的长时间等待
            mqtt.setKeepAlive(KEEP_ALIVE);
            //设置客户端id,用于设置客户端会话的ID。在setCleanSession(false);被调用时，MQTT服务器利用该ID获得相应的会话。
            //此ID应少于23个字符，默认根据本机地址、端口和时间自动生成
            mqtt.setClientId(CLIENT_ID);
            //服务器认证用户名
            mqtt.setUserName("chuangkou");
            //服务器认证密码
            mqtt.setPassword("chuangkou88");

            mqtt.setVersion("3.1.1");

 			 /*
 			 //设置“遗嘱”消息的内容，默认是长度为零的消息
 			 mq.setWillMessage("willMessage");
 			 //设置“遗嘱”消息的QoS，默认为QoS.ATMOSTONCE
 			 mq.setWillQos(QoS.AT_LEAST_ONCE);
 			 //若想要在发布“遗嘱”消息时拥有retain选项，则为true
 			 mq.setWillRetain(true);
 			 //设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
 			 mq.setWillTopic("willTopic");
 			 */

            //==失败重连接设置说明
            //设置重新连接的次数 ,客户端已经连接到服务器，但因某种原因连接断开时的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
            mqtt.setReconnectAttemptsMax(RECONNECTION_ATTEMPT_MAX);
            //设置重连的间隔时间  ,首次重连接间隔毫秒数，默认为10ms
            mqtt.setReconnectDelay(RECONNECTION_DELAY);
            //客户端首次连接到服务器时，连接的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
            //mq.setConnectAttemptsMax(10L);
            //重连接间隔毫秒数，默认为30000ms
            //mq.setReconnectDelayMax(30000L);
            //设置重连接指数回归。设置为1则停用指数回归，默认为2
            //mq.setReconnectBackOffMultiplier(2);

            //== Socket设置说明
            //设置socket接收缓冲区大小，默认为65536（64k）
            //mq.setReceiveBufferSize(65536);
            //设置socket发送缓冲区大小，默认为65536（64k）
            mqtt.setSendBufferSize(SEND_BUFFER_SIZE);
            ////设置发送数据包头的流量类型或服务类型字段，默认为8，意为吞吐量最大化传输
            mqtt.setTrafficClass(8);

            //==带宽限制设置说明
            //设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制
            mqtt.setMaxReadRate(0);
            //设置连接的最大发送速率，单位为bytes/s。默认为0，即无限制
            mqtt.setMaxWriteRate(0);

            //==选择消息分发队列
            //若没有调用方法setDispatchQueue，客户端将为连接新建一个队列。如果想实现多个连接使用公用的队列，显式地指定队列是一个非常方便的实现方法
            //mq.setDispatchQueue(Dispatch.createQueue("mq/aaa"));

             //==设置跟踪器
             mqtt.setTracer(new Tracer(){
                 @Override
                 public void onReceive(MQTTFrame frame) {
                     logger.info("recv: "+frame);
                 }
                 @Override
                 public void onSend(MQTTFrame frame) {
                     logger.info("send: "+frame);
                 }
                 @Override
                 public void debug(String message, Object... args) {
                     logger.info(String.format("debug: "+message, args));
                 }
             });

            /*connection = mq.blockingConnection();
            connection.connect();
            int count=0;
            while(true){
                count++;
                //订阅的主题
                String topic="mq/bbb";
                //主题的内容
                String message="hello "+count+"chinese people !";
                connection.publish(topic, message.getBytes(), QoS.AT_LEAST_ONCE, false);
                logger.info("MQTTServer Message  Topic="+topic+"  Content :"+message);
                Thread.sleep(2000);
            }*/

            //使用Future创建连接
            /*final FutureConnection connection= mq.futureConnection();
            connection.connect();
            int count=1;
            while(count < 10){
                count++;
                // 用于发布消息，目前手机段不需要向服务端发送消息
                //主题的内容
                String message="Hello "+count+" MQTT...";
                String topic = "mq/bbb";
                connection.publish(topic, message.getBytes(), QoS.AT_LEAST_ONCE,
                        false);
                logger.info("MQTTFutureServer.publish Message "+"Topic Title :"+topic+" context :"+message);

            }*/

            //使用回调式API
              final CallbackConnection callbackConnection=mqtt.callbackConnection();
            /*//连接监听
            callbackConnection.listener(new Listener() {
             //接收订阅话题发布的消息
        	@Override
			public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
				logger.info("=============receive msg================"+new String(body.toByteArray()));
				ack.run();
			}
             //连接失败
             @Override
             public void onFailure(Throwable value) {
              logger.info("===========connect failure===========");
              callbackConnection.disconnect(null);
             }
                //连接断开
             @Override
             public void onDisconnected() {
              logger.info("====mq disconnected=====");
             }
             //连接成功
             @Override
             public void onConnected() {
              logger.info("====mq connected=====");
             }
            });*/
             //连接
             callbackConnection.connect(new Callback<Void>() {
               //连接失败
                 @Override
                 public void onFailure(Throwable value) {
                     logger.info("============连接失败："+value.getLocalizedMessage()+"============");
                 }
                 // 连接成功
                 @Override
                 public void onSuccess(Void v) {
                     logger.info(" onSuccess ");
                     /*//订阅主题
                     Topic[] topics = {new Topic("mq/bbb", QoS.AT_LEAST_ONCE)};
                     callbackConnection.subscribe(topics, new Callback<byte[]>() {
                         //订阅主题成功
                         @Override
                      public void onSuccess(byte[] qoses) {
                             logger.info("========订阅成功=======");
                         }
                      //订阅主题失败
                         @Override
                         public void onFailure(Throwable value) {
                          logger.info("========订阅失败=======");
                          callbackConnection.disconnect(null);
                         }
                     });*/
                     while (true){
                         //发布消息
                         callbackConnection.publish("mq/bbb", (" test Hello ").getBytes(), QoS.AT_LEAST_ONCE, true, new Callback<Void>() {
                             @Override
                             public void onSuccess(Void v) {
                                 logger.info("===========消息发布成功============");
                             }
                             @Override
                             public void onFailure(Throwable value) {
                                 logger.info("========消息发布失败=======");
                                 callbackConnection.disconnect(null);
                             }
                         });
                         try {
                             TimeUnit.SECONDS.sleep(1000);
                         } catch (InterruptedException e) {
                             logger.error(" error ",e);
                         }
                     }
                 }
             });

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            /*if(connection != null){
                try {
                    connection.disconnect(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

}
