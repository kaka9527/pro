package com.tin.it.thread;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.util.Constants;
import com.tin.it.util.MessageStringDLTUtils;
import com.tin.it.util.StringUtil;
import com.tin.it.vo.MessageDLT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 校时 设置时间报文
 */
public class PduTimeSettingTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PduTimeSettingTask.class);

    private MqttMessageClient mqttMessageClient;
    private String machineId = null;
    private String moduleId;

    public PduTimeSettingTask(MqttMessageClient mqttMessageClient, String machineId) {
        this.mqttMessageClient = mqttMessageClient;
        this.moduleId = machineId;
        this.machineId = MessageStringDLTUtils.addZeroForNumLeft(machineId, 12);;
    }

    @Override
    public void run() {
        String end = "0D0A";
        try {
            LocalDateTime localDateTime = LocalDateTime.now();
            int hour = localDateTime.getHour();
            int minute = localDateTime.getMinute();
            int second = localDateTime.getSecond();

            String hourhex = StringUtil.addZeroForNum(String.valueOf(hour), 2);
            String minutehex = StringUtil.addZeroForNum(String.valueOf(minute), 2);
            String secondhex = StringUtil.addZeroForNum(String.valueOf(second), 2);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //设置时间数据标
            String datetimeTab = "04000102";
            //时间校时数据
            String datatime = hourhex + minutehex + secondhex;

            MessageDLT messageDLTdatatime = new MessageDLT();
            messageDLTdatatime.setMachineAddress(machineId);
            messageDLTdatatime.setControl("14");
            messageDLTdatatime.setDataTab(datetimeTab);
            messageDLTdatatime.setPassword(Constants.DLT_PASSWORD);
            messageDLTdatatime.setAuth(Constants.DLT_CONTROL);
            messageDLTdatatime.setDataStr(datatime);

            //组合写入对时设置报文
            String datatiemhex = MessageStringDLTUtils.messageToHex(messageDLTdatatime, "write");

            if (!machineId.substring(0, 3).equals("180")) {
                datatiemhex = datatiemhex + end;//非空开设备发送消息加0D0A
            }
            SleepUtils.second(1000L);

            logger.info("发送时间报文===" + formatter.format(LocalDateTime.now()) +" "+ datatiemhex);
            writeMsgToClient(datatiemhex);//重新发送时间报文
        } catch (Exception e) {
            logger.error(" 校时 时间报文发送失败 ",e);
        }
    }

    /**
     * 发送信息
     */
    private void writeMsgToClient(String msg) throws IOException {
        try {
            byte[] ss = StringUtil.hexStringToByteArray(msg);
            this.mqttMessageClient.sendMessage(Constants.TOPIC_GATEWAY_REQUEST+this.moduleId,ss);
            //mqttMessageClient.writeAndFlush(Unpooled.copiedBuffer(ss));
        }catch (Exception e){
            logger.error(" 报文发送失败 ",e);
        }
    }
}
