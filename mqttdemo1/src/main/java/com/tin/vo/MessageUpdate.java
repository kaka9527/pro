package com.tin.vo;

public class MessageUpdate {

    private String startFrame = "68";//起始帧

    private String machineAddress = "";//设备地址

    private String startFrame2 = "68";//起始帧

    private String control = "";//控制码

    private String dataLong = "";//数据长度

    private String dataTab ="";//数据标

    private String dataStr = "";//数据内容

    private String datasum = "";//校验和

    private String endStr = "16";//终止符

    private String packetTotal; // 总包数

    private String packet; // 第x包数

    private String password = "";//密码
    private String auth = "";//操作员代码

    public String getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(String startFrame) {
        this.startFrame = startFrame;
    }

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
    }

    public String getStartFrame2() {
        return startFrame2;
    }

    public void setStartFrame2(String startFrame2) {
        this.startFrame2 = startFrame2;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getDataLong() {
        return dataLong;
    }

    public void setDataLong(String dataLong) {
        this.dataLong = dataLong;
    }

    public String getDataTab() {
        return dataTab;
    }

    public void setDataTab(String dataTab) {
        this.dataTab = dataTab;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }

    public String getDatasum() {
        return datasum;
    }

    public void setDatasum(String datasum) {
        this.datasum = datasum;
    }

    public String getEndStr() {
        return endStr;
    }

    public void setEndStr(String endStr) {
        this.endStr = endStr;
    }

    public String getPacketTotal() {
        return packetTotal;
    }

    public void setPacketTotal(String packetTotal) {
        this.packetTotal = packetTotal;
    }

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
