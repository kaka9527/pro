package com.tin.mqtt;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PduMqttPlugin implements BrokerPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(PduMqttPlugin.class);
    @Override
    public Broker installPlugin(Broker broker) throws Exception {
        LOG.info("install MqttSelectorPlugin ");
        return new MessageControlBroker(broker);
    }
}
