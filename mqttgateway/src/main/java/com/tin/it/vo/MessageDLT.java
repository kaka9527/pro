package com.tin.it.vo;

/**
 * @Description:DLT报文实体属性
 */
public class MessageDLT {

    private int ID;

    private String ip = "";

    private String port = "";

    private String startFrame = "68";//起始帧

    private String machineAddress = "";//设备地址

    private String startFrame2 = "68";//起始帧

    private String control = "";//控制码

    private String dataLong = "";//数据长度

    private String dataTab ="";//数据标

    private String dataStr = "";//数据内容

    private String datasum = "";//校验和

    private String endStr = "16";//终止符

    private String collectionTime;

    private String year = "";
    private String month = "";
    private String day = "";
    private String hour = "";
    private String minute = "";
    private String second = "";

    private String error = "";//错误信息

    private String passpro = "";//密码权限
    private String password = "";//密码
    private String auth = "";//操作员代码

    private String seq = "";//帧序号

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(String startFrame) {
        this.startFrame = startFrame;
    }


    public String getStartFrame2() {
        return startFrame2;
    }

    public void setStartFrame2(String startFrame2) {
        this.startFrame2 = startFrame2;
    }

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPasspro() {
        return passpro;
    }

    public void setPasspro(String passpro) {
        this.passpro = passpro;
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

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getDataTab() {
        return dataTab;
    }

    public void setDataTab(String dataTab) {
        this.dataTab = dataTab;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(String collectionTime) {
        this.collectionTime = collectionTime;
    }
}
