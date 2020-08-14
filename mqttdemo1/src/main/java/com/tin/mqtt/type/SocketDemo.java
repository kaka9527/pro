package com.tin.mqtt.type;

import com.tin.util.HexUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.mqtt.*;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.fusesource.hawtdispatch.transport.ProtocolCodec;
import org.fusesource.hawtdispatch.transport.TcpTransport;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class SocketDemo {
    int maxReadRate;
    int maxWriteRate;
    int receiveBufferSize = 65536;
    int sendBufferSize = 65536;
    boolean closeOnCancel = true;
    boolean keepAlive = true;
    protected ProtocolCodec codec;
    int trafficClass = 8;

    public static void main(String[] args) {
        DataOutputStream out = null;
        InputStream dis = null;
        try {
            Socket socket = new Socket("192.168.10.80",1883);
            socket.setKeepAlive(true);
            socket.setTrafficClass(8);
            //
            String msg = "";
            Connect connect = new Connect("chuangkou","chuangkou88",false,30,"clientId8899");
            msg = connect.getConnectMsg();

            //服务器向客户端写入数据
            out = new DataOutputStream(socket.getOutputStream());
            //
            dis = socket.getInputStream();
            // 连接报文
            sendMsg(out, dis, msg, 1L);

            // 心跳报文
            PingReq pingReq = new PingReq();
            msg = pingReq.getPingReqMsg();
            sendMsg(out, dis, msg, 10L);

            // 发布消息报文
            Publish publish = new Publish("gateway/request/test111","test333333");
            msg = publish.getPublishMsg();
            sendMsg(out, dis, msg, 3L);

            // 订阅报文
            Subscribe subscribe = new Subscribe("clientId8899","gateway/request/test",1);
            msg = subscribe.getSubscribeMsg();
            sendMsg(out, dis, msg, 3L);

            // 取消订阅
            UnSubscribe unSubscribe = new UnSubscribe("gateway/request/test");
            msg = unSubscribe.getUnSubscribeMsg();
            sendMsg(out, dis, msg, 3L);

            // 断开连接报文
            DisConnect disConnect = new DisConnect();
            msg = disConnect.getDisConnectMsg();
            sendMsg(out, dis, msg, 1L);

            //
            out.close();
            //
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(dis != null){
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void sendMsg(DataOutputStream out, InputStream ins, String msg, long second){
        //服务器向客户端写入数据
        try {
            if(msg != null && !"".equals(msg)){
                byte[] m = HexUtil.hexStringToBytes(msg);
                if(out != null){
                    out.write(m,0,m.length);
                    out.flush();
                }
                TimeUnit.SECONDS.sleep(second);
                if(ins != null){
                    int read = ins.read();
                    System.out.println("read1: "+Integer.toHexString(read));
                    System.out.println("read2: "+Integer.toBinaryString(read));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*static class SocketThread implements Runnable{
        private DataOutputStream out;
        private InputStream ins;
        private String msg;
        private long second;

        public SocketThread(DataOutputStream out, InputStream ins, String msg, long second) {
            this.out = out;
            this.ins = ins;
            this.msg = msg;
            this.second = second;
        }

        @Override
        public void run() {
            //服务器向客户端写入数据
            try {
                if(this.msg != null && !"".equals(msg)){
                    byte[] m = HexUtil.hexStringToBytes(msg);
                    if(out != null){
                        out.write(m,0,m.length);
                        out.flush();
                    }
                    TimeUnit.SECONDS.sleep(second);
                    if(ins != null){
                        int read = ins.read();
                        System.out.println("read1: "+Integer.toHexString(read));
                        System.out.println("read2: "+Integer.toBinaryString(read));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

}
