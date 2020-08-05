package com.tin.it.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Constants {
    public static final String DLT_PASSWORD = "35434343";
    public static final String DLT_CONTROL = "44444444";

    public static String IAPmesg = "";

    // 存放bin文件的目录
    public static final String BIN_FILE_DIR = "D:/upload/";
    // 网关主题
    public static final String GATEWAY_TOPIC_NAME = "queue.chuangkou.gateway";
    // 设备更新主题
    public static final String UPLOAD_TOPIC_NAME = "queue.chuangkou.upload";

    // 更新数据包主题
    public static final String UPDATE_TOPIC_NAME = "queue.chuangkou.update";

    // 更新数据包返回主题
    //public static final String UPDATE_RESULT_TOPIC_NAME = "queue.chuangkou.updateResult/#";
    public static final String UPDATE_RESULT_TOPIC_NAME = "/chuangkou/updateResult/#";

    // 消息类型
    public static final String MSG_TYPE_CHECK = "check";
    public static final String MSG_TYPE_REQUEST = "request";
    public static final String MSG_TYPE_UPDATE = "update";

    //
    public static volatile boolean RETRY_STATE = false;

    public static final String TOPIC_PLATFORM_REQUEST = "chuangkou/platform/request/#";

    public static final String TOPIC_PLATFORM_RESPONSE = "chuangkou/platform/response/#";

    public static final String TOPIC_PLATFORM_DATA = "chuangkou/platform/data/#";

    public static final String TOPIC_GATEWAY_REQUEST = "chuangkou/gateway/request/#";

    public static final String TOPIC_GATEWAY_RESPONSE = "chuangkou/gateway/response/#";

    public static final String TOPIC_GATEWAY_DATA = "chuangkou/gateway/data/#";
}
