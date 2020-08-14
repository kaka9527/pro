package com.tin.mqtt.type;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * CONNECT 连接处理
 */
public class Connect {

    private static final Logger log = LoggerFactory.getLogger(Connect.class);
    //
    private String msgType = "10";
    private String protocol = "00044D515454";
    private String version = "04";

    private String username;
    private String password;
    private boolean cleanSession;
    private int keepAlive;
    private String clientId;

    public Connect(String username, String password, boolean cleanSession, int keepAlive, String clientId) {
        this.username = username;
        this.password = password;
        this.cleanSession = cleanSession;
        this.keepAlive = keepAlive;
        this.clientId = clientId;
    }

    public String getConnectMsg(){
        StringBuffer buffer = new StringBuffer();

        buffer.append(protocol);
        buffer.append(version);
        if(this.cleanSession){
            buffer.append("C2");
        }else{
            buffer.append("C0");
        }
        buffer.append("00");
        buffer.append(Integer.toHexString(keepAlive));
        try {
            // clientId
            String cltId = Hex.encodeHexString(clientId.getBytes("UTF-8"), false);
            buffer.append("00");
            String cltStr = Integer.toHexString(cltId.length() / 2);
            //log.info("cltStr: "+cltStr);
            buffer.append(cltStr.length() == 1 ? "0"+cltStr:cltStr);
            buffer.append(cltId);

            // username
            String uname = Hex.encodeHexString(username.getBytes("UTF-8"), false);
            buffer.append("00");
            String suname = Integer.toHexString(uname.length() / 2);
            //log.info("uname: "+suname);
            buffer.append(suname.length() == 1 ? "0"+suname:suname);
            buffer.append(uname);
            // password
            String pwd = Hex.encodeHexString(password.getBytes("UTF-8"), false);
            buffer.append("00");
            String pwdStr = Integer.toHexString(pwd.length() / 2);
            //log.info("pwdStr: "+pwdStr);
            buffer.append(pwdStr.length() == 1 ? "0"+pwdStr:pwdStr);
            buffer.append(pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String msgInfo = buffer.toString();

        String result = msgType+Integer.toHexString(msgInfo.length()/2)+msgInfo;
        log.info("connect result: "+result);
        return result;
    }
}
