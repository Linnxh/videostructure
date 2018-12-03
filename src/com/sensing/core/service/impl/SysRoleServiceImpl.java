package com.sensing.core.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.sensing.core.bean.*;
import com.sensing.core.dao.ISysResourceDAO;
import com.sensing.core.dao.ISysRoleResoDAO;
import com.sensing.core.dao.ISysUserDAO;
import com.sensing.core.service.ISysResourceService;
import com.sensing.core.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sensing.core.dao.ISysRoleDAO;
import com.sensing.core.service.ISysRoleService;
import com.sensing.core.utils.Pager;
import org.springframework.util.CollectionUtils;

/**
 * @author wenbo
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {


    private static final Log log = LogFactory.getLog(ISysRoleService.class);

    @Resource
    public ISysRoleDAO sysRoleDAO;
    @Resource
    public ISysResourceDAO sysResourceDAO;
    @Resource
    public ISysUserDAO sysUserDAO;
    @Resource
    public ISysRoleResoDAO sysRoleResoDAO;
    @Resource
    public ISysResourceService sysResourceService;


    public SysRoleServiceImpl() {
        super();
    }

    @Override
    public int saveNewSysRole(SysRoleSaveReq req) {

        /*** sysRole ***/
        SysUser sysUser = sysUserDAO.getSysUser(req.getUserUUid());
        SysRole role = new SysRole();
        // TODO: 2018/9/10 lxh  根据用户uuid查询对应的roleid  集合  取最大的
        int maxRoleId = getMaxRoleId(req.getUserUUid());
        role.setParentId(maxRoleId);
        role.setRoleName(req.getRoleName());
        role.setAddUuId(req.getUserUUid());
        int code1 = sysRoleDAO.saveSysRole(role);
//        List<SysUserRole> userRoleByUserUuid = sysRoleDAO.getUserRoleByUserUuid(req.getUserUUid());
        /*** sysRoleReso ***/
        List<SysRoleReso> ssList = new ArrayList<>();
        List<Integer> resIds = req.getSysResourceIds();
        if (CollectionUtils.isEmpty(resIds)) {
            return -1;
        }
        SysRoleReso reso = null;
        for (Integer resId : resIds) {
            reso = new SysRoleReso();
            reso.setResoId(resId);
            reso.setRoleId(role.getId());
            ssList.add(reso);
        }
        int code2 = sysRoleResoDAO.saveSysRoleResoBetch(ssList);
        return code2;
    }

    public int getMaxRole(String[] roleIds) {
        List<String> roleList = Arrays.asList(roleIds);
        List<Integer> roleList2 = roleList.stream().map(a -> Integer.parseInt(a)).collect(Collectors.toList());
        Integer max = Collections.max(roleList2);
        return max.intValue();
    }

    @Override
    public int updateSysRole(SysRoleSaveReq req) {

        /*** sysrole ***/
        int code1 = sysRoleDAO.updateSysRole(req);

        /*** sysRoleReso ***/
        //删除
        sysRoleResoDAO.delSysResoByRoleId(req);
        if (req.getIsDeleted() == null || (req.getIsDeleted() != null && req.getIsDeleted() == 0)) {
            //增加
            sysRoleResoDAO.insertBetchSysReso(req);
        }
        return code1;

    }

    @Override
    public Pager queryPage(SysRoleReq req) {


        List<Integer> pIds = null;
        List<SysRole> list = null;
        int totalCount = 0;
        int maxRole = getMaxRoleId(req.getUuid());

        //        if (maxRole == Constants.SYS_ROLE_SUPERADMIN) {
//            // 超级管理员 所有用户
//            list = sysRoleDAO.queryList(req);
//            totalCount = sysRoleDAO.selectCount(req);
//        } else if (maxRole == Constants.SYS_ROLE_ADMIN) {
//            //管理员 自己创建的角色
//            req.setpIds(Arrays.asList(Constants.SYS_ROLE_ADMIN));
//            list = sysRoleDAO.queryList(req);
//            totalCount = sysRoleDAO.selectCount(req);
//        } else if (maxRole == Constants.SYS_ROLE_SYSUSER) {
//            // 系统用户
//            // TODO: 2018/9/4 自定义异常返回
//        } else if (maxRole == Constants.SYS_ROLE_BUSINESS) {
//            // 业务用户
//        }else {
//            //自定义用户
//
//
//        }`


        // TODO: 2018/11/19 lxh pid 这个字段未用到
        // TODO: 2018/11/15 lxh 除了管理员其他人只能看到自己创建的角色

        req.setIsAdmin(maxRole == Constants.SYS_ROLE_SUPERADMIN ? 1 : 0);
        list = sysRoleDAO.queryList(req);
        totalCount = sysRoleDAO.selectCount(req);

        if (!CollectionUtils.isEmpty(list)) {
            List<Integer> roleIds = list.stream().map(a -> a.getId()).collect(Collectors.toList());
            //   根据roleid查询sysuser对应的用户个数
            List<UserRoleCount> counts = sysRoleDAO.getUserRoleCount(roleIds);
            for (SysRole r : list) {
                List<UserRoleCount> cList = counts.stream().filter(a -> a.getRoleId() == r.getId().intValue()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(cList) && cList.size() == 1) {
                    r.setRoleCount(cList.get(0).getCount());
                }
            }
        }

        Pager pager = new Pager();
        pager.setPageNo(req.getPageNo());
        pager.setPageRows(req.getPageRows());
        pager.setTotalCount(totalCount);
        pager.setResultList(list);

        return pager;

    }

    @Override
    public List<SysResource> getSysResOnEdit(SysResoEdidReq req) {

        /*** 当前用户有的资源，得到的是子父级别的数据 ***/
        List<SysResource> allReso = sysResourceService.getSysResoByUuid(req.getUserUuid());

        /***  该角色已经有的资源列表 ***/
        List<SysResource> haveReso = sysResourceDAO.getSysResoByRoleIds(Arrays.asList(req.getRoleId()));

        List<Integer> haveResoIds = haveReso.stream().map(a -> a.getId().intValue()).collect(Collectors.toList());
        for (SysResource s : allReso) {
            /*** 一级分类 **/
            s.setCheckState(haveResoIds.contains(s.getId().intValue()));
            List<SysResource> secondList = s.getChildResoList();
            if (!CollectionUtils.isEmpty(secondList)) {
                for (SysResource secondReso : secondList) {
                    /*** 二级分类 **/
                    secondReso.setCheckState(haveResoIds.contains(secondReso.getId().intValue()));
                    List<SysResource> thirdList = secondReso.getChildResoList();
                    if (!CollectionUtils.isEmpty(thirdList)) {
                        for (SysResource thirdReso : thirdList) {
                            /*** 三级分类 **/
                            thirdReso.setCheckState(haveResoIds.contains(thirdReso.getId().intValue()));
                            List<SysResource> fourthResoList = thirdReso.getChildResoList();
                            if (!CollectionUtils.isEmpty(fourthResoList)){
                                for (SysResource fourthReso : fourthResoList) {
                                    /*** 四级级分类 **/
                                    fourthReso.setCheckState(haveResoIds.contains(fourthReso.getId().intValue()));
                                }
                            }
                        }
                    }
                }
            }
        }

        return allReso;
    }


    private int getMaxRoleId(String userUuid) {
        //通过userUuid得到角色集合，得到最大的角色
        List<SysUserRole> roleList = sysRoleDAO.getUserRoleByUserUuid(userUuid);
        // TODO: 2018/9/6 lxh 角色 
        OptionalInt max = roleList.stream().mapToInt(SysUserRole::getRoleId).max();
        return max.isPresent() ? max.getAsInt() : 0;
    }

}