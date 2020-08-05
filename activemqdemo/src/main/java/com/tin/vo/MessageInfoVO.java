package com.tin.vo;

import java.io.Serializable;

public class MessageInfoVO implements Serializable {

    private static final long serialVersionUID = 3003373687051105069L;

    private String messageType;
    private String moduleId;
    private String data;
    private String msg;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
