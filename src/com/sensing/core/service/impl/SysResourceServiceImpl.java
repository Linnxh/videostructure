package com.sensing.core.service.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.sensing.core.bean.SysResourceResp;
import com.sensing.core.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.SysResource;
import com.sensing.core.cacahes.CacheManagerImpl;
import com.sensing.core.dao.ISysResourceDAO;
import com.sensing.core.service.ISysResourceService;
import com.sensing.core.utils.BussinessException;
import com.sensing.core.utils.Pager;
import org.springframework.util.CollectionUtils;

/**
 * @author wenbo
 */
@Service
public class SysResourceServiceImpl implements ISysResourceService {


    private static final Log log = LogFactory.getLog(ISysResourceService.class);

    @Resource
    public ISysResourceDAO sysResourceDAO;

    public SysResourceServiceImpl() {
        super();
    }

    @Override
    public SysResource saveNewSysResource(SysResource sysResource) throws Exception {
        try {
            sysResourceDAO.saveSysResource(sysResource);
        } catch (Exception e) {
            log.error(e);
            throw new BussinessException(e);
        }
        return sysResource;
    }

    @Override
    public SysResource updateSysResource(SysResource sysResource) throws Exception {
        sysResourceDAO.updateSysResource(sysResource);
        return sysResource;
    }

    @Override
    public SysResource findSysResourceById(Integer id) throws Exception {
        try {
            return sysResourceDAO.getSysResource(id);
        } catch (Exception e) {
            log.error(e);
            throw new BussinessException(e);
        }
    }

    @Override
    public void removeSysResource(Integer id) throws Exception {
        try {
            sysResourceDAO.removeSysResource(id);
        } catch (Exception e) {
            log.error(e);
            throw new BussinessException(e);
        }
    }

    @Override
    public Pager queryPage(Pager pager) throws Exception {
        try {
            List<SysResource> list = sysResourceDAO.queryList(pager);
            int totalCount = sysResourceDAO.selectCount(pager);
            pager.setTotalCount(totalCount);
            pager.setResultList(list);
        } catch (Exception e) {
            log.error(e);
            throw new BussinessException(e);
        }
        return pager;
    }

    /**
     * 根据用户名查询所属资源信息
     */
    public List<SysResource> queryResourceByUserName(String userName) throws Exception {
        return sysResourceDAO.selectResourceByUserName(userName);
    }

    /**
     * 排序问题，代码中排序
     * @param uuid
     * @return
     */
    @Override
    public List<SysResource> getSysResoByUuid(String uuid) {
        if (StringUtils.isEmptyOrNull(uuid)) {
            return null;
        }
        List<SysResource> list = getSysReso(uuid);
        /***  一级分类   *****/
        List<SysResource> first = list.stream().filter(a -> a.getParentId().intValue() == 0).collect(Collectors.toList());
        for (SysResource firstReso : first) {
            /***  二级分类   *****/
            List<SysResource> second = list.stream().filter(a -> firstReso.getId().intValue() == a.getParentId().intValue()).collect(Collectors.toList());
            for (SysResource secondReso : second) {
                /***  三级分类   *****/
                List<SysResource> third = list.stream().filter(a -> secondReso.getId().intValue() == a.getParentId().intValue()).collect(Collectors.toList());
                for (SysResource thirdReso : third) {
                    /***  四级分类   *****/
                    List<SysResource> four = list.stream().filter(a -> thirdReso.getId().intValue() == a.getParentId().intValue()).collect(Collectors.toList());
                    four.sort(Comparator.comparing(SysResource::getOrde));
                    thirdReso.setChildResoList(four);
                }
                third.sort(Comparator.comparing(SysResource::getOrde));
                secondReso.setChildResoList(third);
            }
            second.sort(Comparator.comparing(SysResource::getOrde));
            firstReso.setChildResoList(second);
        }
        first.sort(Comparator.comparing(SysResource::getOrde));
        return first;
    }

    @Override
    public List<String> getSysResoSearchCode(String uuid) {
        List<SysResource> list = getSysReso(uuid);
        List<String> serchCodeList = list.stream().map(a -> a.getSearchCode()).collect(Collectors.toList());
        return serchCodeList;
    }

    private List<SysResource> getSysReso(String uuid) {
        // 2018/9/5  一个用户会对应多个角色，多个角色的时候取合集
        List<Integer> roleIds = sysResourceDAO.getRoleByUuId(uuid);
        if (CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        List<SysResource> list = sysResourceDAO.getSysResoByRoleIds(roleIds);
        return list;
    }


    @Override
    public void queryResource() throws Exception {
        List<SysResource> getRes = sysResourceDAO.queryResourceByMethod();
        for (SysResource sysR : getRes) {
            CacheManagerImpl.putCache(sysR.getPath(), sysR.getTitle());
        }
    }

}