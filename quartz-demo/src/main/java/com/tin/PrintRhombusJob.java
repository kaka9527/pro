package com.tin;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

public class PrintRhombusJob extends MethodInvokingJobDetailFactoryBean.StatefulMethodInvokingJob {
    private static final Logger logger = LoggerFactory.getLogger(PrintRhombusJob.class);

    /**
     * @see org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean.MethodInvokingJob#executeInternal(org.quartz.JobExecutionContext)
     */

    @Override
    protected void executeInternal(JobExecutionContext cts) throws JobExecutionException {
        logger.info("================== print rhombus job begin =================");
        System.out.println("print rhombus");
        logger.info("================== print rhombus job end =================");
    }

}
