package com.tin;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component("quartzJobManager")
public class QuartzJobManager {
    private static final Logger logger = LoggerFactory.getLogger(QuartzJobManager.class);
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    //@Autowired
    //private QuartzJobDAO quartzJobDAO;

    /** 默认任务组名称 */
    private static final String DEFAULT_JOB_GROUP_NAME = "DefaultJobGroup";
    /** 默认触发器组名称 */
    private static final String DEFAULT_TRIGGER_GROUP_NAME = "DefaultTriggerGroup";

    /**
     * 加载数据库中已定义的定时任务
     *
     * @Title: loadJobs
     * @Description: 加载数据库中已定义的定时任务
     */
    public void loadJobs() {
        /*List<QuartzJobModel> jobs = quartzJobDAO.getAll();
        if (null != jobs && !jobs.isEmpty()) {
            for (QuartzJobModel job : jobs) {
                addJob(job);
            }
        }*/
    }

    /**
     * 重新加载数据库中已定义的定时任务
     *
     * @Title: reloadJobs
     * @Description: 重新加载数据库中已定义的定时任务
     */
    public void reloadJobs() {
        removeAll();
        loadJobs();
    }

    /**
     * 添加一个新的定时任务
     *
     * @Title: addJob
     * @Description: 添加一个新的定时任务
     * @param job QuartzJobModel
     */
    public void addJob(QuartzJobModel job) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            // 使用"jobName+默认任务组名称"作为定时任务的Key
            JobDetail jobDetail = /*new JobDetail(job.getJobName(), DEFAULT_JOB_GROUP_NAME,
                    Class.forName(job.getTargetClassName()));*/
            JobBuilder.newJob().build();
            // 使用"jobName+默认触发器组名称"作为定时任务触发器的Key
//            CronTrigger trigger = new CronTrigger(job.getJobName(), DEFAULT_TRIGGER_GROUP_NAME,
//                    job.getCronExpression());
//            scheduler.scheduleJob(jobDetail, trigger);
            logger.info("******注册定时任务：" + job + " ******");
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Exception e) {
            logger.error("注册定时任务失败：" + job + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 修改定时任务
     *
     * @Title: modifyJob
     * @Description: 修改定时任务
     * @param job QuartzJobModel
     */
    public void modifyJob(QuartzJobModel job) {
        removeJob(job);
        addJob(job);
    }

    /**
     * 删除定时任务
     *
     * @Title: removeJob
     * @Description: 删除定时任务
     * @param job QuartzJobModel
     */
    public void removeJob(QuartzJobModel job) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            // 根据""暂停触发器
//            scheduler.pauseTrigger(job.getJobName(), DEFAULT_TRIGGER_GROUP_NAME);
//            // 根据""移除触发器
//            scheduler.unscheduleJob(job.getJobName(), DEFAULT_TRIGGER_GROUP_NAME);
//            // 根据""删除定时任务
//            scheduler.deleteJob(job.getJobName(), DEFAULT_JOB_GROUP_NAME);
            logger.info("******删除定时任务：" + job + " ******");
        } catch (Exception e) {
            logger.error("移除定时任务失败：" + job + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 移除所有定时任务
     *
     * @Title: removeAll
     * @Description: 移除所有定时任务
     */
    public void removeAll() {
        /*List<QuartzJobModel> jobs = quartzJobDAO.getAll();
        if (null != jobs && !jobs.isEmpty()) {
            for (QuartzJobModel job : jobs) {
                removeJob(job);
            }
        }*/
    }

    /**
     * 暂停定时任务
     *
     * @Title: pauseJob
     * @Description: 暂停定时任务
     * @param job QuartzJobModel
     */
    public void pauseJob(QuartzJobModel job) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
//            scheduler.pauseJob(job.getJobName(), DEFAULT_JOB_GROUP_NAME);
            logger.info("******暂停定时任务：" + job + " ******");
        } catch (Exception e) {
            logger.error("暂停定时任务失败：" + job + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 恢复定时任务
     *
     * @Title: resumeJob
     * @Description: 恢复定时任务
     * @param job QuartzJobModel
     */
    public void resumeJob(QuartzJobModel job) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
//            scheduler.resumeJob(job.getJobName(), DEFAULT_JOB_GROUP_NAME);
            logger.info("******恢复定时任务：" + job + " ******");
        } catch (Exception e) {
            logger.error("恢复定时任务失败：" + job + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 立刻执行一次任务
     *
     * @Title: triggerJob
     * @Description: 立刻执行一次任务
     * @param job QuartzJobModel
     */
    public void triggerJob(QuartzJobModel job) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
//            scheduler.triggerJob(job.getJobName(), DEFAULT_JOB_GROUP_NAME);
            logger.info("******立刻执行定时任务：" + job + " ******");
        } catch (Exception e) {
            logger.error(" 立刻执行定时任务失败：" + job + e.getMessage());
            e.printStackTrace();
        }
    }

}
