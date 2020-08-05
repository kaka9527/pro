package com.tin.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.jms.pool.PooledSession;

import javax.jms.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Producer implements Runnable {

    //业务处理异步线程池，线程池参数可以根据您的业务特点调整，或者您也可以用其他异步方式处理接收到的消息。
//    private final static ExecutorService executorService = new ThreadPoolExecutor(
//            Runtime.getRuntime().availableProcessors(),
//            Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<>(50000));

    private final static ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    //ActiveMq 的默认用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;

    //ActiveMq 的默认登录密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

    //ActiveMQ 的链接地址
    private static final String BROKEN_URL = "tcp://192.168.10.80:61616";
    //链接工厂
    //ConnectionFactory connectionFactory;
    //链接对象
    Connection connection;
    //事务管理

    Session session;

    ThreadLocal<MessageProducer> threadLocal = new ThreadLocal<>();

    // 回调主题名称
    private static final String REPLY_TOPIC_NAME = "queue.chuangkou.uploadResult";

    //  消息属性
    private static final String BIN_FILE_NAME = "fileName";

    /**
     * 初始化消息生产者
     *
     * @throws JMSException
     */
    public void init() throws JMSException {
        try {
            //创建一个链接工厂
            /*ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    USERNAME, PASSWORD, BROKEN_URL);
            //从工厂中创建一个链接
            connection = connectionFactory.createConnection();
            //开启链接
            connection.start();*/
            connection = ActiveMQPoolsUtil.getConnection();
            //创建一个事务（这里通过参数可以设置事务的级别）
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String disname = "";
    private String fileName = "";

    public Producer(String disname, String fileName) {
        this.disname = disname;
        this.fileName = fileName;
        try {
            init();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sendMessage(this.disname,this.fileName);
        //
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        destory();
    }

    /**
     * 将消息发送至消息队列
     * @param disname
     * @param fileName
     */
    public void sendMessage(String disname, String fileName) {
        InputStream is = null;
        try {
            //创建一个点对点消息队列,队列名
            Destination queue = session.createTopic(disname);
            //消息生产者
            MessageProducer messageProducer = null;
            //设置消息生产者
            if (threadLocal.get() != null) {
                messageProducer = threadLocal.get();
            } else {
                messageProducer = session.createProducer(queue);
                threadLocal.set(messageProducer);
            }

            File file = new File("E:/ROOTiapupload/" + fileName);
            while (!file.renameTo(file)) {
                //当该文件正在被操作时
                Thread.sleep(1000);
            }

            BytesMessage bytesMessage = session.createBytesMessage();
            is = new FileInputStream(file);
            // 读取数据到byte数组中
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            bytesMessage.writeBytes(buffer);
            //创建一个回执地址
            Destination reback = session.createQueue(REPLY_TOPIC_NAME);
//            Topic topic = session.createTopic(REPLY_TOPIC_NAME);
//            MessageConsumer reConsumer = session.createConsumer(topic);
            MessageConsumer reConsumer = session.createConsumer(reback);
            Message receive = reConsumer.receive();
            if(receive instanceof TextMessage){
                TextMessage msg = (TextMessage)receive;
                System.out.println("msg=="+msg.getStringProperty("msg"));
                System.out.println("result=="+msg.getText());
            }
            reConsumer.setMessageListener(messageListener);
            /*reConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("code=="+message.getStringProperty("msg"));
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });*/
            //将回执地址写到消息
            bytesMessage.setJMSReplyTo(reback);
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = String.valueOf(System.currentTimeMillis())+suffix;
            bytesMessage.setStringProperty(BIN_FILE_NAME, newFileName);
            // selector
            bytesMessage.setStringProperty("type", "prepareUpload");
            messageProducer.send(bytesMessage);
            is.close();
            System.out.println(file.getName() + "发送消息成功！");
            //
            messageProducer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void destory(){
        try {
            if (null != session) {
                session.close();
            }
            if (null != threadLocal) {
                threadLocal.remove();
            }
            /*if (null != connection) {
                ActiveMQPoolsUtil.close();
            }*/

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private static MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessage(Message message) {
            try {
                //1.收到消息之后一定要ACK。
                // 推荐做法：创建Session选择Session.AUTO_ACKNOWLEDGE，这里会自动ACK。
                // 其他做法：创建Session选择Session.CLIENT_ACKNOWLEDGE，这里一定要调message.acknowledge()来ACK。
                // message.acknowledge();
                //2.建议异步处理收到的消息，确保onMessage函数里没有耗时逻辑。
                // 如果业务处理耗时过程过长阻塞住线程，可能会影响SDK收到消息后的正常回调。
                executorService.submit(() -> processMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
                //logger.error("submit task occurs exception ", e);
            }
        }
    };

    /**
     * 在这里处理您收到消息后的具体业务逻辑。
     */
    private static void processMessage(Message message) {
        try {
//            byte[] body = message.getBody(byte[].class);
//            String content = new String(body);
            String topic = message.getStringProperty("topic");
            String messageId = message.getStringProperty("messageId");
            System.out.println("receive message"
                    + ", topic = " + topic
                    + ", messageId = " + messageId);
//            logger.info("receive message"
//                    + ", topic = " + topic
//                    + ", messageId = " + messageId
//                    + ", content = " + content);
        } catch (Exception e) {
            //logger.error("processMessage occurs error ", e);
            e.printStackTrace();
        }
    }

    //
    private class CallBackListener implements MessageListener {
        ActiveMQSession session = null;

        public CallBackListener(ActiveMQSession session) {
            this.session = session;
        }

        @Override
        public void onMessage(Message message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TextMessage tx = (TextMessage) message;
                    try {
                        System.out.println("result=="+tx.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //
            /*threadPool.shutdown();
            try {
                threadPool.awaitTermination(1000,TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }
}
