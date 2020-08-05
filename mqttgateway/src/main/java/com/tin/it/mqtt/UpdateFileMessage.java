package com.tin.it.mqtt;

import com.tin.it.util.Constants;
import com.tin.it.util.MessageStringDLTUtils;
import com.tin.it.util.StringUtil;
import com.tin.it.vo.MessageDLT;
import com.tin.it.vo.MessageUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class UpdateFileMessage {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateFileMessage.class);

    private final static int PACKET_SIZE = 2000;

    /**
     * 检查版本报文
     */
    public static String getCheckVersionMessage(String moduleId, String version){
        //获取最新的版本号
        String vn = version.replace(".", "");
        vn = StringUtil.addZeroForNum(vn, 4);
        // 版本比较报文
        MessageDLT msgDlt = new MessageDLT();
        msgDlt.setMachineAddress(moduleId);
        msgDlt.setControl("14");
        msgDlt.setDataTab("0400040F");
        msgDlt.setPassword(Constants.DLT_PASSWORD);
        msgDlt.setAuth(Constants.DLT_CONTROL);
        msgDlt.setDataStr(vn);
        //组合写入对时设置报文
        String datahex = MessageStringDLTUtils.messageToHex(msgDlt, "write");
        LOG.info("datahex=="+datahex);
        return datahex;
    }

    /**
     * 更新请求报文（固定） 数据标：42373337
     * @return
     */
    public static String getUpdateRequestMessage(String moduleId){
        String machineAddress = moduleId;
        machineAddress = UpdateMessageUtil.addZeroForNumLeft(machineAddress, 12);
        machineAddress = UpdateMessageUtil.machineAddressHex(machineAddress);
        String pduMsg = "68"+machineAddress+"68110442373337D016";
        return pduMsg;
    }

    /**
     * 获取bin文件数据
     * @param filePath
     * @return
     */
    public static Hashtable<String,byte[]> getBinData(String filePath){
        File file = new File(filePath);
        Hashtable<String,byte[]> binData = null;
        if(file.exists() && file.isFile()){
            FileInputStream fis = null;
            DataInputStream dis = null;
            try {
                fis = new FileInputStream(file);
                dis = new DataInputStream(fis);
                // 文件大小
                int len = (int)file.length();

                // 计算总包数
                int y = len / PACKET_SIZE;
                int s = len % PACKET_SIZE;
                if(s > 0 && s < PACKET_SIZE){
                    y = y + 1;
                }
                LOG.info("包数："+ y);

                // 数据包
                binData = new Hashtable<>(y);
                byte [] bytes = new byte[PACKET_SIZE];
                int n = 0;
                int x = 0;
                String msg = "";
                while ((n = dis.read(bytes)) != -1){
                    x = x + 1;
                    String key = String.valueOf(y) + "_"+x;
                    binData.put(key,bytes);
                }
                // close
                dis.close();
                fis.close();
            } catch (Exception e) {
                LOG.error("读取bin文件异常",e);
            } finally {
                try {
                    if (null != dis) {
                        dis.close();
                    }
                } catch (IOException e) {
                    LOG.error("关闭流异常1",e);
                }
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException e) {
                    LOG.error("关闭流异常2",e);
                }
            }
        }
        return binData;
    }

    public static HashMap<String,String> getDataMessage(String filePath,String moduleId){
        Hashtable<String, byte[]> binData = getBinData(filePath);
        HashMap<String,String> msgMap = new HashMap<>();
        int size = binData.size();
        String sizeStr = String.valueOf(size);
        int x = 0;
        String msg = "";
        //
        Iterator<Map.Entry<String, byte[]>> datas = binData.entrySet().iterator();
        while (datas.hasNext()) {
            Map.Entry<String, byte[]> entry = datas.next();
            x = x + 1;
            String key = sizeStr + "_" + x;
            if(x < size){
                msg = setDataMessage(entry.getValue(),"D2",sizeStr,String.valueOf(x),moduleId);
            }else {
                // 最后一包
                msg = setDataMessage(entry.getValue(),"92",sizeStr,String.valueOf(x),moduleId);
            }
            msgMap.put(key,msg);
        }
        return msgMap;
    }

    /**
     * 设置数据包
     * @param bytes
     * @param control
     * @param packetTotal
     * @param packet
     * @param moduleId
     * @return
     */
    private static String setDataMessage(byte[] bytes,String control,String packetTotal,String packet,String moduleId){
        MessageUpdate msgUpd = new MessageUpdate();
        msgUpd.setMachineAddress(moduleId);
        msgUpd.setControl(control);
        msgUpd.setDataTab("04000417");
        msgUpd.setPacketTotal(packetTotal);
        msgUpd.setPacket(packet);
        msgUpd.setDataStr(MessageHex.byteArrToHex(bytes));
        String msgHex = UpdateMessageUtil.updateMessageToHex(msgUpd);
        LOG.info(msgHex);
        return msgHex;
    }
}
