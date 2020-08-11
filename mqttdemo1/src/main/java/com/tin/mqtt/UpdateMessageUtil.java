package com.tin.mqtt;

import com.tin.vo.MessageDLT;
import com.tin.vo.MessageUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateMessageUtil {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateMessageUtil.class);
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
            //LOG.info("packetTotal=="+packetTotal);
            // 第x包
            String packet = message.getPacket();
            packet = addZeroForNumLeft(packet, 4);
            packet = packetInfoHex(packet);
            packet = dataTohex(packet);
            //LOG.info("packet=="+packet);

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

            /*String dataSum = makeChecksum("68" + machineAddress + "68" + messageBean.getControl() + dataLong
                    + messageBean.getDataTab() + packetTotal + packet + messageBean.getDataStr());
            LOG.info("dataSum=="+dataSum);
            messageBean.setDatasum(dataSum);*/
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
            /*sendmes = messageBean.getStartFrame() + messageBean.getMachineAddress() +
                    messageBean.getStartFrame2() + messageBean.getControl() + messageBean.getDataLong() +
                    messageBean.getDataTab() + packetTotal + packet +  messageBean.getDataStr() + messageBean.getEndStr();*/

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return builder.toString();
    }

    //解析数据标 加33 倒序
    private static String dataTohex(String dataID) {
        int len = dataID.length();
        String send = null;

        try {
            send = "";
            long x = 0;
            long y = Long.parseLong("33", 16);
            int start = 2;
            int end = 0;
            for (int i = 0; i < len; i++) {
                if (dataID.length() >= start) {
                    String str = dataID.substring(dataID.length() - start, dataID.length() - end);
                    x = Long.parseLong(str, 16);
                    String temp = String.valueOf(Long.toHexString(x + y));
                    send += temp.substring(temp.length() - 2, temp.length());//当ff+33时=132，取后两位
                    start += 2;
                    end += 2;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return send;
    }

    //16进制累加和
    private static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        int x = 0;
        String s = "";
        while (num < len) {
            x = num + 2;
            if(x > len){
                s = data.substring(num);
            }else {
                s = data.substring(num, x);
            }
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static String addZeroForNumLeft(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);//左补0
                str = sb.toString();
                strLen = str.length();
            }
        }

        return str;
    }

    //组合设备地址 倒序
    public static String machineAddressHex(String address) {
        String addressHex = "";
        int start = 2;
        int end = 0;
        for (int i = 0; i < 6; i++) {
            if (address.length() >= start) {
                String str = address.substring(address.length() - start, address.length() - end);
                addressHex += str;
                start += 2;
                end += 2;
            }
        }

        addressHex = addZeroForNumFRight(addressHex, 12);
        return addressHex;
    }

    private static String addZeroForNumFRight(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }

        return str;
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

        addressHex = addZeroForNumFRight(addressHex, 4);
        return addressHex;
    }
}
