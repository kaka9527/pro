package com.tin.mqtt;

import org.apache.activemq.broker.*;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.command.*;
import org.apache.activemq.store.MessageStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class MessageControlBroker extends BrokerFilter {

    private static final Log log = LogFactory.getLog(MessageControlBroker.class);
    private static final long DEFAULT_EXPIRATION = 300 * 1000;
    private static final long DEFAULT_PURGE_COUNT = 1000;

    public MessageControlBroker(Broker next) {
        super(next);
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        log.info("state: "+context.getConnectionState());
        log.info("client ip: "+info.getClientIp());
        super.addConnection(context, info);
    }

    //需要注意的是mqtt协议建立消费者的时候。consumerInfo里不会带上clientID，只能从ConnectionContext中取。
    @Override
    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        log.info("plugin topic: "+info.getDestination().toString());
        //if (info.getDestination().toString().contains("chuangkou")){
            //info.setSelector("ClientID='"+context.getClientId()+"'");
            log.info("[Consumer] Adding consumer : "+ info);
       //}

        return super.addConsumer(context, info);
    }

    @Override
    public void addProducer(ConnectionContext context, ProducerInfo info) throws Exception {
        super.addProducer(context, info);
    }

    @Override
    public void processConsumerControl(ConsumerBrokerExchange consumerExchange, ConsumerControl control) {
        //log.info(consumerExchange.toString());
        //log.info(control.toString());
        super.processConsumerControl(consumerExchange, control);
    }

    /**
     * 消息已过期
     * @param context
     * @param message
     * @param subscription
     */
    @Override
    public void messageExpired(ConnectionContext context, MessageReference message, Subscription subscription) {
        super.messageExpired(context, message, subscription);
    }

    /**
     * 清除队列中的所有消息
     */
    /*private void purgeMessage(Message message) {
        Message.MessageDestination r = message.getRegionDestination();
        if (r instanceof Topic) {
            try {
                //如果累积消息超过1000个，清除队列消息
                MessageStore messageStore = ((Topic) r).getMessageStore();
                log.info("message size: "+messageStore.getMessageSize());
                if (messageStore.getMessageSize() > DEFAULT_PURGE_COUNT) {
                    //((Topic) r);
                }
            } catch (Exception e) {
                log.error(" failed to purge topic ", e);
            }
        }else {
            ActiveMQDestination destination = message.getDestination();
            if(destination.isTopic()){
                ActiveMQTopic topic = (ActiveMQTopic)destination;
                //如果累积消息超过1000个，清除队列消息
                try {
                    log.info("message size: ");
                } catch (Exception e) {
                    log.error(" failed to purge topic error ", e);
                }
            }
        }
    }*/

    /**
     * 当消息发送时
     * 全部设置过期时间1个小时
     */
    @Override
    public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
        String physicalName = messageSend.getDestination().getPhysicalName();
        Message message = messageSend.getMessage();
        String msg = message.toString();
        byte[] content = messageSend.getContent().getData();
        log.info("content: "+new String(content));
        int len = message.getContent().getLength();
        log.info("---broker send msg len : "+len);
        byte[] data = message.getContent().getData();
        log.info("---broker send msg : "+new String(data));
        //log.info("---broker send msg1 : "+msg);
        log.info("---broker send msg length : "+data.length);
        //log.info("---broker send physicalName : "+physicalName);
//        long oldExp = messageSend.getExpiration();
//        messageSend.setExpiration(oldExp < DEFAULT_EXPIRATION && oldExp > 0 ? oldExp : DEFAULT_EXPIRATION);
//        purgeMessage(messageSend);
        super.send(producerExchange, messageSend);
    }


}
