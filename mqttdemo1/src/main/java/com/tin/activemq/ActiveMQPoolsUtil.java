package com.tin.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.JMSException;

public class ActiveMQPoolsUtil {

    //ActiveMq 的默认用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //ActiveMq 的默认登录密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    /**
     * 连接
     */
    private static PooledConnection connection;

    private ActiveMQPoolsUtil() {
    }

    // 初始化连接池等工作
    static{
        String url = "failover:(tcp://192.168.10.80:61616)?initialReconnectDelay=1000";
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setUserName(USERNAME);
        activeMQConnectionFactory.setPassword(PASSWORD);
        activeMQConnectionFactory.setBrokerURL(url);

        try{

            PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(
                    activeMQConnectionFactory);

            // session数
            int maximumActive = 200;
            pooledConnectionFactory.setMaximumActiveSessionPerConnection(maximumActive);
            pooledConnectionFactory.setIdleTimeout(120);
            pooledConnectionFactory.setMaxConnections(5);
            pooledConnectionFactory.setBlockIfSessionPoolIsFull(true);
            connection = (PooledConnection) pooledConnectionFactory.createConnection();
            // 必须start，否则无法接收消息
            connection.start();
        }catch(JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public static void close() {
        try{
            if(connection != null) {
                connection.close();
            }
        }catch(JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个连接
     */
    public static PooledConnection getConnection() {
        return connection;
    }

    /**
     * 设置连接
     */
    public static void setConnection(PooledConnection connection) {
        ActiveMQPoolsUtil.connection = connection;
    }
}
