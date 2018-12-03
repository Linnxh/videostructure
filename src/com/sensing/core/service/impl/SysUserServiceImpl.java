package com.sensing.core.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.base.Joiner;
import com.sensing.core.bean.*;
import com.sensing.core.cacahes.OnlineUserListener;
import com.sensing.core.dao.*;
import com.sensing.core.utils.*;
import com.sensing.core.utils.results.ResultUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sensing.core.service.ISysUserService;
import com.sensing.core.utils.props.PropUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author wenbo
 */
@Service
public class SysUserServiceImpl implements ISysUserService {


    private static final Log log = LogFactory.getLog(ISysUserService.class);

    @Resource
    public ISysUserDAO sysUserDAO;

    @Resource
    public ISysRoleDAO sysRoleDAO;

    @Resource
    public ISysResourceDAO sysResourceDAO;

    @Resource
    public OnlineUserListener onlineUserListener;

    /**
     * @Description: 保存用户
     * @author dongsl
     * @Date 2018年6月13日下午1:47:41
     */
    @Override
    public SysUser saveNewSysUser(SysUser sysUser) throws Exception {
        try {
            //如果用户真实姓名为空，则真实姓名和登录名相同
            if (sysUser.getRealname() == null || "".equals(sysUser.getRealname())) {
                sysUser.setRealname(sysUser.getUsername());
            }
            String id = UuidUtil.getUuid();
            sysUser.setUuid(id);
            sysUser.setAddTime(new Date());    //创建时间
            sysUser.setLoginCount(0);    //登录次数默认为0
            sysUser.setIsDeleted(Constants.DELETE_NO);
            sysUserDAO.saveSysUser(sysUser);
            sysUserDAO.saveUserRoleo(sysUser.getRoleId(), id);
        } catch (Exception e) {
            log.error("保存用户失败！" + ExpUtil.getExceptionDetail(e));
            throw new BussinessException(e);
        }
        return sysUser;
    }

    /**
     * @Description: 更新用户
     * @author dongsl
     * @Date 2018年6月13日下午1:49:19
     */
    @Override
    public SysUser updateSysUser(SysUser sysUser) {
        try {
            //如果用户真实姓名为空，则真实姓名和登录名相同
            if (sysUser.getRealname() == null || "".equals(sysUser.getRealname())) {
                sysUser.setRealname(sysUser.getUsername());
            }
            sysUserDAO.updateSysUser(sysUser);
            if (sysUser.getRoleId() != null && !"".equals(sysUser.getRoleId())) {
                sysUserDAO.deleteUserRoleo(sysUser.getUuid());
                if (!Constants.DELETE_YES.equals(sysUser.getIsDeleted())) {
                    sysUserDAO.saveUserRoleo(sysUser.getRoleId(), sysUser.getUuid());
                }
            }
        } catch (Exception e) {
            log.info("更新用户信息失败！" + ExpUtil.getExceptionDetail(e));
            throw new BussinessException(e);
        }
        return sysUser;
    }

    /**
     * @Description: 根据uuid查询用户详情
     * @author dongsl
     * @Date 2018年6月13日下午1:10:20
     */
    @Override
    public SysUser findSysUserById(java.lang.String uuid) throws Exception {
        try {
            SysUser su = new SysUser();
            su = sysUserDAO.getSysUser(uuid);
            return su;
        } catch (Exception e) {
            log.error("查询用户详情失败！" + ExpUtil.getExceptionDetail(e));
            throw new BussinessException(e);
        }
    }

    /**
     * @Description: 删除用户
     * @author dongsl
     * @Date 2018年6月13日下午1:53:09
     */
    @Override
    public void removeSysUser(String uuid) throws Exception {
        try {
            sysUserDAO.removeSysUser(uuid);
        } catch (Exception e) {
            log.error("删除用户失败！" + ExpUtil.getExceptionDetail(e));
            throw new BussinessException(e);
        }
    }

    /**
     * @Description: 查询用户列表
     * @author dongsl
     * @Date 2018年6月13日下午1:53:53
     */
    @Override
    public Pager queryPage(Pager pager) throws Exception {
        int totalCount = 0;
        List<SysUser> userlist = null;
        String uuId = pager.getF().get("uuId");
        List<SysUserRole> userRoleIds = sysRoleDAO.getUserRoleByUserUuid(uuId);

        // TODO: 2018/11/19 lxh 只有超级管理员能看到所有的列表，其他人都是只能看到自己创建的列表
        pager.getF().put("isAdmin", userRoleIds.contains(Constants.SYS_ROLE_SUPERADMIN) ? "1" : "0");

        userlist = sysUserDAO.queryUser(pager);
        totalCount = sysUserDAO.queryCount(pager);

        pager.setTotalCount(totalCount);
        pager.setResultList(userlist);
        return pager;

    }

    /**
     * 登录方法
     */
    public SysUser login(SysUser sysUser) {
        SysUser user = new SysUser();
        try {
            user = sysUserDAO.getLoginUser(sysUser);
        } catch (Exception e) {
            log.error("查询登录用户失败");
        }
        return user;
    }

    /**
     * @Description: 根据用户名查询用户
     * @author dongsl
     * @Date 2018年6月13日下午1:55:22
     */
    public List<SysUser> queryUserByUserName(String id, String username) throws Exception {
        List<SysUser> list = new ArrayList<SysUser>();
        try {
            list = sysUserDAO.queryUserByUserName(id, username);
        } catch (Exception e) {
            log.error("根据用户名查询用户失败" + ExpUtil.getExceptionDetail(e));
        }
        return list;
    }

    /**
     * 查询有审批权限的用户
     */
    @Override
    public ResponseBean getUserHaveRatify() {
        //lxh 审批的权限id不建议更改
        SysResource sysResource = sysResourceDAO.getSysResource(Constants.JOB_RATIFY_QUALIFICATION);
        List<SysUser> list = sysUserDAO.getUserHaveRatify(sysResource.getId());
        return ResultUtils.success("resultList", list);
    }

    @Override
    public ResponseBean setSessionTimeOut(Integer sessionTimeOut) {
        onlineUserListener.setSessionTimeOut(sessionTimeOut);
        return ResultUtils.success();
    }


}