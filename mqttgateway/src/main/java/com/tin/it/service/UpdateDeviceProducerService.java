package com.tin.it.service;

import com.tin.it.vo.MessageInfoVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 更新设备软件发送接口
 */
@RestController
@RequestMapping("/msg")
public interface UpdateDeviceProducerService {

    /**
     * 更新设备接口
     * @param msg
     */
    @PostMapping("/update")
    public void startUpdate(MessageInfoVO msg);

    /**
     * 预留上传文件接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file);
}
