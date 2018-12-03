package com.sensing.core.aop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.sensing.core.utils.Exception.InValidParamException;
import com.sensing.core.utils.results.ResultUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;//spring自带的日志框架
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.bean.SysUser;
import com.sensing.core.cacahes.CacheManagerImpl;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.WebUtil;

/**
 * 切面日志信息记录类
 * <p>Title: RequestInfoLog</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p>
 *
 * @author mingxingyu
 * @version 1.0
 * @date 2018年9月1日
 */
@Aspect
@Component
public class RequestInfoLog {

    //日志记录
    private final static Logger log = LoggerFactory.getLogger(RequestInfoLog.class);
    @Resource
    private IRpcLogService rcpLogService;


    @Around("execution(public * com.sensing.core.controller.*Controller.*(..))")//要处理的方法，包名+类名+方法名
    public Object cut(ProceedingJoinPoint joinPoint) {
        RpcLog rpc = new RpcLog();
        Object object = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        JSONObject json = getParam(request);

        try {
            String tag = json == null ? "" : json.getString("tag");
            String rec = (String) CacheManagerImpl.getCacheByKey(tag == null ? "" : tag);
            Date date = new Date();
            long start = System.currentTimeMillis();
            //权限验证,暂时没有做
            object = joinPoint.proceed();
            //后置拦截
            long end = System.currentTimeMillis();
            rpc.setMs((int) (start + end));
            //不再登录菜单中，不出异常不及日志
            if (request.getRequestURI().indexOf("login") != -1) {
                rpc.setType(1);
                rpc.setTodo("登录");
                SysUser model = JSONObject.toJavaObject((JSONObject) joinPoint.getArgs()[0], SysUser.class);
                rpc.setMemo(model.getUsername());
                rpc.setCreateUser(model.getUsername());
                rpc.setModule(model.getUsername());
            } else {
                if (rec == null || rec.equals("")) {
                    if (object instanceof ResponseBean) {
                        ResponseBean bean = (ResponseBean) object;
                        if (0 != bean.getError()) {
                            rpc.setCallTime(new Date());
                            rpc.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
                            rpc.setType(3);
                            rpc.setResult("异常");
                            rpc.setMemo("");
                            rpc.setTodo("异常");
                            rpc.setErrorMsg(bean.getMessage());
                            rcpLogService.saveNewRpcLog(rpc);
                        }
                    }
                    return object;
                } else {
                    rpc.setType(2);
                    String[] str = rec.split("-");
                    rpc.setCreateUser(request.getHeader("uuid"));
                    rpc.setModule(str[0]);
                    rpc.setTodo(str[1]);
                    rpc.setMemo(joinPoint.getArgs()[0].toString());
                }
            }
            if (object instanceof ResponseBean) {
                ResponseBean bean = (ResponseBean) object;
                if (0 != bean.getError()) {
                    rpc.setResult("失败");
                    rpc.setErrorMsg(bean.getMessage());
                } else {
                    rpc.setResult("成功");
                }
            }
            rpc.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
            rpc.setCallTime(date);
            rpc.setIp(WebUtil.getIpAddress(request));
            rpc.setRpcType("httpclient");
            rcpLogService.saveNewRpcLog(rpc);
            log.info("请求路径:{},请求用户:{},登录的ip:{},请求参数:{}", request.getRequestURI(), request.getHeader("uuid"), request.getRemoteAddr(), joinPoint.getArgs());
        } catch (Throwable e) {
            rpc.setCallTime(new Date());
            rpc.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
            rpc.setType(3);
            rpc.setResult("异常");
            rpc.setErrorMsg(StringUtils.getStackTrace(e).substring(0, 1000));
            rpc.setMemo("");
            rpc.setTodo("异常");
            rcpLogService.saveNewRpcLog(rpc);
            object = dealException(request, e);
        }
        return object;
    }

    public ResponseBean dealException(HttpServletRequest request, Throwable ex) {
        ResponseBean bean;
        if (ex instanceof InValidParamException) {
            bean = ResultUtils.REQUIRED_EMPTY_ERROR();
        } else {
            bean = ResultUtils.UNKONW_ERROR();
        }
        /****  从request中拿到入参和header  ****/
        String header = request.getHeader("uuid");
//        BufferedReader reader = null;
        JSONObject jsonObject = getParam(request);
        String json = jsonObject == null ? "" : jsonObject.toJSONString();
        log.error("\n==请求地址=========" + request.getRequestURL().toString() + "\n" + "==入参============" + json + "\n" + "==header里的uuid==" + header + "\n" + StringUtils.getStackTrace(ex));
        return bean;
    }

    private JSONObject getParam(HttpServletRequest request) {
        JSONObject jsonObject = null;
        BufferedReader streamReader = null;
        try {
            streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonObject;

    }
}
