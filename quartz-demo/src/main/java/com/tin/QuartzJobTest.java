package com.tin;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzJobTest implements Job {

    private static final Logger logger = LoggerFactory.getLogger(QuartzJobTest.class);

    @Autowired
    private TestService testService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
            String payCompanyCode = (String) map.get("payCompanyCode");
            logger.info(" job map value: {}",payCompanyCode);
            //PayCompany payCompany = testService.getCompanyByCode(payCompanyCode);

        } catch (Exception e) {
            logger.error("QuartzJob execute error", e);
        }

    }
}
