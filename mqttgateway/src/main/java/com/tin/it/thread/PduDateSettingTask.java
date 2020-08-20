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
 * 校时设置日期报文
 */
public class PduDateSettingTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PduDateSettingTask.class);

    private MqttMessageClient mqttMessageClient;
    private String machineId = null;
    private String moduleId;

    public PduDateSettingTask(MqttMessageClient mqttMessageClient, String machineId) {
        this.mqttMessageClient = mqttMessageClient;
        this.moduleId = machineId;
        this.machineId = MessageStringDLTUtils.addZeroForNumLeft(machineId, 12);;
    }

    @Override
    public void run() {
        String end = "0D0A";
        try {
            LocalDateTime localDateTime = LocalDateTime.now();
            int year = localDateTime.getYear();
            int month = localDateTime.getMonthValue();
            int day = localDateTime.getDayOfMonth();
            int whatdayInt = localDateTime.getDayOfWeek().getValue();

            String yearhex = StringUtil.addZeroForNum(String.valueOf(year), 2);
            String monthhex = StringUtil.addZeroForNum(String.valueOf(month), 2);
            String dayhex = StringUtil.addZeroForNum(String.valueOf(day), 2);
            String whatdayhex = StringUtil.addZeroForNum(String.valueOf(whatdayInt), 2);
            String dateTab = "04000101"; //设置日期数据标
            //日期校时数据
            String data = yearhex.substring(2) + monthhex + dayhex + whatdayhex;

            MessageDLT messageDLTdata = new MessageDLT();
            messageDLTdata.setMachineAddress(machineId);
            messageDLTdata.setControl("14");
            messageDLTdata.setDataTab(dateTab);
            messageDLTdata.setPassword(Constants.DLT_PASSWORD);
            messageDLTdata.setAuth(Constants.DLT_CONTROL);
            messageDLTdata.setDataStr(data);
            //组合写入对时设置报文
            String datahex = MessageStringDLTUtils.messageToHex(messageDLTdata, "write");

            if (!machineId.substring(0, 3).equals("180")) {
                //非空开设备发送消息加0D0A
                datahex = datahex + end;
            }
            //68350000000100681411 34343337 35434343 44444444 394b3a5353 6f 160D0A
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            logger.info("发送日期报文时间===" + formatter.format(LocalDateTime.now()) +" "+ datahex);
            //重新发送报文
            writeMsgToClient(datahex);
        } catch (Exception e) {
            logger.error(" 校时 日期报文发送失败 ",e);
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
