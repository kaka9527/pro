package com.tin.activemq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestMain {
    private String filePath = "D:/upload/1595492436354.bin";
    private static final Logger LOG = LoggerFactory.getLogger(TestMain.class);


    private final static int PACKET_SIZE = 256;

    public static void main(String[] args) {
        TestMain testMain = new TestMain();
        testMain.getHeadMessage();
    }

    public void getHeadMessage(){
        File file = new File(filePath);
        if(file.exists() && file.isFile()){
            FileInputStream fis = null;
            DataInputStream dis = null;
            try {
                fis = new FileInputStream(file);
                dis = new DataInputStream(fis);
                //62280
                int len = (int)file.length();
                int y = len / PACKET_SIZE;
                int s = len % PACKET_SIZE;
                if(s > 0 && s < PACKET_SIZE){
                    y = y + 1;
                }
                LOG.info("包数："+ y);
                byte [] bytes = new byte[PACKET_SIZE];
                //byte [] bytes = new byte[dis.available()];
                //dis.read(bytes);
                int n = 0;
                int x = 0;
                while ((n = dis.read(bytes)) != -1){
                    System.out.println("-----------------"+n);
                    if(n < PACKET_SIZE){

                    }
                    x=x+1;
                }
                LOG.info("---bytes length---"+x);

                dis.close();
                fis.close();
            } catch (Exception e) {
                LOG.error("读取bin文件异常");
            } finally {
                try {
                    if (null != dis) {
                        dis.close();
                    }
                } catch (IOException e) {
                    LOG.error("关闭流异常1");
                }
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException e) {
                    LOG.error("关闭流异常2");
                }
            }
        }
    }

    // 组包
    public void setDataMessage(byte[] bytes){

    }
}
