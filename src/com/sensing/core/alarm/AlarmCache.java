package com.sensing.core.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 告警缓存类
 * <p>Title: AlarmCache</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年10月30日
 * @version 1.0
 */
public class AlarmCache {

	//通道任务关联关系缓存<通道uuid,布控任务的uuid>
	public static Map<String,List<String>> channelJobMap = new ConcurrentHashMap<String,List<String>>();
	//任务缓存
	public static Map<String,JobBean> jobMap = new ConcurrentHashMap<String,JobBean>();
	//任务关联的模板库的map
	public static Map<String,List<Integer>> jobTemplateDbMap = new HashMap<String, List<Integer>>();
	

	//告警的缓存 key:personId+jobsId+vehicleId，value:告警对象
	public static Map<String, AlarmInfo> alarmInfoMap = new ConcurrentHashMap<String,AlarmInfo>();
//	public static Map<String, Long> alarmInfoMap = new ConcurrentHashMap<String,Long>();
	
	//线程数量
	public static Short threadCount = 10;
	//计数器
	public static AtomicInteger alarmProcessAI = new AtomicInteger(0);
	//流程获取的数据缓冲区  key每个流程的id，value每个流程需要处理的数据集合
	public static Map<Integer,List<CapBean>> capDataMap = new ConcurrentHashMap<Integer,List<CapBean>>();
	
	//可用的模板库的id缓存
	public static List<Integer> templateDbList = new ArrayList<Integer>();
	
	// 初始化
	static{
		//channelJobMap
		List<String> jobsList = new ArrayList<String>();
		jobsList.add("jobsUuid83b2868bf2");
		channelJobMap.put("deviceUuid5232125487",jobsList);
		
		//jobMap
		JobBean job = new JobBean();
		job.setUuid("jobsUuid83b2868bf2");
		jobMap.put("jobsUuid83b2868bf2",job);
		
		//jobTemplateDbMap
		List<Integer> templateDbList = new ArrayList<Integer>();
		templateDbList.add(1);
		templateDbList.add(5);
		templateDbList.add(9);
		jobTemplateDbMap.put("jobsUuid83b2868bf2",templateDbList);
		
	}
}
