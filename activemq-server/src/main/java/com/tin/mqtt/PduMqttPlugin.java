package com.tin.mqtt;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PduMqttPlugin implements BrokerPlugin {
    private static final Log LOG = LogFactory.getLog(PduMqttPlugin.class);
    @Override
    public Broker installPlugin(Broker broker) throws Exception {
        LOG.info("install PduMqttPlugin ");
        return new MessageControlBroker(broker);
    }
}
