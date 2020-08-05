package com.tin.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Session;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActiveMQPool {
    private int currentNum = 0; // 该对象池当前已创建的对象数目
    private Session currentObj;// 该对象池当前可以借出的对象
    private List<Session> pool;// 用于存放对象的池
    private ActiveMQConnection conn = null;
    private Logger logger = LoggerFactory.getLogger(ActiveMQPool.class);

    public ActiveMQPool() {
        pool = Collections.synchronizedList(new LinkedList<Session>());
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setOptimizeAcknowledge(true);
            conn = (ActiveMQConnection) connectionFactory.createConnection();
            conn.start();
        } catch (JMSException e) {
            logger.error("MQ session连接池异常", e);
        }
    }

    public synchronized Session getSession() throws Exception {
        if (pool.size() == 0 /*&& currentNum < BigDataConstant.CONFIG.MQ_SESSION_MAXSIZE*/) {
            // 如果当前池中无对象可用，而且已创建的对象数目小于所限制的最大值，创建一个新的对象
            currentObj = conn.createSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);
            pool.add(currentObj);
            currentNum++;
        } else if (pool.size() == 0 && currentNum >= 100) {
            // 如果当前池中无对象可用，而且所创建的对象数目已达到所限制的最大值, 就只能等待其它线程返回对象到池中
            while (pool.size() == 0) {
            }
            currentObj = (Session) pool.remove(0);
        } else {
            // 如果当前池中有可用的对象，就直接从池中取出对象
            currentObj = (Session) pool.remove(0);
        }
        return currentObj;
    }

    /**
     * 放回连接池
     * @param session
     */
    public void returnSession(Session session) {
        pool.add(session);
    }

    /**
     * 获取连接
     *
     * @return
     */
    public ActiveMQConnection getConn() {
        return conn;
    }

    /**
     * 关闭连接
     *
     * @throws Exception
     */
    public void colseConn() throws Exception {
        if (null != conn) {
            conn.close();
        }
    }
}
