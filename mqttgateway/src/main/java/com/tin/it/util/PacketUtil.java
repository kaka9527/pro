package com.tin.it.util;

public class PacketUtil {
    private static final int len18 = 18;
    private static final int len14 = 14;
    private static final int len22 = 22;
    /**
     * 获取控制码
     * @param msg
     * @return
     */
    public static String getControlCode(String msg){
        if(msg.length() > len18){
            return msg.substring(16,18);
        }
        return msg;
    }

    /**
     * 获取设备id
     * @param msg
     * @return
     */
    public static String getDeviceCode(String msg){
        if(msg.length() > len14){
            return msg.substring(2,14);
        }
        return msg;
    }

    /**
     * 获取校时从站应答标识 00,01,10,11
     * @param msg
     * @return
     */
    public static String getRespCode(String msg){
        if(msg.length() > len22){
            return msg.substring(20,22);
        }
        return msg;
    }

    /**
     * 34343337
     */
//    public static String getDataIdentity(String msg){
//        if(msg.length() > len14){
//            return msg.substring(2,14);
//        }
//        return msg;
//    }
}
