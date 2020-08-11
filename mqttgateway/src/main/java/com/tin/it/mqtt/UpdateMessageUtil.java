package com.tin.it.mqtt;

import com.tin.it.vo.MessageDLT;
import com.tin.it.vo.MessageUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateMessageUtil {
    private static final Logger logger = LoggerFactory.getLogger(UpdateMessageUtil.class);
    /**
     * 设备更新报文组合
     */
    public static String updateMessageToHex(MessageUpdate message) {
        StringBuilder builder = new StringBuilder();
        try {
            String machineAddress = message.getMachineAddress();
            machineAddress = addZeroForNumLeft(machineAddress, 12);
            machineAddress = machineAddressHex(machineAddress);
            // 总包数
            String packetTotal = message.getPacketTotal();
            packetTotal = addZeroForNumLeft(packetTotal, 4);
            packetTotal = packetInfoHex(packetTotal);
            packetTotal = dataTohex(packetTotal);
            //logger.info("packetTotal=="+packetTotal);
            // 第x包
            String packet = message.getPacket();
            packet = addZeroForNumLeft(packet, 4);
            packet = packetInfoHex(packet);
            packet = dataTohex(packet);
            //logger.info("packet=="+packet);

            MessageDLT messageBean = new MessageDLT();
            String dataTab = dataTohex(message.getDataTab());
            String dataStr = message.getDataStr();

            messageBean.setStartFrame("68");
            messageBean.setMachineAddress(machineAddress);
            messageBean.setStartFrame2("68");
            messageBean.setControl(message.getControl());

            String data = message.getDataTab() + message.getDataStr();

            //数据长度需要转成16进制
            String dataLong = addZeroForNumLeft(String.valueOf(Integer.toHexString(data.length() / 2)), 4);

            messageBean.setDataLong(dataLong);
            messageBean.setDataTab(dataTab);
            messageBean.setPassword(message.getPassword());
            messageBean.setAuth(message.getAuth());
            messageBean.setDataStr(dataStr);

            messageBean.setEndStr("16");
            // messageBean.getPassword() + messageBean.getAuth() +
            builder.append(messageBean.getStartFrame());
            builder.append(messageBean.getMachineAddress());
            builder.append(messageBean.getStartFrame2());
            builder.append(messageBean.getControl());
            builder.append(messageBean.getDataLong());
            builder.append(messageBean.getDataTab());
            builder.append(packetTotal);
            builder.append(packet);
            builder.append(messageBean.getDataStr());
            //builder.append(messageBean.getDatasum());
            builder.append(messageBean.getEndStr());

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return builder.toString();
    }

    //解析数据标 加33 倒序
    private static String dataTohex(String dataId) {
        int len = dataId.length();
        String send = null;

        try {
            send = "";
            long x = 0;
            long y = Long.parseLong("33", 16);
            int start = 2;
            int end = 0;
            for (int i = 0; i < len; i++) {
                if (len >= start) {
                    String str = dataId.substring(len - start, len - end);
                    x = Long.parseLong(str, 16);
                    String temp = String.valueOf(Long.toHexString(x + y));
                    send += temp.substring(temp.length() - 2, temp.length());//当ff+33时=132，取后两位
                    start += 2;
                    end += 2;
                }
            }
        } catch (NumberFormatException e) {
            logger.error(" dataTohex error ",e);
        }

        return send;
    }

    /**
     * 左边补0
     * @param str
     * @param strLength
     * @return
     */
    public static String addZeroForNumLeft(String str, int strLength) {
        int strLen = str.length();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        int len = strLength - strLen;
        while (i < len) {
            builder.append("0");
            i++;
        }
        builder.append(str);
        return String.valueOf(builder);
    }

    //组合设备地址 倒序
    public static String machineAddressHex(String address) {
        String addressHex = "";
        int start = 2;
        int end = 0;
        int len = address.length();
        for (int i = 0; i < 6; i++) {
            if ( len >= start) {
                String str = address.substring(len - start, len - end);
                addressHex += str;
                start += 2;
                end += 2;
            }
        }

        addressHex = addZeroForNumRight(addressHex, 12);
        return addressHex;
    }

    private static String addZeroForNumRight(String str, int strLength) {
        int strLen = str.length();
        StringBuilder builder = new StringBuilder();
        int i = 0;
        int len = strLength - strLen;
        while (i < len) {
            builder.append("0");
            i++;
        }
        return str + String.valueOf(builder);
    }

    /**
     * 组合总包数、第x包 倒序
     */
    private static String packetInfoHex(String address) {
        String addressHex = "";
        int start = 2;
        int end = 0;
        for (int i = 0; i < 2; i++) {
            if (address.length() >= start) {
                String str = address.substring(address.length() - start, address.length() - end);
                addressHex += str;
                start += 2;
                end += 2;
            }
        }

        addressHex = addZeroForNumRight(addressHex, 4);
        return addressHex;
    }

    /**
     * 判断返回消息
     */
    public static boolean retryStatus(String rtMsg,int type){
        String rt0 = "";
        String rt1 = "";
        String ninetyOne = "91";
        String ninetyFour = "94";
        String one = "01";
        String six = "06";
        if(type == 1){
            //  94
            rt0 = getRetryStr(rtMsg,0,2);
            //  01  or 00
            rt1 = getRetryStr(rtMsg,4,6);
            if(ninetyFour.equals(rt0) && one.equals(rt1)){
                return true;
            }
        }
        if(type == 2){
            //  91
            rt0 = getRetryStr(rtMsg,0,2);
            //  01  or 00
            rt1 = getRetryStr(rtMsg,12,14);
            if(ninetyOne.equals(rt0) && one.equals(rt1)){
                return true;
            }
        }
        if(type == 3){
            // 91 07 42373337 0032 0002 15 5016
            //  91
            rt0 = getRetryStr(rtMsg,0,2);
            //  15  or 06
            rt1 = getRetryStr(rtMsg,20,22);
            if(ninetyOne.equals(rt0) && six.equals(rt1)){
                return true;
            }
        }
        return false;
    }

    public static String getRetryStr(String rtMsg,int startIndex,int endIndex){
        String msg = rtMsg;
        if(null != rtMsg && rtMsg.length() > 16){
        // 68 880000100200 68 94 01 01 00 16
//        01：可以更新
//        00：不更新
            msg = rtMsg.substring(16);
            if(msg.length() < endIndex){
                endIndex = msg.length();
                startIndex = endIndex - 2;
            }
            msg = msg.substring(startIndex,endIndex);

            // 68 010011000100 68 91 04 423733375016
            //6801001100010068110442373337D016
//        01准备好
//        00没有准备好

        // 68 010011000100 68 91 07	42373337 0032 0002 15 50 16
        /**
         * 接收成功ACK=0X15
         * 接收失败NCK=0X06
         */
        }
        return msg;
    }
}
