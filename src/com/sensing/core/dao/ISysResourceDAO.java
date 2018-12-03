package com.sensing.core.dao;

import java.util.List;

import com.sensing.core.bean.SysResource;
import com.sensing.core.utils.Pager;

/**
 * @author wenbo
 */
public interface ISysResourceDAO {
    public int saveSysResource(SysResource sysResource);

    public SysResource getSysResource(java.lang.Integer id);

    public int removeSysResource(java.lang.Integer id);

    public int updateSysResource(SysResource sysResource);

    public List<SysResource> queryList(Pager pager);

    public int selectCount(Pager pager);

    /**
     * 根据用户名查询所属资源信息
     */
    public List<SysResource> selectResourceByUserName(String userName);

    List<SysResource> getSysResoByRoleIds(List<Integer> list);

    List<Integer> getRoleByUuId(String uuid);

    public List<SysResource> queryResourceByMethod();

}
