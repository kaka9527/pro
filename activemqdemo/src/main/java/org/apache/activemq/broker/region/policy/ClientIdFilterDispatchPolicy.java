package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *  @org.apache.xbean.XBean
 */
public class ClientIdFilterDispatchPolicy extends SimpleDispatchPolicy {
    private static final Logger logger = LoggerFactory.getLogger(ClientIdFilterDispatchPolicy.class);

    @Override
    public boolean dispatch(MessageReference node, MessageEvaluationContext msgContext, List<Subscription> consumers) throws Exception {
        logger.info("--------------------------------来了------------------------------------------");
        logger.info(node.getMessage().getDestination().getQualifiedName());
        logger.info(node.getMessage().getDestination().getPhysicalName());
        String topic = node.getMessage().getDestination().getPhysicalName();
        String clientId = topic.substring(topic.indexOf(".") + 1, topic.length());
        logger.info("clientId:" + clientId);
        logger.info("--------------------------------end------------------------------------------");

        if (clientId == null) {
            super.dispatch(node, msgContext, consumers);
        }
        ActiveMQDestination destination = node.getMessage().getDestination();

        int count = 0;
        for (Subscription sub : consumers) {
            if (sub.getConsumerInfo().isBrowser()) {
                continue;
            }
            if (!sub.matches(node, msgContext)) {
                sub.unmatched(node);
                continue;
            }
            logger.info("isTopic:" + destination.isTopic());
            logger.info("getClientId:" + sub.getContext().getClientId());
            if (clientId != null && destination.isTopic() && clientId.equals(sub.getContext().getClientId())) {
                sub.add(node);
                count++;
            } else {
                sub.unmatched(node);
            }
        }

        return count > 0;
    }
}
