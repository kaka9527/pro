package com.tin.thread;

import com.tin.thread.JobPduDateTimeSetThread;
import com.tin.util.*;
import com.tin.vo.MessageDLT;
import io.netty.channel.Channel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;

/**
 * @Description:
 */
public class IAPAllServerThread implements Callable<String> {
    // 和本线程相关的Socket
    Channel socket = null;
    public static String readstr = "";
    String type = "";
    String version = "";
    String fileurl = "";
    String machineid = "";

    ExecutorService singleThreadExecutor = new ThreadPoolExecutor(1,1,1000, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(),Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    public IAPAllServerThread(Channel socket, String version, String fileurl, String type,String machineid) {
        this.socket = socket;
        this.version = version;
        this.fileurl = fileurl;
        this.type = type;
        this.machineid = machineid;
    }

    // 线程执行的操作，响应客户端的请求
    @Override
    public String call() throws Exception {

        //获取最新的版本号
        version = version.replace(".", "");
        version = StringUtil.addZeroForNum(version, 4);

        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        executorService.setKeepAliveTime(20,TimeUnit.SECONDS);
        CompletionService<String> completionService = new ExecutorCompletionService(executorService);

        GetForChannel getForChannel = new GetForChannel();
        String conName = getForChannel.selectSocketIp(socket);

        String conPort = getForChannel.selectSocketHost(socket);
        try {
            String pdumesg = "68010011000100681104333a3239D0160D0A";
            String pdureadmesg = "FEFEFEFE68010011000100689104333A32395016";

            String machineaddress = "";
            String readmesg = "";
            int num = 0;
            // 循环接收客户端的消息
            while (true) {
                // 读取客户端内容
                if (!type.equals("180")) {
                    System.out.println("发送PDU设备报文===" + pdumesg);
                    SocketUtils.writeMsgToClient(socket, pdumesg);
                    machineaddress = "010011000100";
                    readmesg = pdureadmesg;

                    if (readstr.equals("")) {
                        MessageDLT message = new MessageDLT();
                        message.setMachineAddress(machineaddress);

                        message.setDataTab("333A3239");

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String datetime = df.format(System.currentTimeMillis());
                        message.setCollectionTime(datetime);
                        message.setIp(conName);
                        message.setPort(conPort);

                        //readstr = pduMessageService.selectByMachineAddress(message);
                    }
                    System.out.println("readstr===" + readstr);
                    num++;
                    File file = new File(fileurl);

                    if (!readstr.equals("") && readstr.equals(readmesg)) {

                        Thread.sleep(5000);
                        String ifone = PduIAPUtils.SendYModemHead(file, socket);

                        if (ifone.equals("06")) {
                            String iftwo = PduIAPUtils.SendYModemData(file, socket);
                            System.out.println("更新程序返回结果" + iftwo);
                            if (iftwo.equals("06")) {
                                System.out.println("更新成功！");
                                Constants.IAPmesg = "";
                                //MessageDLT messageDLT = MessageStringDLTUtils.onlineMessage(readstr);
                                //String machineid = MessageStringDLTUtils.machineAddressHexOpposite(messageDLT.getMachineAddress());
                                // 校时
                                //singleThreadExecutor.submit(new JobPduDateTimeSetThread(socket, machineid));
                                return "ok";
                            }
                            if (iftwo.equals("20")) {
                                System.out.println("更新失败！");
                                return "notok";
                            }

                        }

                        if (ifone.equals("15")) {
                            System.out.println("发送数据有误，重新发送！");
                            return "notok";
                        }

                        if (ifone.equals("18")) {
                            System.out.println("取消更新！更新包过大！");
                            return "notok";
                        }

                        if (ifone.equals("20")) {
                            System.out.println("程序问题，取消更新！");
                            return "notok";
                        }
                    }
                    num++;
                    if (num == 20) {
                        System.out.println("设备没有回复更新请求！");
                        return "notok";
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "notok";
    }
}
