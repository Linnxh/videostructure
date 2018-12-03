package com.sensing.core.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sensing.core.utils.results.ResultUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.SysUser;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.service.ISysUserService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.MD5Utils;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.WebUtil;


/**
 * com.sensing.core.controller
 * 用户登录Controller类
 *
 * @author haowenfeng
 * @date 2018年4月23日 下午4:25:10
 */
@Controller
@RequestMapping("/sysUser")
@SuppressWarnings("all")
public class SysUserController extends BaseController {

    private static final Log log = LogFactory.getLog(SysUserController.class);
    @Resource
    public ISysUserService sysUserService;
    @Resource
    public IRpcLogService rpcLogService;

    /**
     * 用户登录信息认证
     *
     * @param p
     * @return
     */
    @ResponseBody
    @RequestMapping("/login")
    public ResponseBean login(@RequestBody JSONObject p, HttpServletRequest request) {
        SysUser model = JSONObject.toJavaObject(p, SysUser.class);
        ResponseBean result = new ResponseBean();
        String ip = WebUtil.getIpAddress(request);//返回发出请求的IP地址
        model.setLoginIp(ip);
        //用户名或密码非空判断
        if (model.getUsername() == null || model.getPassword() == null || "".equals(model.getUsername()) || "".equals(model.getPassword())) {

            return setErrMsg(result, 101, "用户名密码不能为空！");
        }
        //如果参数有ip，则判断ip合法性
        if (StringUtils.isEmptyOrNull(model.getLoginIp())) {

            return setErrMsg(result, 101, "确认IP地址是否正确！");
        }
        //密码转换
        model.setPassword(MD5Utils.MD5(model.getPassword()));//密码加密S
        SysUser user = sysUserService.login(model);
        if (user == null) {
            return setErrMsg(result, 100, "用户名密码错误");
        }
        if ((user.getIsAdmin() == null || user.getIsAdmin() == 0) && (user.getLoginIp() == null || model.getLoginIp() == null || !model.getLoginIp().equals(user.getLoginIp()))) {
            return setErrMsg(result, 100, "确认已开通访问权限");
        }
        //判断用户是否为启用状态
        if (user.getState() != Constants.USER_STATE_USE) {
            return setErrMsg(result, 100, "账号已被停用，请联系管理员");
        }
        try {
            //request.getSession().setAttribute(user.getUuid(), user);
        } catch (Exception e) {
            log.error("创建session异常", e);
            return setErrMsg(result, -1, "生成session错误");
        }
        result.setError(0);
        result.getMap().put("user", user);
        result.getMap().put("uuid", user.getUuid());
        result.setMessage("successful");
        return result;
    }

    //返回错误信息
    private ResponseBean setErrMsg(ResponseBean r, Integer code, String msg) {
        r.setError(code);
        r.setMessage(msg);
        log.error(msg);
        return r;
    }

    /**
     * @Description: 用户列表查询
     * @author dongsl
     * @Date 2018年6月13日下午1:08:03
     */
    @ResponseBody
    @RequestMapping("/query")
    public ResponseBean query(@RequestBody JSONObject p, HttpServletRequest request, HttpServletResponse response) {
        log.info("调用sysUser/query接口参数" + p);
        Pager pager = JSONObject.toJavaObject(p, Pager.class);
        ResponseBean result = new ResponseBean();
        try {

            pager.getF().put("uuId", getUser().getUuid());
            pager = sysUserService.queryPage(pager);    //查询用户列表
            result.getMap().put("pager", pager);
            result.setError(0);
            result.setMessage("success");
        } catch (Exception e) {
            log.error(e + StringUtils.getStackTrace(e));
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 用户详情查询
     * author dongsl
     * date 2017年8月10日上午9:23:58
     */
    @ResponseBody
    @RequestMapping("/getUserById")
    public ResponseBean getUserById(@RequestBody JSONObject p) {
        String uuidd = p.getString("uuid");
        ResponseBean result = new ResponseBean();
        try {
            if (StringUtils.isNotEmpty(uuidd)) {
                SysUser model = sysUserService.findSysUserById(uuidd);
                result.getMap().put("model", model);
                result.setError(0);
                result.setMessage("success");
            } else {
                result.setError(100);
                result.setMessage("用户uuid不能为空！");
            }
        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 用户修改
     * author dongsl
     * date 2017年8月10日上午9:31:32
     */
    @ResponseBody
    @RequestMapping("/update")
    public ResponseBean update(@RequestBody JSONObject m, HttpServletRequest request) {
        SysUser model = JSONObject.toJavaObject(m, SysUser.class);
        ResponseBean result = new ResponseBean();
        try {
            if (model != null && model.getUuid() != null && !"".equals(model.getUuid())) {    //用户uuid不为空，执行更新
                String vr = validateUser(model, 2);
                if ("".equals(vr)) {
                    SysUser su = sysUserService.findSysUserById(model.getUuid());
                    if (su == null) {
                        log.info("查询数据库中用户信息为空！uuid---" + model.getUuid());
                        result.setError(100);
                        result.setMessage("用户uuid不能为空！");
                    } else if (!su.getPassword().equals(model.getPassword())) {
                        //如果密码不为空，将其转为MD5
                        model.setPassword(MD5Utils.MD5(model.getPassword()));
                    }
                    model = sysUserService.updateSysUser(model);
                    result.getMap().put("model", model);
                    result.setError(0);
                    result.setMessage("success");
                } else {
                    result.setError(100);
                    result.setMessage(vr);
                }
            } else {
                result.setError(100);
                result.setMessage("用户uuid不能为空");
            }
        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * @Description: 保存用户
     * @author dongsl
     * @Date 2018年6月13日下午1:13:44
     */
    @ResponseBody
    @RequestMapping("/save")
    public ResponseBean save(@RequestBody JSONObject m, HttpServletRequest request) {
        SysUser model = JSONObject.toJavaObject(m, SysUser.class);
        String uuid = request.getHeader("uuid");
        ResponseBean result = new ResponseBean();
        try {
            //较验输入参数合法性
            String vr = validateUser(model, 1);
            if (vr == "") {
                model.setPassword(MD5Utils.MD5(model.getPassword()));
                model.setAddUid(uuid);
                model.setIsAdmin(0);
                model = sysUserService.saveNewSysUser(model);
                result.getMap().put("model", model);
                result.setError(0);
                result.setMessage("success");
            } else {
                result.setError(100);
                result.setMessage(vr);
            }

        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/updatePsw")
    public ResponseBean updatePsw(@RequestBody JSONObject p, HttpServletRequest request) {
        ResponseBean result = new ResponseBean();
        String oldPsw = MD5Utils.MD5(p.getString("oldPsw"));
        String newPsw = MD5Utils.MD5(p.getString("newPsw"));
        String uuid = p.getString("uuid");
        try {
            SysUser su = sysUserService.findSysUserById(uuid);
            if (su == null) {
                log.info("查询数据库中用户信息为空！");
                result.setError(100);
                result.setMessage("查询数据库中用户信息为空！");
            } else if (!su.getPassword().equals(oldPsw)) {
                log.info("旧密码输入错误！");
                result.setError(100);
                result.setMessage("旧密码输入错误！");
            } else {
                su.setPassword(newPsw);
                sysUserService.updateSysUser(su);
                result.setError(0);
                result.setMessage("success");
            }
        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/updatePswByAdmin")
    public ResponseBean updatePswByAdmin(@RequestBody JSONObject p, HttpServletRequest request) {
        ResponseBean result = new ResponseBean();
        String newPsw = MD5Utils.MD5(p.getString("newPsw"));
        String uuid = p.getString("uuid");
        try {
            SysUser su = sysUserService.findSysUserById(uuid);
            if (su == null) {
                log.info("查询数据库中用户信息为空！");
                result.setError(100);
                result.setMessage("查询数据库中用户信息为空！");
            } else {
                su.setPassword(newPsw);
                sysUserService.updateSysUser(su);
                result.setError(0);
                result.setMessage("success");
            }
        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * @param idarr
     * @return ResponseBean
     * @Description: 用户删除， 逻辑删除
     * @author dongsl
     * @Date 2017年9月18日下午4:01:12
     */
    @ResponseBody
    @RequestMapping("/delete")
    public ResponseBean delete(@RequestBody JSONObject m, HttpServletRequest request, HttpServletResponse response) {
        Long d1 = new Date().getTime();
        ResponseBean result = new ResponseBean();
        String id = m.getString("id");
        if (StringUtils.isEmptyOrNull(id)) {
            result.setError(100);
            result.setMessage("业务逻辑错误，请提交要删除数据ID");
            return result;
        }
        try {
            SysUser su = sysUserService.findSysUserById(id);
            if (su == null) {
                log.info("查询数据库中用户信息为空！");
                result.setError(100);
                result.setMessage("查询数据库中用户信息为空！");
            } else {
                su.setIsDeleted(Constants.DELETE_YES);
                sysUserService.updateSysUser(su);
                result.setError(0);
                result.setMessage("success");
            }
        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    /**
     * 用户启用
     * author dongsl
     * date 2017年8月11日下午2:01:08
     */
    @ResponseBody
    @RequestMapping("/openUser")
    public ResponseBean openUser(@RequestBody JSONObject m) {
        ResponseBean result = new ResponseBean();
        String id = m.getString("id");
        if (StringUtils.isEmptyOrNull(id)) {
            result.setError(100);
            result.setMessage("业务逻辑错误，请提交要删除数据ID");
            return result;
        }
        try {
            SysUser user = sysUserService.findSysUserById(id);    //查询用户详情
            if (user == null) {
                result.setError(100);
                result.setMessage("不存在该用户！");
            } else {
                //将用户状态置为启用
                user.setState(Constants.USER_STATE_USE);
                //更新用户
                sysUserService.updateSysUser(user);
                result.setError(0);
                result.setMessage("success");
            }
        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 用户禁用
     * author dongsl
     * date 2017年8月11日下午2:01:08
     */
    @ResponseBody
    @RequestMapping("/closeUser")
    public ResponseBean closeUser(@RequestBody JSONObject m) {
        ResponseBean result = new ResponseBean();
        String id = m.getString("id");
        if (StringUtils.isEmptyOrNull(id)) {
            result.setError(100);
            result.setMessage("业务逻辑错误，请提交要删除数据ID");
            return result;
        }
        try {
            int sucCount = 0;
            SysUser user = sysUserService.findSysUserById(id);    //查询用户详情
            if (user == null) {
                log.error("不存在uuid为：" + id + "的用户");
            } else {
                //将用户状态置为启用
                user.setState(Constants.USER_STATE_UNUSE);
                //更新用户
                sysUserService.updateSysUser(user);
                result.setError(0);
                result.setMessage("success");
            }
        } catch (Exception e) {
            log.error(e);
            result.setError(-1);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 用户退出
     * author dongsl
     * date 2017年8月11日下午2:01:08
     */
    @ResponseBody
    @RequestMapping("/logout")
    public ResponseBean logout(HttpServletRequest request) {
        ResponseBean result = new ResponseBean();
//		String uuid=request.getHeader("uuid");
//		request.getSession().removeAttribute(uuid);
        result.setError(0);
        result.setMessage("success");
        return result;
    }


    /**
     * 验证保存用户信息是否完善
     * author dongsl
     * date 2017年8月10日上午9:45:33
     */
    private String validateUser(SysUser user, Integer type) {
        try {
            if (user.getRoleId() == null || user.getRoleId().length == 0) {
                return "用户名不能为空！";
            }
            if (!StringUtils.isNotEmpty(user.getUsername())) {
                return "用户名不能为空！";
            }
            if (!StringUtils.isNotEmpty(user.getPassword()) && type == 1) {
                return "密码不能为空！";
            }
            if (user.getUsername().length() > 16) {
                return "用户名长度在0－16位之间！";
            }
            if (type == 1 && (user.getPassword().length() < 4 || user.getPassword().length() > 32)) {
                return "密码名长度在4－32位之间！";
            }
            if (null != user.getDescription() && (user.getDescription().length() > 512)) {
                return "描述长度在0－512位之间！";
            }
            if (null != user.getMobile() && (user.getMobile().length() > 11)) {
                return "联系方式长度在0－11位之间！";
            }
            List<SysUser> list = sysUserService.queryUserByUserName(user.getUuid(), user.getUsername());
            if (type == 1) {
                if (list != null && list.size() > 0) {
                    return "用户名已存在！";
                }
            }
            if (type == 2) {
                if (list != null && list.size() > 0) {
//					for (int i = 0; i < list.size() ; i++) {
//						if ( user.getUuid() != null && !list.get(i).getUuid().equals(user.getUuid())  ) {
                    return "用户名已存在！";
//						}
//					}
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";

    }

    /**
     * 查询有审批权限的用户
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/getuserhaveratify")
    public ResponseBean getUserHaveRatify(HttpServletRequest request) {
        return sysUserService.getUserHaveRatify();
    }


    /**
     * 设置session超时时间
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/setsessiontimeout")
    public ResponseBean setSessionTimeOut(@RequestBody JSONObject m) {
        Integer sessionTimeOut = m.getInteger("sessionTimeOut");
        if (sessionTimeOut != null) {
            return sysUserService.setSessionTimeOut(sessionTimeOut * 60);
        } else {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
    }


}
