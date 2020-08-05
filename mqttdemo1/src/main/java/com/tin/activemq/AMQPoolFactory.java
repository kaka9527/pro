package com.tin.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.ConnectionPool;
import org.apache.activemq.jms.pool.PooledSession;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.LinkedList;

public class AMQPoolFactory {
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String BROKERURL = "tcp://192.168.10.80:61616";
    private static final int SESSIONCACHESIZE = 20;//session缓存大小设置

    private static final int INIT_CONNECTION_SIZE = 1;//初始化连接数
    private static final int MAX_CONNECTION_SIZE = 5;//最大连接数
    private static final int MAX_SESSION_SIZE = 100;//每个连接可创建的最大session数
    private static final int MIN_SESSION_SIZE = 10;//每个连接可创建的最小session数
    private static final int MAX_PRODUCER_SESSION = 10;//每个session可建的最大producer数
    private static final int MAX_CONSUMER_SESSION = 10;//每个session可建的最大consumer数

    private LinkedList<ConnectionPool> connectionPool = new LinkedList<ConnectionPool>();//存放空闲连接的链表
    private LinkedList<PooledSession> sessionPools = new LinkedList<PooledSession>();

    /**
     * 缺省设置，默认加载MQ工厂初始化方法
     *
     * @throws Exception
     */
    public AMQPoolFactory() throws Exception {
        initMQFactory();
    }

    /**
     * 初始化MQ
     */
    public void initMQFactory() throws Exception {
        if (INIT_CONNECTION_SIZE > 0 && INIT_CONNECTION_SIZE <= MAX_CONNECTION_SIZE) {
            try {
                for (int i = 0; i < INIT_CONNECTION_SIZE; i++) {
                    Connection connection = buildConnection();
                    ConnectionPool connPool = new ConnectionPool(connection);
                    if (MIN_SESSION_SIZE > 0 && MIN_SESSION_SIZE <= MAX_SESSION_SIZE) {
                        /*connPool.setActiveSessions(MIN_SESSION_SIZE);
                        for (int j = 0; j < MIN_SESSION_SIZE; j++) {
                            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);//默认设置MQ自动检测消息是否送达
                            PooledSession sessionPool = new PooledSession();
                            sessionPool.setConnection(connection);
                            sessionPool.setSession(session);
                            sessionPools.addLast(sessionPool);
                        }*/
                    }
                    connectionPool.add(connPool);
                }
            } catch (JMSException e) {
                throw new Exception("MQ初始化异常", e);
            }
        }
    }

    /**
     * 构建方法
     *
     * @throws JMSException
     */
    private synchronized Connection buildConnection() throws JMSException {
        MqConfigBean bean = new MqConfigBean(BROKERURL, USERNAME, PASSWORD, SESSIONCACHESIZE);
        ActiveMQConnectionFactory targetFactory = new ActiveMQConnectionFactory(bean.getUserName(), bean.getPassword(), bean.getBrokerURL());
        //targetFactory.setUseAsyncSend(true);//强制使用同步返回数据格式

        CachingConnectionFactory connectoryFacotry = new CachingConnectionFactory();
        connectoryFacotry.setTargetConnectionFactory(targetFactory);
        //设置缓存大小，优化性能
        connectoryFacotry.setSessionCacheSize(bean.getSessionCacheSize());
        Connection connection = connectoryFacotry.createConnection();
        //开启连接
        connection.start();

        return connection;
    }

    /**
     * 从连接池中获取连接
     *
     * @throws Exception
     */
    private ConnectionPool getConnectionPool() throws Exception {
        ConnectionPool connPool = null;
        if (connectionPool != null && connectionPool.size() > 0) {
            for (ConnectionPool connectionPool : connectionPool) {
                int poolSessionSize = connectionPool.getNumActiveSessions();//获取该连接中session活跃的会话数量
                if (poolSessionSize < MAX_SESSION_SIZE) {//取相对会话比较少的连接
                    connPool = connectionPool;
                }
            }
            //若当前连接池中的连接全部处于使用状态，则在不超过最大连接数的前提下添加新的连接
            if (connPool == null && connectionPool.size() < MAX_CONNECTION_SIZE) {
                try {
                    Connection connection = buildConnection();
                    connPool = new ConnectionPool(connection);
                    /*if(MIN_SESSION_SIZE > 0 && MIN_SESSION_SIZE <= MAX_SESSION_SIZE) {
                        connPool.setActiveSessions(MIN_SESSION_SIZE);
                        for (int j = 0; j < MIN_SESSION_SIZE; j++) {
                            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);//默认设置MQ自动检测消息是否送达
                            SessionPool sessionPool = new SessionPool();
                            sessionPool.setConnection(connection);
                            sessionPool.setSession(session);
                            sessionPools.addLast(sessionPool);
                        }
                    }*/
                    connectionPool.add(connPool);
                } catch (JMSException e) {
                    throw new Exception("getConnection方法创建Connection异常", e);
                }
            }
        }
        return connPool;
    }

    // 获取连接
    public Connection getConnection(){
        try {
            ConnectionPool connPool = getConnectionPool();
            return connPool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 获取生产者的session信息
//     */
//    private SessionPool getProducerSessionPool() {
//        SessionPool sesPool = null;
//        if(sessionPools != null && sessionPools.size() > 0) {
//            try {
//                ConnectionPool connPool = getConnectionPool();
//                for(SessionPool pool : sessionPools) {
//                    if(pool.getConnection() == connPool.getConnection()) {
//                        int poolProducerSize = pool.getAvailableProducer();//获取当前session中已存在的生产者的数量
//                        if(poolProducerSize < MAX_PRODUCER_SESSION) {//当前session中生产者较少的优先使用
//                            sesPool = pool;
//                        }
//                    }
//                }
//                //当前session资源被占满，则重新创建一个新的session
//                if(sesPool == null && connPool.getActiveSessions() < MAX_SESSION_SIZE) {
//                    try {
//                        Connection conn = connPool.getConnection();
//                        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
//                        sesPool = new SessionPool();
//                        sesPool.setConnection(conn);
//                        sesPool.setSession(session);
//                        sessionPools.addLast(sesPool);
//                    }catch(Exception e) {
//                        throw new Exception("getProducerSession方法创建Session异常",e);
//                    }
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return sesPool;
//    }
//
//    /**
//     * 获取消费者的session信息
//     */
//    private SessionPool getConsumerSessionPool() {
//        SessionPool sesPool = null;
//        if(sessionPools != null && sessionPools.size() > 0) {
//            try {
//                ConnectionPool connPool = getConnectionPool();
//                for(SessionPool pool : sessionPools) {
//                    if(pool.getConnection() == connPool.getConnection()) {
//                        int poolConsumerSize = pool.getAvailableConsumer();//获取当前session中已存在的生产者的数量
//                        if(poolConsumerSize < MAX_CONSUMER_SESSION) {//当前session中消费者较少的优先使用
//                            sesPool = pool;
//                        }
//                    }
//                }
//                //当前session资源被占满，则重新创建一个新的session
//                if(sesPool == null && connPool.getActiveSessions() < MAX_SESSION_SIZE) {
//                    try {
//                        Connection conn = connPool.getConnection();
//                        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
//                        sesPool = new SessionPool();
//                        sesPool.setConnection(conn);
//                        sesPool.setSession(session);
//                        sessionPools.addLast(sesPool);
//                    }catch(Exception e) {
//                        throw new Exception("getProducerSession方法创建Session异常",e);
//                    }
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return sesPool;
//    }
//
//    /**
//     * 获取生产者的连接信息
//     * @param name 消息队列名称  messageType p2p or topic
//     * @param isVIP  可扩展  若为true则代表 当前session中无消费者和生产者
//     * @throws Exception
//     */
//    public ProducerPool getProducerPool(String name,String messageType) throws Exception{
//        SessionPool sessionPool = getProducerSessionPool();
//        Session session = sessionPool.getSession();
//        try {
//            Destination dt = null;
//            if(messageType.equals(MessageTypeEnum.TOPIC.getTypeName())) {
//                dt = session.createTopic(name);
//            }else if(messageType.equals(MessageTypeEnum.QUEUE.getTypeName())) {
//                dt = session.createQueue(name);
//            }
//            MessageProducer producer = session.createProducer(dt);
//            ProducerPool producerPool = new ProducerPool();
//            producerPool.setProducer(producer);
//            producerPool.setConnection(sessionPool.getConnection());
//            producerPool.setSession(session);
//            producerIncreament(sessionPool);
//            return producerPool;
//        }catch(Exception e) {
//            throw new Exception("获取生产者连接异常",e);
//        }
//    }
//    /**
//     * 获取消费者的连接信息
//     * @param name 消息队列名称  messageType p2p or topic
//     * @param isVIP  可扩展  若为true则代表 当前session中无消费者和生产者
//     * @throws Exception
//     */
//    public ConsumerPool getConsumerPool(String name,String messageType) throws Exception{
//        SessionPool sessionPool = getConsumerSessionPool();
//        Session session = sessionPool.getSession();
//        try {
//            Destination dt = null;
//            if(messageType.equals(MessageTypeEnum.TOPIC.getTypeName())) {
//                dt = session.createTopic(name);
//            }else if(messageType.equals(MessageTypeEnum.QUEUE.getTypeName())) {
//                dt = session.createQueue(name);
//            }
//            MessageConsumer consumer = session.createConsumer(dt);
//            ConsumerPool consumerPool = new ConsumerPool();
//            consumerPool.setConsumer(consumer);
//            consumerPool.setConnection(sessionPool.getConnection());
//            consumerPool.setSession(session);
//            consumerIncreament(sessionPool);
//            return consumerPool;
//        }catch(Exception e) {
//            throw new Exception("获取消费者连接异常",e);
//        }
//    }
//    /**
//     * 连接池中可用生产者加一
//     * @param sessionPool
//     */
//    private void producerIncreament(SessionPool sessionPool){
//        if(sessionPool!=null){
//            for(SessionPool sePool : sessionPools){
//                if(sePool==sessionPool){
//                    int cnt = sePool.getAvailableProducer();
//                    cnt++;
//                    sePool.setAvailableProducer(cnt);
//                }
//            }
//        }
//    }
//    /**
//     * 连接池中可用生产者减一
//     * @param producerPool
//     */
//    public void producerDecreament(ProducerPool producerPool){
//        if(producerPool!=null){
//            for(SessionPool sessionPool : sessionPools){
//                if(sessionPool.getConnection()==producerPool.getConnection()
//                        && sessionPool.getSession()==producerPool.getSession()){
//                    int cnt = sessionPool.getAvailableProducer();
//                    cnt--;
//                    sessionPool.setAvailableProducer(cnt);
//                }
//            }
//        }
//    }
//    /**
//     * 连接池中可用消费者加一
//     * @param sessionPool
//     */
//    private void consumerIncreament(SessionPool sessionPool){
//        if(sessionPool!=null){
//            for(SessionPool sePool : sessionPools){
//                if(sePool==sessionPool){
//                    int cnt = sePool.getAvailableConsumer();
//                    cnt++;
//                    sePool.setAvailableConsumer(cnt);
//                }
//            }
//        }
//    }
//    /**
//     * 连接池中可用消费者减一
//     * @param consumerPool
//     */
//    public void consumerDecreament(ConsumerPool consumerPool){
//        if(consumerPool!=null){
//            for(SessionPool sessionPool : sessionPools){
//                if(sessionPool.getConnection()==consumerPool.getConnection()
//                        && sessionPool.getSession()==consumerPool.getSession()){
//                    int cnt = sessionPool.getAvailableConsumer();
//                    cnt--;
//                    sessionPool.setAvailableConsumer(cnt);
//                }
//            }
//        }
//    }
//    /**
//     * 释放所有连接
//     * @return
//     * @throws AMQFactoryException
//     */
//    public boolean disposeAll() throws Exception{
//        try {
//            if(sessionPools!=null && sessionPools.size()>0){
//                for (SessionPool sessionPool : sessionPools) {
//                    sessionPool.getSession().close();
//                }
//                sessionPools.clear();
//            }
//            if(connectionPool!=null && connectionPool.size()>0){
//                for(ConnectionPool connectionPool : connectionPool){
//                    connectionPool.getConnection().stop();
//                    connectionPool.getConnection().close();
//                }
//                connectionPool.clear();
//            }
//            return true;
//        } catch (JMSException e) {
//            throw new Exception("释放连接出错",e);
//        }
//    }
//    /**
//     * 释放生产者连接
//     * @throws Exception
//     */
//    public void closeProducerConnection(MessageProducer producer) throws Exception {
//        if(producer!=null){
//            try {
//                producer.close();
//            } catch (JMSException e) {
//                throw new Exception("释放producer连接出错",e);
//            }
//        }
//    }
//    /**
//     * 释放消费者连接
//     * @throws Exception
//     */
//    public void closeConsumerConnection(MessageConsumer consumer) throws Exception {
//        if(consumer!=null){
//            try {
//                consumer.close();
//            } catch (JMSException e) {
//                throw new Exception("释放consumer连接出错",e);
//            }
//        }
//    }

    /**
     * 编写MQ配置bean
     */
    @SuppressWarnings("unused")
    private static class MqConfigBean {
        private String brokerURL;
        private String userName;
        private String password;
        private int sessionCacheSize;

        public MqConfigBean() {

        }

        public MqConfigBean(String brokerURL, String userName, String password, int sessionCacheSize) {
            this.brokerURL = brokerURL;
            this.userName = userName;
            this.password = password;
            this.sessionCacheSize = sessionCacheSize;
        }

        public String getBrokerURL() {
            return brokerURL;
        }

        public void setBrokerURL(String brokerURL) {
            this.brokerURL = brokerURL;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getSessionCacheSize() {
            return sessionCacheSize;
        }

        public void setSessionCacheSize(int sessionCacheSize) {
            this.sessionCacheSize = sessionCacheSize;
        }
    }
}
