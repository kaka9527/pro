package com.tin.util;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;

/**
 * @author zhangbo
 * @Date 2019/7/2
 * Server4HelloWorldUtil
 */
@Component
public class GetForChannel {

    //通过管道发送信息til
    public   void sendMsgToClient(Channel channel, String msg)  {
        try {
            byte[] ss = StringUtil.hexStringToByteArray(msg);
            channel.writeAndFlush(ss);
        }catch (Exception E){
            System.out.println("报文发送失败");
            E.printStackTrace();

        }
    }
    //通过管道获取socket地址
    public   String selectSocketIpAndHost (Channel channel){
        //socket地址
        SocketAddress socketAddress1 = channel.remoteAddress();
        String address = socketAddress1.toString().substring(1, socketAddress1.toString().length());
        return address;
    }
    public   String selectSocketIp (Channel channel){
        //socket地址
        SocketAddress socketAddress1 = channel.remoteAddress();
        String address = socketAddress1.toString().substring(1, socketAddress1.toString().length());
        String[] split = address.split(":");
        String ip = split[0].toString();
        return ip;
    }
    public   String selectSocketHost (Channel channel){
        //socket地址
        SocketAddress socketAddress1 = channel.remoteAddress();
        String address = socketAddress1.toString().substring(1, socketAddress1.toString().length());
        String[] split = address.split(":");
        String port= split[1].toString();
        return port;
    }
}
