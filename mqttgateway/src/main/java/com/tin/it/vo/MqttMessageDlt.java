package com.tin.it.vo;

import java.io.Serializable;

public class MqttMessageDlt {
    /**
     * 帧起始符 1
     */
    private String startFrame0 = "68";
    /**
     * 地址域
     */
    private String addressDomain;
    /**
     * 帧起始符 2
     */
    private String startFrame1 = "68";
    /**
     * 控制码
     */
    private String controlCode;
    /**
     * 数据标
     */
    private String dataIdentifiers;
    /**
     * 数据域长度
     */
    private String dataLength;
    /**
     * 数据域
     */
    private String dataDomain;

    /**
     * 校验码
     */
    private String checkCode;

    /**
     * 结束符
     */
    private String endFrame;

    public String getStartFrame0() {
        return startFrame0;
    }

    public void setStartFrame0(String startFrame0) {
        this.startFrame0 = startFrame0;
    }

    public String getAddressDomain() {
        return addressDomain;
    }

    public void setAddressDomain(String addressDomain) {
        this.addressDomain = addressDomain;
    }

    public String getStartFrame1() {
        return startFrame1;
    }

    public void setStartFrame1(String startFrame1) {
        this.startFrame1 = startFrame1;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public String getDataDomain() {
        return dataDomain;
    }

    public void setDataDomain(String dataDomain) {
        this.dataDomain = dataDomain;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(String endFrame) {
        this.endFrame = endFrame;
    }

    public String getDataIdentifiers() {
        return dataIdentifiers;
    }

    public void setDataIdentifiers(String dataIdentifiers) {
        this.dataIdentifiers = dataIdentifiers;
    }
}
