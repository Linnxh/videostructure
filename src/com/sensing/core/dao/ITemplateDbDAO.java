package com.sensing.core.dao;

import java.util.List;
import com.sensing.core.bean.TemplateDb;
import com.sensing.core.utils.Pager;

/**
 * @author mingxingyu
 */
public interface ITemplateDbDAO {
	public int saveTemplateDb(TemplateDb templateDb) throws Exception;

	public TemplateDb getTemplateDb(java.lang.Integer id, Integer isDeleted) throws Exception;

	public int removeTemplateDb(java.lang.Integer id) throws Exception;

	public int updateTemplateDb(TemplateDb templateDb) throws Exception;

	public List<TemplateDb> queryList(Pager pager) throws Exception;

	public int selectCount(Pager pager) throws Exception;

	public List<TemplateDb> queryTemplateDbByName(String templatedbName);

}
