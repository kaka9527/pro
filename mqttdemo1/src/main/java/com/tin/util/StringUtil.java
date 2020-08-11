package com.tin.util;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
public class StringUtil {

    static String[] hexStr = {"0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E", "F"};

    public static void main(String[] args) {
//        hexMessage("2", 3, "1232","1");
        actionMessage("2341");
    }

    public static String randomStr() {
        String a = "0123456789";
        char[] rands = new char[4];
        String str = "";
        for (int i = 0; i < rands.length; i++) {
            int rand = (int) (Math.random() * a.length());
            rands[i] = a.charAt(rand);
            str += rands[i];

        }
        for (int i = 0; i < rands.length; i++) {
//            System.out.println(rands[i]);
        }
        return str;
    }




    private static String toHexString(String str, int start, int end) {
        return Long.valueOf(str.substring(start, end), 16).toString();
    }

    /**
     * @Author:xulei
     * @Description:解析回传的报文
     * @Date:2019-02-28
     */
//    public static String actionMessage(String result,String messageID,Pdu pdu) {
    public static String actionMessage(String messageID) {
        String res = "aaaab2a3120205051b2e0925000000010015b3007bc00c0001000000000000000000000100000000000000000000000000000000000000000000000000505555";
        String[] str = new String[37];
        Byte ii = 123;
        int s = ii.intValue();
//        System.out.println(s);
//        System.out.println(new Byte("12").intValue());
//        System.out.println(Integer.valueOf(res.substring(0, 2).toString(), 16));

        String actiontype = null;
        try {
            str[0] = toHexString(res, 0, 2);//帧头
            str[1] = toHexString(res, 2, 4);//帧头
            str[2] = toHexString(res, 4, 6);//控制字
            str[3] = toHexString(res, 6, 8);//命令字
            str[4] = toHexString(res, 8, 10);//年
            str[5] = toHexString(res, 10, 12);//月
            str[6] = toHexString(res, 12, 14);//日
            str[7] = toHexString(res, 14, 16);//时
            str[8] = toHexString(res, 16, 18);//分
            str[9] = toHexString(res, 18, 20);//秒
            str[10] = toHexString(res, 20, 24);//操作ID
            str[11] = toHexString(res, 24, 26);//操作状态
            str[12] = toHexString(res, 26, 28);//心跳
            str[13] = toHexString(res, 28, 32);//接收端--产品类型 设备ID前三位
            str[14] = toHexString(res, 32, 38);//接收端--产品序号 设备ID中间四位
            str[15] = toHexString(res, 38, 42);//接收端--产品随机码 设备ID后三位
            str[16] = toHexString(res, 42, 44) + "."
                    + toHexString(res, 44, 46) + "."
                    + toHexString(res, 46, 48) + "."
                    + toHexString(res, 48, 50);//设备IP
            str[17] = toHexString(res, 50, 52);//保留
            str[18] = toHexString(res, 52, 54);//保留
            str[19] = toHexString(res, 54, 56);//保留
            str[20] = toHexString(res, 56, 58);//0--市电  1—UPS 电源类型
            str[21] = toHexString(res, 58, 62);//电压
            str[22] = toHexString(res, 62, 66);//电流
            str[23] = toHexString(res, 66, 70);//功率
            str[24] = toHexString(res, 70, 72);//继电器状态
            str[25] = toHexString(res, 72, 74);//过压状态
            str[26] = toHexString(res, 74, 76);//欠压状态
            str[27] = toHexString(res, 76, 78);//过流状态
            str[28] = toHexString(res, 78, 80);//断路状态
            str[29] = toHexString(res, 80, 82);//漏电状态
            str[30] = toHexString(res, 82, 88);//发送端--产品类型 设备ID前三位
            str[31] = toHexString(res, 88, 94);//发送端--产品序号 设备ID中间四位
            str[32] = toHexString(res, 94, 98);//发送端--产品随机码 设备ID后三位
            str[33] = toHexString(res, 98, 122);//48-60位都是保留字
            str[34] = toHexString(res, 122, 124);//校验和
            str[35] = toHexString(res, 124, 126);//帧尾
            str[36] = toHexString(res, 126, 128);//帧尾

            String acstate = "";//操作状态
            String mechineID = ""; //设备ID
            String ip = "";
            actiontype = "";

            for (int rss = 0; rss <= 36; rss++) {
//                System.out.println(str[rss]);
            }
//            System.out.println("str[10]==" + str[10].toString());

            //根据操作类型封装相应的返回值，用map返回
            //str[3] = "a1";//1表示 自动对时&网络配置信息上报（0xa1）
            Map map = new HashMap<String, String>();
            String actype = Long.toHexString(Integer.valueOf(str[3]));
            if (actype.equals("a1")) {//a1表示 自动对时&网络配置信息上报
                //获取machineID、IP地址、设备类型
                map.put("machineid", str[13] + str[14] + str[15]);
                map.put("ip", str[16]);
                map.put("type", str[13]);

            }
            if (actype.equals("a2")) {//a2表示工作状态信息查询
                //获取machineID、IP地址、设备类型
                map.put("machineid", str[13] + str[14] + str[15]);
                map.put("ip", str[16]);
                map.put("type", str[13]);

            }
            if (actype.equals("a2")) {//a3表示控制命令下发
                //获取machineID、IP地址、设备类型
                map.put("machineid", str[13] + str[14] + str[15]);
                map.put("ip", str[16]);
                map.put("type", str[13]);

            }
            if (actype.equals("a2")) {//a4表示故障告警信息主动上报
                //获取machineID、IP地址、设备类型
                map.put("machineid", str[13] + str[14] + str[15]);
                map.put("ip", str[16]);
                map.put("type", str[13]);

            }


            //判断操作ID是否一致
            if (messageID.equals(str[10])) {
                acstate = str[11].toString();
                mechineID = str[13].toString() + str[14].toString() + str[15].toString();
                ip = str[16].toString();

                //判断设备ID、Ip地址是否一致 以及操作是否成功
                //            if(acstate.equals("1") && mechineID.equals(pdu.getMachineid()) && ip.equals(pdu.getIp())){
                //                actiontype = "1";
                //            }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return actiontype;
    }






    //字符串左边补0
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
            // sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    //字符串左边补0
    public static String addZeroForNumRight(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
//            sb.append("0").append(str);// 左补0
            sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    //16进制累加和
    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
//            System.out.println(s);
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


    //二进制字符串装16进制
    public static String b2h(String binary) {
        // 这里还可以做些判断，比如传进来的数字是否都是0和1
//        System.out.println(binary);
        int length = binary.length();
        int temp = length % 4;
        // 每四位2进制数字对应一位16进制数字
        // 补足4位
        if (temp != 0) {
            for (int i = 0; i < 4 - temp; i++) {
                binary = "0" + binary;
            }
        }
        // 重新计算长度
        length = binary.length();
        StringBuilder sb = new StringBuilder();
        // 每4个二进制数为一组进行计算
        for (int i = 0; i < length / 4; i++) {
            int num = 0;
            // 将4个二进制数转成整数
            for (int j = i * 4; j < i * 4 + 4; j++) {
                num <<= 1;// 左移
                num |= (binary.charAt(j) - '0');// 或运算
            }
            // 直接找到该整数对应的16进制，这里不用switch来做
            sb.append(hexStr[num]);
            // 这里如果要用switch case来做，大概是这个样子
            // switch(num){
            // case 0:
            // sb.append('0');
            // break;
            // case 1:
            // ...
            // case 15:
            // sb.append('F');
            // break;
            // }
        }
        return sb.toString();
    }


    //十六进制转二进制
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789abcdef".indexOf(c);
        return b;
    }


    //十六进制字符串转二进制字符串
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }


    //十六进制转byte[]
    public static byte[] hexToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2; // 两个字符对应一个byte
            int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
            int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
            if (h == -1 || l == -1) { // 非16进制字符
                return null;
            }
            bytes[i] = (byte) (h | l);
        }
        return bytes;
    }

    /**
     * @Author:
     * @Description:字符串转十六进制字符串
     * @Date:
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * @Author:xulei
     * @Description: 字符串转字节数组
     * @Date:
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }


    /**
     * @Author:xulei
     * @Description:字符串转十六进制字符串
     * @Date:
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


    public static String str2HexStr(byte[] bs) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
//        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }


    /**
     * 转换时间日期格式字串为long型
     *
     * @param time 格式为：yyyy-MM-dd HH:mm:ss的时间日期类型
     */
    public static Long convertTimeToLong(String time) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(time);
//            System.out.println(date);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 数组比较，获取相同的元素
     *
     */
    public static <T> Set<T> getIds(T[] a, T[] b){

        Set<T> same = new HashSet<T>();  //用来存放两个数组中相同的元素
        Set<T> temp = new HashSet<T>();  //用来存放数组a中的元素

        for (int i = 0; i < a.length; i++) {
            temp.add(a[i]);   //把数组a中的元素放到Set中，可以去除重复的元素
        }

        for (int j = 0; j < b.length; j++) {
            //把数组b中的元素添加到temp中
            //如果temp中已存在相同的元素，则temp.add（b[j]）返回false
            if(!temp.add(b[j])){
                same.add(b[j]);
            }
        }
        return same;
    }

    //数据比较找出不同的元素
    public static <T> List<T> compare(T[] t1, T[] t2) {
        List<T> list1 = Arrays.asList(t1); //将t1数组转成list数组
        List<T> list2 = new ArrayList<T>();//用来存放2个数组中不相同的元素
        for (T t : t2) {
            if (!list1.contains(t)) {
                list2.add(t);
            }
        }
        return list2;
    }


}
