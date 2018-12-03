package com.sensing.core.dao;

import java.util.Map;

/**
 * 
 * <p>Title: ICmpDao</p>
 * <p>Description:数据库的比对</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年11月26日
 * @version 1.0
 */
public interface IAlarmInfoSaveDAO {
	/**
	 * 保存抓拍数据
	 * @param params
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date   2018年11月27日 上午11:07:02
	 */
	public int saveCapture(Map<String,String> params)throws Exception;
	
	/**
	 * 保存告警关联表
	 * @param params
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date   2018年11月27日 上午11:07:17
	 */
	public int saveAlarmCmpLogs(Map<String,String> params)throws Exception;
	
	/**
	 * 保存告警
	 * @param params
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date   2018年11月27日 上午11:07:41
	 */
	public int saveAlarm(Map<String,Object> params)throws Exception;
	
}
