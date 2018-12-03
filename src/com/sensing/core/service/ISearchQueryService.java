package com.sensing.core.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.utils.Pager;

public interface ISearchQueryService {

	/**
	 * 获取mongodb的单个对象
	 * 
	 * @param uuid    抓拍的uuid
	 * @param capType 抓拍的类型
	 * @return
	 * @author mingxingyu
	 * @throws Exception
	 * @date 2018年9月20日 下午2:47:56
	 */
	public Object getObjectByUuid(String uuid, String capType) throws Exception;

	/**
	 * 抓拍图的以图搜图调用比对查询
	 * 
	 * @param jo 参数
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年9月20日 下午3:02:49
	 */
	public List<Object> queryResultByCmpByUuid(JSONObject jo) throws Exception;

	/**
	 * 以图搜图调用比对查询
	 * 
	 * @param jo
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年9月17日 下午4:44:02
	 */
	public List<Object> queryResultByCmp(JSONObject jo) throws Exception;

	Pager queryPage(Pager pager) throws Exception;

	List<Map<String, Object>> queryChannelTraffic(String deviceId, Integer dateScope) throws Exception;

	Map<String, Object> queryCapByUuid(String uuid, Integer capType) throws Exception;

	Map<String, Object> trafficCount(String ids, String startTime, String endTime) throws Exception;

	Map<String, Object> queryCapByUuids(Integer capType, String uuids) throws Exception;

}
