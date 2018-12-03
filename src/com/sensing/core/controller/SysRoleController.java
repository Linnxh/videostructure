package com.sensing.core.controller;

import javax.annotation.Resource;

import com.sensing.core.bean.*;
import com.sensing.core.service.ISysResourceService;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.results.ResultUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sensing.core.service.ISysRoleService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统角色管理
 */
@Controller
@RequestMapping("/sysRole")
@SuppressWarnings("all")
public class SysRoleController extends BaseController {

    private static final Log log = LogFactory.getLog(SysRoleController.class);

    @Resource
    public ISysRoleService sysRoleService;

    /**
     * 查询角色列表
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/query")
    public ResponseBean query(@RequestBody SysRoleReq req) {
        req.setUuid(getUser().getUuid());
        Pager pager = sysRoleService.queryPage(req);
        ResponseBean result;
        if (pager == null) {
            result = new ResponseBean(100, "fail", null);
        } else {
            Map map = new HashMap();
            map.put("pager", pager);
            result = new ResponseBean(0, "success", map);
        }
        return result;
    }

    /**
     * 更新操作
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/update")
    public ResponseBean update(@RequestBody SysRoleSaveReq req) {
        ResponseBean result = new ResponseBean();
        if (req == null && req.getRoleId() == null) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        int code = sysRoleService.updateSysRole(req);
        return code > 0 ? ResultUtils.success(null) : ResultUtils.UNKONW_ERROR();
    }

    /**
     * 编辑的时候获取拥有的资源
     *
     * @param req
     * @return
     */
    @ResponseBody
    @RequestMapping("/getsysresoonedit")
    public ResponseBean getSysResOnEdit(@RequestBody SysResoEdidReq req) {
        if (req == null || req.getRoleId() == null) {
            return ResultUtils.REQUIRED_EMPTY_ERROR();
        }
        req.setUserUuid(getUser().getUuid());
        List<SysResource> resp = sysRoleService.getSysResOnEdit(req);
        Map map = new HashMap();
        map.put("resultList", resp);
        ResponseBean success = ResultUtils.success(map);
        return success;
    }

    /**
     * 角色新建保存
     */
    @ResponseBody
    @RequestMapping("/save")
    public ResponseBean save(@RequestBody SysRoleSaveReq req) {
        if (req == null || StringUtils.isEmptyOrNull(req.getRoleName()) || CollectionUtils.isEmpty(req.getSysResourceIds())) {
            return new ResponseBean(101, "参数不全", null);
        }
        ResponseBean result = new ResponseBean();
        req.setUserUUid(getUser().getUuid());
        int code = sysRoleService.saveNewSysRole(req);
        result.setError(code > 0 ? 0 : -1);
        result.setMessage(code > 0 ? "successful" : "fail");
        return result;
    }

}
