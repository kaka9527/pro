package com.tin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
@RequestMapping(value = "/jobs")
public class JobController {
    private static Logger logger = LoggerFactory.getLogger(JobController.class);

//    @Autowired
//    private QuartzJobService quartzJobService;

    @Autowired
    private QuartzJobManager quartzJobManager;

    @RequestMapping(value = "/list")
    public String list() {
        return "list";
    }

    @RequestMapping(value = "/queryListByPage")
//    @RunningControllerLog(description = "分页查询")
    public void queryListByPage(HttpServletRequest request, HttpServletResponse response) {
//        Map<String, Object> params = HttpServletUtils
//                .parseReqToSearchCondition(new String[] { "jobStatus", "runOnHoliday", "jobName" }, request);
        // 权限条件追加
//        PermUtils.appendPermParams(params);

//        PageModel<QuartzJobModel> pageModel = new PageModel<>(request);
//        pageModel.setSearchCdtns(params);
        try {
//            pageModel = quartzJobService.findListByPage(pageModel);
//            returnJSONData(response, pageModel.toJSONString());
        } catch (Exception e) {
            logger.error("分页查询异常，异常信息:" + e);
        }
    }

    @RequestMapping(value = "/toAdd")
    public String toAdd() {
        return "add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void add(HttpServletRequest request, HttpServletResponse response) {
        String jobName = request.getParameter("jobName");
        String targetClassName = request.getParameter("targetClassName");
        String cronExpression = request.getParameter("cronExpression");
        String runOnHoliday = request.getParameter("runOnHoliday");
        String jobDesc = request.getParameter("jobDesc");

//        Date date = new Date();
//        SysUser user = getSysUser(request);
//        String userName = user.getUserName();
//
//        QuartzJobModel job = new QuartzJobModel();
//        job.setJobName(jobName);
//        job.setTargetClassName(targetClassName);
//        job.setCronExpression(cronExpression);
//        if (!StringUtil.isEmpty(jobDesc)) {
//            job.setJobDesc(jobDesc);
//        } else {
//            job.setJobDesc("");
//        }
//        job.setRunOnHoliday(runOnHoliday);
//        job.setJobStatus(JobStatusEnum.RUNNING.getValue());
//        job.setCreaterName(userName);
//        job.setCreateTime(date);
//        job.setUpdaterName(userName);
//        job.setUpdateTime(date);

//        quartzJobManager.addJob(job);
//        logger.info("^^^^^^^(add " + job + " by " + getSysUser(request).getUserName() + ")^^^^^^");
//        int result = quartzJobService.save(job);
//        returnJSONData(response, JSON.toJSONString(result));
    }

    @RequestMapping(value = "/toEdit/{jobId}")
    public String toEdit(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("jobId") String jobId) {
//        QuartzJobModel job = quartzJobService.getById(jobId);
//        request.setAttribute("item", job);
        return "edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public void edit(HttpServletRequest request, HttpServletResponse response) {
        String jobId = request.getParameter("jobId");
        String cronExpression = request.getParameter("cronExpression");
        String runOnHoliday = request.getParameter("runOnHoliday");
        String jobDesc = request.getParameter("jobDesc");

        Date date = new Date();
//        SysUser user = getSysUser(request);
//        String userName = user.getUserName();
//
//        QuartzJobModel job = quartzJobService.getById(jobId);
//
//        job.setCronExpression(cronExpression);
//        if (!StringUtil.isEmpty(jobDesc)) {
//            job.setJobDesc(jobDesc);
//        } else {
//            job.setJobDesc("");
//        }
//        job.setRunOnHoliday(runOnHoliday);
//        job.setUpdaterName(userName);
//        job.setUpdateTime(date);
//        int result = quartzJobService.update(job);
//        job = quartzJobService.getById(jobId);
//        quartzJobManager.modifyJob(job);
//        logger.info("^^^^^^^(edit " + job + " by " + getSysUser(request).getUserName() + ")^^^^^^");

        //returnJSONData(response, JSON.toJSONString(result));
    }

    @RequestMapping(value = "/pause/{jobId}",
            method = RequestMethod.POST)
    public void pause(HttpServletRequest request, HttpServletResponse response, @PathVariable("jobId") String jobId) {
//        Date date = new Date();
//        SysUser user = getSysUser(request);
//        String userName = user.getUserName();
//
//        QuartzJobModel job = quartzJobService.getById(jobId);
//
//        job.setJobStatus(JobStatusEnum.PAUSED.getValue());
//        job.setUpdaterName(userName);
//        job.setUpdateTime(date);
//
//        quartzJobManager.pauseJob(job);
//        logger.info("^^^^^^^(pause " + job + " by " + getSysUser(request).getUserName() + ")^^^^^^");
//        int result = quartzJobService.update(job);
//        returnJSONData(response, JSON.toJSONString(result));
    }

    @RequestMapping(value = "/resume/{jobId}",
            method = RequestMethod.POST)
    public void resume(HttpServletRequest request, HttpServletResponse response, @PathVariable("jobId") String jobId) {
//        Date date = new Date();
//        SysUser user = getSysUser(request);
//        String userName = user.getUserName();
//
//        QuartzJobModel job = quartzJobService.getById(jobId);
//        if (job.getJobStatus().equals(JobStatusEnum.PAUSED.getValue())) {
//            quartzJobManager.resumeJob(job);
//        } else if (job.getJobStatus().equals(JobStatusEnum.STOPPED.getValue())) {
//            quartzJobManager.addJob(job);
//        }
//        logger.info("^^^^^^^(sesume " + job + " by " + getSysUser(request).getUserName() + ")^^^^^^");
//        job.setJobStatus(JobStatusEnum.RUNNING.getValue());
//        job.setUpdaterName(userName);
//        job.setUpdateTime(date);
//        int result = quartzJobService.update(job);
//        returnJSONData(response, JSON.toJSONString(result));
    }

    @RequestMapping(value = "/run/{jobId}",
            method = RequestMethod.POST)
    public void runJob(HttpServletRequest request, HttpServletResponse response, @PathVariable("jobId") String jobId) {
//        QuartzJobModel job = quartzJobService.getById(jobId);
//        logger.info("^^^^^^^(run " + job + " by " + getSysUser(request).getUserName() + ")^^^^^^");
//        quartzJobManager.triggerJob(job);
    }

    @RequestMapping(value = "/reloadJobs",
            method = RequestMethod.POST)
    public void reloadJobs(HttpServletRequest request, HttpServletResponse response) {
//        logger.info("^^^^^^^(reload jobs by " + getSysUser(request).getUserName() + ")^^^^^^");
        quartzJobManager.reloadJobs();
    }

    @RequestMapping(value = "/remove/{jobId}",
            method = RequestMethod.POST)
    public void remove(HttpServletRequest request, HttpServletResponse response, @PathVariable("jobId") String jobId) {
//        QuartzJobModel job = quartzJobService.getById(jobId);
//        logger.info("^^^^^^^(remove job " + job + " by " + getSysUser(request).getUserName() + ")^^^^^^");
//        quartzJobManager.removeJob(job);
//        job.setJobStatus(JobStatusEnum.STOPPED.getValue());
//        int result = quartzJobService.update(job);
//        returnJSONData(response, JSON.toJSONString(result));
    }

    @RequestMapping(value = "/getJobStatus",
            method = RequestMethod.POST)
    public void getJobStatus(HttpServletRequest request, HttpServletResponse response) {
//        StringBuilder options = new StringBuilder();
//        List<JobStatusEnum> list = JobStatusEnum.getAll();
//        options.append("<option value=''>--请选择--</option>");
//        for (JobStatusEnum item : list) {
//            options.append("<option value='" + item.getValue() + "'>" + item.getName() + "</option>");
//        }
//        String json = "{\"html\":\"" + options.toString() + "\"}";
//        returnJSONData(response, json);
    }

}
