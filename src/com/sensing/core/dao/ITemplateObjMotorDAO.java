package com.sensing.core.dao;

import java.util.List;
import com.sensing.core.bean.TemplateObjMotor;
import com.sensing.core.utils.Pager;

/**
 * @author mingxingyu
 */
public interface ITemplateObjMotorDAO {
	public int saveTemplateObjMotor(TemplateObjMotor templateObjMotor) ;

	public TemplateObjMotor getTemplateObjMotor(java.lang.String uuid) throws Exception;

	public int removeTemplateObjMotor(java.lang.String uuid) throws Exception;

	public int updateTemplateObjMotor(TemplateObjMotor templateObjMotor) throws Exception;

	public List<TemplateObjMotor> queryList(Pager pager) throws Exception;

	public int selectCount(Pager pager) throws Exception;

	public List<TemplateObjMotor> queryByTemplateDbId(Integer templateDbId) throws Exception;

	public void logicalDeleted(Integer templateDbId) throws Exception;

}
