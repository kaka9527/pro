package com.tin;

import org.quartz.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //打印当前的时间
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("now: "+LocalDateTime.now());
        // 1.获取Trigger
        Trigger trigger = context.getTrigger();
        //2.通过trigger获取job标识
        JobKey jobKey = trigger.getJobKey();
        System.out.println("Job's key:"+"name:"+jobKey.getName()+"\tgroup:"+jobKey.getGroup());getClass();
        System.out.println("Start time : "+sf.format(trigger.getStartTime()));
        System.out.println("End time : "+sf.format(trigger.getEndTime()));
    }
}
