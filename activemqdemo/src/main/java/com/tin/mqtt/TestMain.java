package com.tin.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestMain {
    private static final Logger LOG = LoggerFactory.getLogger(TestMain.class);

    private String filePath = "D:/upload/1595492436354.bin";

    private final static int PACKET_SIZE = 2000;

    public static void main(String[] args) {
        TestMain testMain = new TestMain();
//        String rt = "68880000100200689401010016";
        String rt = "6801001100010068910442373337015016";
//        String rt = "680100110001006891074237333701600001155016";
        testMain.retryMessage(rt,2);
    }

    private boolean retryMessage(String rtMsg,int type){
        boolean bool = false;
        String rt0 = "";
        String rt1 = "";
        if(type == 1){
            rt0 = getRetryStr(rtMsg,0,2);
            rt1 = getRetryStr(rtMsg,4,6);
            if("94".equals(rt0) && "01".equals(rt1)){
                //
                System.out.println("---1--true------");
                bool = true;
                return bool;
            }
        }
        if(type == 2){
            rt0 = getRetryStr(rtMsg,0,2);
            rt1 = getRetryStr(rtMsg,12,14);
            if("91".equals(rt0) && "01".equals(rt1)){
                //
                System.out.println("---2--true------");
                bool = true;
                return bool;
            }
        }
        if(type == 3){
            rt0 = getRetryStr(rtMsg,0,2);
            rt1 = getRetryStr(rtMsg,20,22);
            if("91".equals(rt0) && "15".equals(rt1)){
                //
                System.out.println("---3--true------");
                bool = true;
                return bool;
            }
        }
        System.out.println(rt0);
        System.out.println(rt1);

        return bool;
    }

    private String getRetryStr(String rtMsg,int startIndex,int endIndex){
        String msg = rtMsg;
        if(null != rtMsg && rtMsg.length() > 16){
            // 68 880000100200 68 94 01 01 00 16
//        01：可以更新
//        00：不更新
            msg = rtMsg.substring(16);
            msg = msg.substring(startIndex,endIndex);


            // 68 010011000100 68 91 04 423733375016
//        01准备好
//        00没有准备好

            // 68 010011000100 68 91 07	42373337 0160 0001 15 50 16
            /**
             * 接收成功ACK=0X15
             * 接收失败NCK=0X06
             */


        }
        return msg;
    }
}
