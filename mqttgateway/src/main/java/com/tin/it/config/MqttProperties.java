package com.tin.it.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 是否清除会话
     */
    private Boolean cleanSession = true;
    /**
     * 服务端url
     */
    private String[] serverURIs;
    /**
     * 是否异步发送
     */
    private Boolean async = true;
    /**
     * 超时时间
     */
    private int completionTimeout = 20000;
    /**
     * 心跳
     */
    private int keepAliveInterval = 30;
    /**
     * 客户端id
     */
    private String clientId = "ckMqttClient";
    /**
     * 默认的消息服务质量
     */
    private int defaultQos = 1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public String[] getServerURIs() {
        return serverURIs;
    }

    public void setServerURIs(String[] serverURIs) {
        this.serverURIs = serverURIs;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public int getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(int completionTimeout) {
        this.completionTimeout = completionTimeout;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getDefaultQos() {
        return defaultQos;
    }

    public void setDefaultQos(int defaultQos) {
        this.defaultQos = defaultQos;
    }
}
