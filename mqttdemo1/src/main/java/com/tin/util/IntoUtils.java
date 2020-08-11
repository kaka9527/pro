package com.tin.util;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.tin.util.StringUtil.addZeroForNum;


/**
 * @author zhangbo
 * @Date 2019/8/16
 * MODBUS协议的进制转换工具
 */

public class IntoUtils {




    /**
     * @author zhangbo
     * @Date 2019/8/16
     * 时间需要转换为16进制
     * 年月日星期时分秒
     */
        public static String dataIntoMethods(String data){
            ArrayList list = new ArrayList();
            String dd="";
            int i = 0;
            for (i = 0; i < data.length() - 1; i += 2) {
                list.add(data.substring(i, i + 2));
            }
            list.add(data.substring(i));
            for (i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                if (!o.equals(0) && !o.equals("")){
                    int b = Integer.parseInt(String.valueOf(o));
                    String s = intToHex(b);
                    data = addZeroForNum(s, 2);
                    dd += data;
                }
            }
            return dd;

        }


    /**
     * @author zhangbo
     * @Date 2019/8/16
     *将字符串循环切割为1位并转换2进制
     * 用于判断 回路 状态
     * Bit15-bit0
     */

    public static String cuttingTransformationOne(String hex) {

        ArrayList list = new ArrayList();
        String dd="";
        int i = 0;
        for (i = 0; i < hex.length() - 1; i += 1) {
            list.add(hex.substring(i, i + 1));
        }
        list.add(hex.substring(i));
        for (i = 0; i < list.size(); i++) {
            Object oo = list.get(i);
            if (!oo.equals(0) && !oo.equals("")){
                System.out.println("----------"+oo);
                //int bb = Integer.parseInt(oo,16);
                System.out.println("bbbbbb:"+oo);
                String string = hexStringToByte(String.valueOf(oo));
                hex = addZeroForNum(String.valueOf(Integer.parseInt(string)), 4);
                System.out.println(hex);
                dd += hex;
            }
        }
        return dd;


    }



    /**
     * @author zhangbo
     * @Date 2019/8/16
     * 16进制转2进制
     *
     */
    public static String hexStringToByte(String hex) {
        int i = Integer.parseInt(hex, 16);
        String str2 = Integer.toBinaryString(i);
        return str2;
    }


    /**
     * @author zhangbo
     * @Date 2019/8/16
     * 将字符串转换为16进制
     *
     */
    private static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString();
        return a;
    }

    /**
     * @author zhangbo
     * @Date 2019/8/16
     * 星期 的转换
     *
     */
    public static String getWeekOfDate(String dt) {

        String whatday = "";
//        if (dt.equals("星期日")) whatday = "00";
//        if (dt.equals("星期一")) whatday = "01";
//        if (dt.equals("星期二")) whatday = "02";
//        if (dt.equals("星期三")) whatday = "03";
//        if (dt.equals("星期四")) whatday = "04";
//        if (dt.equals("星期五")) whatday = "05";
//        if (dt.equals("星期六")) whatday = "06";

        return whatday;
    }



    /**
     * @author zhangbo
     * @Date 2019/8/16
     * 将字符串转换为URL ASCII 码
     *
     */
    public static String intoMethodsURLCode(String data) {

        ArrayList list = new ArrayList();
        String dd = "";
        String urlDecoderString = "";
        int i = 0;
        for (i = 0; i < data.length() - 1; i += 2) {
            list.add(data.substring(i, i + 2));
        }
        list.add(data.substring(i));
        for (i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (!o.equals(0) && !o.equals("")) {
                String s = addPercentForNum(o.toString(), 3);
                dd += s;
                urlDecoderString = getURLDecoderString(dd);
            }
        }
        return urlDecoderString;
    }



    /**
     * @author zhangbo
     * @Date 2019/8/16
     * 左边补上 %
     *
     */
    public static String addPercentForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("%").append(str);
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }


    /**
     * @author zhangbo
     * @Date 2019/8/16
     *  URL 解码
     */



    public static String getURLDecoderString(String str) {
       final  String ENCODE = "GBK";
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }




}
