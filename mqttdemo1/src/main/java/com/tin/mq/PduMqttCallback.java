package com.tin.mq;

public abstract class PduMqttCallback {
    /**
     * 订阅成功
     *
     * @param mqttTopic
     */
    public void subscribedSuccess(String[] mqttTopic) {

    }

    /**
     * 订阅失败
     *
     * @param message
     */
    public void subscribedFail(String message) {

    }

    /**
     * 发送成功
     * @param message
     */
    public void deliveryComplete(String message) {

    }

    /**
     * 接收的数据
     *
     * @param topic
     * @param message
     */
    public abstract void receiveMessage(String topic, String message);


    /**
     * 连接成功
     */
    public void connectSuccess(boolean reconnect) {

    }

    /**
     * 连接失败
     *
     * @param message
     */
    public void connectFail(String message) {

    }

    /**
     * 断开连接
     *
     * @param message
     */
    public void connectLost(String message) {

    }

}
