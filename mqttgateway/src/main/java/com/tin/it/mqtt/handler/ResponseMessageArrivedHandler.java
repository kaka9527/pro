package com.tin.it.mqtt.handler;

import com.tin.it.mqtt.MqttMessageClient;
import com.tin.it.thread.PduDateSettingTask;
import com.tin.it.thread.PduTimeSettingTask;
import com.tin.it.thread.ResponseMessageThread;
import com.tin.it.util.ControlCode;
import com.tin.it.util.PacketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * 处理返回消息
 */
public class ResponseMessageArrivedHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseMessageArrivedHandler.class);

    ExecutorService threadPool = new ThreadPoolExecutor(1,1,0, TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    private ExecutorService subResponseExecutor;
    private LinkedBlockingQueue   subResponseQueue;

    @Resource
    MqttMessageClient mqttMessageClient;

    public ResponseMessageArrivedHandler(){
        int coreThreadNum = Runtime.getRuntime().availableProcessors();
        logger.info("coreThreadNum: "+coreThreadNum);
        this.subResponseQueue = new LinkedBlockingQueue(10000);
        this.subResponseExecutor = new ThreadPoolExecutor(coreThreadNum * 2,
                coreThreadNum * 2,
                30000,
                TimeUnit.MILLISECONDS,
                subResponseQueue,
                new ThreadFactoryImpl("SubResponseThread"),
                new RejectHandler("subResponse", 10000));
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();
        logger.info(headers.toString());
        String msg = message.getPayload().toString();
        logger.info(msg);
        String topic = headers.get("mqtt_receivedTopic").toString();
        // 判断控制码
        checkResponseMessageType(msg);
        logger.info(topic);
    }

    private final String RESP_CODE_00 = "00";
    private final String RESP_CODE_01 = "01";
    private final String RESP_CODE_10 = "10";
    private final String RESP_CODE_11 = "11";

    private void checkResponseMessageType(String message){
        // 控制码
        String controlCode = PacketUtil.getControlCode(message);
        // 设备ID
        String deviceCode = PacketUtil.getDeviceCode(message);
        // 写数据成功设备上报的其他数据
        if(ControlCode.CODE_B4.equals(controlCode)){
            // 更新软件包主题返回的消息
            subResponseExecutor.submit(new ResponseMessageThread(message));
        }else if(ControlCode.CODE_A4.equals(controlCode)){
            // 校时报文应答
            String respCode = PacketUtil.getRespCode(message);
            startDateTimeTask(respCode,deviceCode);
        }
    }

    /**
     * 从站校时应答 00,01,10,11
     * @param respCode
     * @param deviceCode
     */
    private void startDateTimeTask(String respCode,String deviceCode){
        //
        switch (respCode){
            case RESP_CODE_00:
                subResponseExecutor.submit(new PduDateSettingTask(mqttMessageClient,deviceCode));
                break;
            case RESP_CODE_01:
            case RESP_CODE_10:
                subResponseExecutor.submit(new PduTimeSettingTask(mqttMessageClient,deviceCode));
                break;
            default:
                break;
        }
    }
}
