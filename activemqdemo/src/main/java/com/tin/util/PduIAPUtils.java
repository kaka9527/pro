package com.tin.util;

import io.netty.channel.Channel;

import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;


/**
 *
 */
public class PduIAPUtils {

    /**
     *@Author:
     *@Description: 发送头文件第一帧
     *@Date:
     */
    public static String SendYModemHead(File file, Channel socket) {
        String start = "";
        try {
            String[] st = new String[1027];
            int numone = 0;
            while (true) {
                st[0] = "01";
                st[1] = "00";

                String s2 = Integer.toHexString(~Integer.valueOf(st[1]));
                st[2] = s2.substring(s2.length() - 2, s2.length());

                String filelenth = getFileInput(file);
                String[] fl = filelenth.split("\\.");
                int kb = Integer.valueOf(fl[0]) + 1;

                System.out.println("文件占用空间" + kb + "KB");

                st[3] = StringUtil.addZeroForNum(Integer.toHexString(kb), 2);
                System.out.println("文件占用空间" + st[3] + "KB");
                for (int s = 4; s < st.length; s++) {
                    st[s] = "ff";

                }

                String end = "0d0a";

                String msg = "";

                for (int m = 0; m < st.length; m++) {

                    msg += st[m];
                }
                msg = msg + end;
                System.out.println("第一帧头文件==== " + msg);

                int num = 0;
                while (true) {
                    Thread.sleep(100);
                    SocketUtils.writeMsgToClient(socket,msg);

                    Thread.sleep(500);
                    start = Constants.IAPmesg;
                    System.out.println("第一帧返回==== " + start);
                    if (!start.equals("")){
                        break;
                    }
                    num++;
                    if (num == 10){
                        return "20";
                    }
                }
                int ack;
                int nck;
                int ck;

                ack = start.indexOf("06");
                if (ack != -1) {
                    return "06";
                }

                nck = start.indexOf("15");
                if (nck != -1) {
                    System.out.println("发送数据错误重新发送！");
                }

                ck = start.indexOf("18");
                if(ck != -1){
                    return "18";
                }

                if (numone == 10) {
                    System.out.println("程序问题，更新失败");
                    return "20";

                }
                numone++;

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return start;
    }

    public static String SendYModemData(File file, Channel socket) {

        String dataSuccess = "";
        String remesg = "";

        try {
            FileInputStream in = new FileInputStream(file);
            int step = 0;
            int datalen;
            byte[] filedata = new byte[1024];
            // boolean nak = false;
            boolean stop = false;
            while ((datalen = in.read(filedata)) == 1024) {
                step++;
                String[] stream = new String[1029];
                int numone = 0;
                while (true) {

                    stream[0] = "02";
                    String stepstr = Integer.toHexString(step);
                    stream[1] = StringUtil.addZeroForNum(String.valueOf(stepstr), 2);
                    // String s2 =
                    // Integer.toHexString(~Integer.valueOf(stream[1]));
                    String s2 = Integer.toHexString(~step);
                    stream[2] = s2.substring(s2.length() - 2, s2.length());
                    for (int i = 0; i < 1024; i++) {

                        stream[i + 3] = Byte.toString(filedata[i]);
                        String temp = Integer.toHexString(Integer.valueOf(stream[i + 3]));
                        temp = StringUtil.addZeroForNum(temp, 2);
                        stream[i + 3] = temp.substring(temp.length() - 2, temp.length());
                    }
                    stream[1027] = "0d";
                    stream[1028] = "0a";

                    // 组合报文内容
                    String sendData = "";
                    for (int i = 0; i < stream.length; i++) {
                        sendData += stream[i];
                    }
                    System.out.println("发送数据===" + sendData);

                    int ack;
                    int nck;
                    int num = 0;
                    while (true) {
                        // 发送报文内容
                        SocketUtils.writeMsgToClient(socket,sendData);
                        Thread.sleep(1000);

                        remesg = Constants.IAPmesg;

                        System.out.println("第"+step+"帧返回==== " + remesg);
                        if (!remesg.equals("")){
                            break;
                        }
                        num++;
                        if (num == 10){
                            return "20";
                        }
                    }

                    ack = remesg.indexOf("06");
                    if (ack != -1) {
                        break;
                    }
                    nck = remesg.indexOf("15");
                    if (nck != -1) {
                        System.out.println("发送数据错误重新发送！");
                    }

                    if (numone == 10) {
                        System.out.println("程序问题，更新失败");
                        dataSuccess = "20";
                        return dataSuccess;

                    }

                }

            }

            step++;
            // 发送y-modem文件身体
            if (datalen < 1024) {
                int numtwo = 0;
                while (true) {
                    String[] stream = new String[1029];
                    stream[0] = "03";
                    String stepstr = Integer.toHexString(step);
                    stream[1] = StringUtil.addZeroForNum(String.valueOf(stepstr), 2);
                    String s2 = Integer.toHexString(~step);

                    stream[2] = s2.substring(s2.length() - 2, s2.length());

                    for (int i = 0; i < datalen; i++) {

                        stream[i + 3] = Byte.toString(filedata[i]);
                        String temp = Integer.toHexString(Integer.valueOf(stream[i + 3]));
                        temp = StringUtil.addZeroForNum(temp, 2);
                        stream[i + 3] = temp.substring(temp.length() - 2, temp.length());

                    }
                    for (int i = datalen; i < 1024; i++) {
                        stream[i + 3] = "ff";
                    }

                    stream[1027] = "0d";
                    stream[1028] = "0a";
                    //
                    // 组合报文内容最后一帧
                    System.out.println("");
                    String sendDataOne = "";
                    for (int i = 0; i < stream.length; i++) {
                        sendDataOne += stream[i];

                    }
                    // 发送报文内容
                    System.out.println("sendDateOne===" + sendDataOne);

                    int ack;
                    int nck;
                    int num = 0;
                    while (true) {
                        SocketUtils.writeMsgToClient(socket,sendDataOne);
                        Thread.sleep(1000);

                        remesg = Constants.IAPmesg;

                        System.out.println("第"+step+"帧返回==== " + remesg);
                        if (!remesg.equals("")){
                            break;
                        }
                        num++;
                        if (num == 10){
                            return "20";
                        }
                    }

                    ack = remesg.indexOf("06");
                    if (ack != -1) {
                        dataSuccess = "06";
                        return dataSuccess;
                    }
                    nck = remesg.indexOf("15");
                    if (nck != -1) {
                        System.out.println("发送数据错误重新发送！");
                    }

                    if (numtwo == 10) {
                        System.out.println("程序问题，更新失败");
                        dataSuccess = "20";
                        return dataSuccess;

                    }

                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return dataSuccess;
    }



    public static String getFileInput(File f) {

        FileChannel fc = null;
        String filelenth = "";
        try {

            if (f.exists() && f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                fc = fis.getChannel();
                System.out.println(fc.size() + " 字节");
                filelenth = getPrintSize(fc.size());
                System.out.println(getPrintSize(fc.size()));

            } else {
                // logger.info("file doesn't exist or is not a file");
                System.out.println("file doesn't exist or is not a file");
            }
        } catch (FileNotFoundException e) {
            // logger.error(e);
            e.printStackTrace();
            System.err.println(e);
        } catch (IOException e) {
            // logger.error(e);
            System.err.println(e);
            e.printStackTrace();
        } finally {
            if (null != fc) {
                try {

                    fc.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
        return filelenth;

    }


    public static String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        double value = (double) size;
        if (value < 1024) {
            return String.valueOf(value) + "B";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (value < 1024) {
            return String.valueOf(value) + "KB";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        if (value < 1024) {
            return String.valueOf(value) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return String.valueOf(value) + "GB";
        }
    }
}
