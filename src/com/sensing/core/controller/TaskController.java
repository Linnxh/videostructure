package com.sensing.core.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.SysUser;
import com.sensing.core.bean.Task;
import com.sensing.core.bean.TaskChannel;
import com.sensing.core.bean.TaskRequest;
import com.sensing.core.bean.TaskResp;
import com.sensing.core.bean.req.RunningTaskCountReq;
import com.sensing.core.bean.req.StopTaskReq;
import com.sensing.core.service.ITaskService;
import com.sensing.core.service.impl.CaptureThriftServiceImpl;
import com.sensing.core.thrift.cap.bean.CapReturn;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.results.ResultUtils;

@Controller
@RequestMapping("/task")
public class TaskController extends BaseController {

    private static final Log log = LogFactory.getLog(TaskController.class);

    @Resource
    public ITaskService taskService;

    @Resource
    public CaptureThriftServiceImpl captureThriftServiceImpl;

    @ResponseBody
    @RequestMapping("/query")
    public ResponseBean query(HttpServletRequest request, @RequestBody JSONObject p) {

        ResponseBean result = new ResponseBean();
        String uuid = request.getHeader("uuid");
        Pager pager = JSONObject.toJavaObject(p, Pager.class);
        pager.getF().put("createUser", uuid);
        /*****  通过名字查询的接口速度较慢，如果没有 *****/
        if (!TextUtils.isEmpty(pager.getF().get("name"))) {
            pager = taskService.queryListByName(pager);
        } else {
            pager = taskService.query(pager);
        }
        result.getMap().put("pager", pager);
        result.setError(0);
        result.setMessage("successful");
        return result;
    }

    @ResponseBody
    @RequestMapping("/openchannelbychanneluuid")
    public ResponseBean openChannelByChannelUuid(@RequestBody JSONObject p) {
        String channelUuid = p.getString("channelUuid");
        if (StringUtils.isEmptyOrNull(channelUuid)) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        CapReturn capReturn = null;
        try {
            capReturn = captureThriftServiceImpl.OpenCloseChannels(Arrays.asList(channelUuid), 1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (capReturn != null) {
            log.info("根据channeluuid打开对应的通道===res-" + capReturn.res + "--msg--" + capReturn.msg);
        } else {
            log.info("Exception");
        }
        return ResultUtils.success(null);
    }

    @ResponseBody
    @RequestMapping("/querylistbyname")
    public ResponseBean queryListByName(HttpServletRequest request, @RequestBody JSONObject p) {
        String uuid = request.getHeader("uuid");
        SysUser su = (SysUser) request.getSession().getAttribute(uuid);
        Pager pager = JSONObject.toJavaObject(p, Pager.class);
        ResponseBean result = new ResponseBean();
        try {
            if (TextUtils.isEmpty(pager.getF().get("name"))) {
                result.setError(101);
                result.setMessage("fail");
                return result;
            }
            pager.getF().put("createUser", su.getUuid());
            pager = taskService.queryListByName(pager);
            result.getMap().put("pager", pager);
            result.setError(0);
            result.setMessage("successful");
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            result.setError(100);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/update")
    public ResponseBean update(@RequestBody JSONObject m) {
        TaskRequest model = JSONObject.toJavaObject(m, TaskRequest.class);
        ResponseBean result = new ResponseBean();
        if (model == null || model.getTask() == null || TextUtils.isEmpty(model.getTask().getUuid())) {
            result.setError(100);
            result.setMessage("business error");
            return result;
        }
        TaskRequest request = taskService.updateTask(model);
        if (request != null) {
            result.setError(0);
            result.setMessage("successful");
        } else {
            result.setError(100);
            result.setMessage("business error");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping("/updatestate")
    public ResponseBean updateState(@RequestBody JSONObject m) {
        Task model = JSONObject.toJavaObject(m, Task.class);
        ResponseBean result = new ResponseBean();
        if (model != null && model.getUuid() != null && !model.getUuid().equals("")) {
            model.setModifyTime(new Date());
            model = taskService.updateState(model);
            result.getMap().put("model", model);
            result.setError(0);
            result.setMessage("successful");
        } else {
            result.setError(100);
            result.setMessage("business error");
        }
        return result;
    }

    /**
     * save data
     */
    @ResponseBody
    @RequestMapping("/save")
    public ResponseBean save(HttpServletRequest request, @RequestBody JSONObject m) {

        TaskRequest model = JSONObject.toJavaObject(m, TaskRequest.class);
        if (!avaliableParams(model, 0)) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        //入参构建
        model.getTask().setCreateUser(getUser().getUuid());
        model.getTaskChannel().setCreateUser(getUser().getUuid());
        model.getTask().setState(Constants.TASK_STAT_WAITSTART);
        //重名
        List<Task> taskByName = taskService.getTaskByName(model.getTask().getName());
        if (!CollectionUtils.isEmpty(taskByName)) {
            return ResultUtils.error(-1, "已存在名称为\"" + model.getTask().getName() + "\"的任务");
        }
        //保存
        TaskRequest taskRequest = taskService.saveNewTask(model);
        return taskRequest == null ? ResultUtils.UNKONW_ERROR() : ResultUtils.success(null);
    }

    /**
     * @param model
     * @param type  1:修改   2：新增
     * @return
     */
    public boolean avaliableParams(TaskRequest model, int type) {

        if (model == null || model.getTask() == null || model.getTaskChannel() == null) {
            return false;
        }

        Task task = model.getTask();
        if (TextUtils.isEmpty(task.getName()) || task.getType() == null || TextUtils.isEmpty(task.getAnalyType()) || TextUtils.isEmpty(task.getRunWeek())) {
            return false;
        }

        TaskChannel taskC = model.getTaskChannel();
        if (TextUtils.isEmpty(taskC.getChannelUuid())) {
            return false;
        }

        return true;
    }

    /**
     * 任务查看详情
     *
     * @param m
     * @return
     */
    @ResponseBody
    @RequestMapping("/info")
    public ResponseBean info(@RequestBody JSONObject m) {

        Task model = JSONObject.toJavaObject(m, Task.class);
        ResponseBean result = new ResponseBean();
        if (TextUtils.isEmpty(model.getUuid())) {
            result.setError(2);
            result.setMessage("参数不全");
            return result;
        }
        TaskResp info = taskService.info(model.getUuid());
        if (info == null) {
            result.setError(-1);
            result.setMessage("fail");
        }
        result.getMap().put("model", info);
        result.setError(0);
        result.setMessage("successful");
        return result;
    }

    /**
     * 获取正在运行的任务个数
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/getrunningtaskCount")
    public ResponseBean getRunningTask(@RequestBody RunningTaskCountReq req) {
        if (req == null) {
            req = new RunningTaskCountReq();
        }
        req.setUserUuid(getUser().getUuid());
        int count = taskService.getrunningtaskCount(req);
        return ResultUtils.success("runnningCount", count);
    }

    /**
     * 停止任务
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/stoptaskbyuuid")
    public ResponseBean stoptaskByUuId(@RequestBody StopTaskReq req) {
        if (req == null || StringUtils.isEmptyOrNull(req.getTaskUuid()) || req.getState() == null) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        req.setUserUuid(getUser().getUuid());
        return taskService.stoptaskByUuId(req);
    }
    /**
     * 开启任务
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/starttaskbyuuid")
    public ResponseBean startTaskByUuId(@RequestBody StopTaskReq req) {
        if (req == null || StringUtils.isEmptyOrNull(req.getTaskUuid()) || req.getState() == null) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        req.setUserUuid(getUser().getUuid());
        return taskService.startTaskByUuId(req);
    }

    /**
     * 根据id查询任务执行时间（任务的查看结果，从mongo里取数据）
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/getanalytimebyuuid")
    public Map getAnalyTimeByUuid(@RequestBody StopTaskReq  req) {
        return taskService.getAnalyTimeByUuid(req.getTaskUuid());
    }


}
