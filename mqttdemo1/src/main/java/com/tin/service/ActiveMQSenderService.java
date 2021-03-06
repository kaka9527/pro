package com.tin.service;

import com.tin.vo.MessageInfoVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public interface ActiveMQSenderService {

    @PostMapping("/send")
    public String sendMessage(MessageInfoVO msg);

    @PostMapping("/upload")
    public void startUpdate(MessageInfoVO msg);
}
