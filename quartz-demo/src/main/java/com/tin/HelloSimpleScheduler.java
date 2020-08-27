package com.tin;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;

public class HelloSimpleScheduler {
    public static void main(String[] args) {
        try {
            // 1. 创建一个JodDetail实例 将该实例与Hello job class绑定 (链式写法)
            JobDetail jobDetail = newJob(HelloJob.class) // 定义Job类为HelloQuartz类，这是真正的执行逻辑所在
                    .withIdentity("myJob") // 定义name/group
                    .build();
            // 打印当前的时间
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date();
            System.out.println("current time is :" + sf.format(date));

            // 2. 定义一个Trigger，定义该job在4秒后执行，并且执行一次
            Date startTime = new Date();
            startTime.setTime(startTime.getTime()+4000L);
            SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                    .withIdentity("myTrigger", "group1")// 定义名字和组
                    .startAt(startTime)
                    .withSchedule(    //定义任务调度的时间间隔和次数
                            SimpleScheduleBuilder
                                    .simpleSchedule()
                                    .withIntervalInSeconds(3)//定义时间间隔是3秒
//                                    .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY)//定义重复执行次数是无限次
                                    .withRepeatCount(3)//定义重复执行次数是无限次
                    )
                    .build();

            // 3. 创建scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // 4. 将trigger和jobdetail加入这个调度
            scheduler.scheduleJob(jobDetail, trigger);

            // 5. 启动scheduler
            scheduler.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
