package com.tin;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class InitSystemJobsListener implements ApplicationListener<ContextRefreshedEvent> {

    private int runTime = 0;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        runTime++;
        if (2 == runTime) {
            // 获取spring管理的Bean
            ApplicationContext context = contextRefreshedEvent.getApplicationContext();
            //QuartzManager quartzManager = (QuartzManager) context.getBean("quartzJobManager");
            //quartzManager.loadJobs();
        }

    }
}
