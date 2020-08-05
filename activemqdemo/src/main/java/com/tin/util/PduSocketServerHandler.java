package com.tin.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tin.thread.JobPduDateTimeSetThread;
import com.tin.vo.MessageDLT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.util.concurrent.*;

@ChannelHandler.Sharable
public class PduSocketServerHandler extends ChannelInboundHandlerAdapter {
    ExecutorService singleThreadExecutor = new ThreadPoolExecutor(1,1,1000, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    /*
     * channelAction
     * channel 通道 action 活跃的
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().localAddress().toString() + " 通道已激活！");
    }

    /*
     * channelInactive
     * channel 通道 Inactive 不活跃的
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().localAddress().toString() + " 通道不活跃！");
        // 关闭流
        if(!singleThreadExecutor.isShutdown()){
            singleThreadExecutor.shutdown();
        }
    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 第一种：接收字符串时的处理
        ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
        result.readBytes(result1);
        String s = str2HexStr(result1);

        heartData(s);

        System.out.println("客户端收到服务器数据:" + s);
        // 释放资源
        result.release();
        ctx.flush();

    }

    /**
     * 功能：读取完毕客户端发送过来的数据之后的操作
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if(!singleThreadExecutor.isShutdown()){
            JobPduDateTimeSetThread thread = new JobPduDateTimeSetThread(ctx.channel(), "000100000035");
            singleThreadExecutor.execute(thread);
            // 退出
            singleThreadExecutor.shutdown();
        }
        //System.out.println("aa=="+singleThreadExecutor.isTerminated());
        //System.out.println("bb=="+singleThreadExecutor.isShutdown());
        System.out.println("服务端接收数据完毕..");

        if(idx ==3){
            shutdown(ctx.channel(),"000100000035");
        }
        idx = idx + 1;
    }

    private int idx = 0;

    /**
     * 功能：服务端发生异常的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("异常信息：\r\n" + cause.getMessage());
    }

    public String str2HexStr(byte[] bs) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    // 远程关闸
    public void shutdown(Channel channel,String machineId) {
        MessageDLT msg = new MessageDLT();
        msg.setMachineAddress(machineId);
        msg.setControl("1C");
        msg.setPassword(Constants.DLT_PASSWORD);
        msg.setAuth(Constants.DLT_CONTROL);
        msg.setDataTab("06010101");
        msg.setDataStr("0000");
        String datatiemhex = MessageStringDLTUtils.messageToHex(msg, "write");
        //System.out.println("datatiemhex=="+datatiemhex);
        datatiemhex = "68350000000100681C1035434343444444445E33838443393C4A9516";
        try {
            SocketUtils.writeMsgToClient(channel,datatiemhex);
            System.out.println("远程关闸...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void heartData(String message){
        try{
            int heartTab = message.indexOf("3C3C3239");
            if (heartTab> -1) {
                String msg = message.substring(heartTab + 8, message.length() - 4);
                if (msg.length() > 22) {
                    String dataStr = msg.substring(22);
                    //获取电流
                    String current = dataStr.substring(0, 6);
                    current = MessageStringDLTUtils.receiverVlue(current);
                    float currentF = Float.parseFloat(current);
                    currentF = (float) Math.round(currentF) / 1000;
                    System.out.println("电流：" + currentF);

                    /*// 总电流值
                    String current1 = dataStr.substring(6, 12);
                    current1 = MessageStringDLTUtils.receiverVlue(current1);
                    float currentF1 = Float.parseFloat(current1);
                    currentF1 = (float) Math.round(currentF1) / 1000;
                    System.out.println("总电流值：" + currentF1);
                    // 电压值
                    String current2 = dataStr.substring(12, 16);
                    current2 = MessageStringDLTUtils.receiverVlue(current2);
                    float currentF2 = Float.parseFloat(current2);
                    currentF2 = (float) Math.round(currentF2) / 1000;
                    System.out.println("电压值：" + currentF2);

                    // 有功功率值
                    String current3 = dataStr.substring(16, 22);
                    current3 = MessageStringDLTUtils.receiverVlue(current3);
                    float currentF3 = Float.parseFloat(current3);
                    currentF3 = (float) Math.round(currentF3) / 1000;
                    System.out.println("有功功率值：" + currentF3);
                    */
                    // 有功功率值
                    /*String timeStr = dataStr.substring(dataStr.length() - 12);
                    timeStr = timeStr.substring(6);
                    //System.out.println("时间1：" + timeStr);
                    String hour = timeStr.substring(0, 2);
                    String minute = timeStr.substring(2, 4);
                    String second = timeStr.substring(4);
                    timeStr = hour + ":" + minute + ":" + second;
                    System.out.println("时间：" + timeStr);
*/
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //3C3C3239 55555555 01 FFFFFFFEFFFF 453333393333855633333333333333533A4B436A636366A916
        String msg = "3C3C32395555555501FFFFFFFEFFFF453333393333855633333333333333533A4B436A636366A916";
        heartData(msg);
    }
}
