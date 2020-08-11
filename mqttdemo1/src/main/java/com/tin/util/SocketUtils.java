package com.tin.util;


import io.netty.buffer.Unpooled;

import java.io.*;

/**
 * @Author:
 * @Description:
 * @Date:Created in 10:49 2018/4/22
 */
public class SocketUtils {

    /**
     * 响应客户端信息
     *

     * @param string
     */
    public static void writeMsgToClient(io.netty.channel.Channel channel, String string) throws IOException {

        try {
            byte[] ss = StringUtil.hexStringToByteArray(string);
            channel.writeAndFlush(Unpooled.copiedBuffer(ss));
        }catch (Exception E){
            System.out.println("报文发送失败");
            E.printStackTrace();

        }

    }


    public static void writeMsgToAcrel(io.netty.channel.Channel channel, String string) throws IOException {
        try {
            byte[] bytes = StringUtil.hexStringToByteArray(string);
            channel.writeAndFlush(Unpooled.copiedBuffer(bytes));
        }catch (Exception E){
            System.out.println("报文发送失败....");
            E.printStackTrace();

        }
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
     * 读取客户端信息
     *
     * @param inputStream
     */
    public static String readMessageFromClient(InputStream inputStream) throws IOException {
        String message = "";
        try {
            byte[] bytes = null;
			Thread.sleep(500);
            int bufflenth = inputStream.available();
//            System.out.println("bufflenth====" + bufflenth);

            if (bufflenth > 0) {
                while (bufflenth != 0) {
					Thread.sleep(200);
                    // 初始化byte数组为buffer中数据的长度
//                    System.out.println("bufflenth====" + bufflenth);
                    bytes = new byte[bufflenth];
                    inputStream.read(bytes);
                    bufflenth = inputStream.available();
//                    System.out.println("循环接收报文===" + str2HexStr(bytes));
                }
//                System.out.println("接收数据时间===" + System.currentTimeMillis());
                message = str2HexStr(bytes);
//                System.out.println(message);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
//            inputStream.close();
            e.printStackTrace();
        } finally {
//            try {
//                if (inputStream != null) {
//                	inputStream.close();
//                	inputStream = null;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return message;
    }


    /**
     * 读取客户端信息
     *
     * @param inputStream
     */
    public static String readMessageFromClientInternet(InputStream inputStream) throws IOException {
        String message = "";
        try {
            byte[] bytes = null;
            int bufflenth = inputStream.available();

            if (bufflenth > 0) {
                while (bufflenth != 0) {
                    Thread.sleep(200);
                    // 初始化byte数组为buffer中数据的长度
//                    System.out.println("bufflenth====" + bufflenth);
                    bytes = new byte[bufflenth];
                    inputStream.read(bytes);
                    bufflenth = inputStream.available();
                }
                message = str2HexStr(bytes);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
//            inputStream.close();
            e.printStackTrace();
        }
        return message;
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

    public static String readMessageFromClientTwo(InputStream inputStream) throws IOException {
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader br=new BufferedReader(reader);
        String a = null;
        while((a=br.readLine())!=null){
            System.out.println(a);
            break;
        }

        return a;
    }
}
