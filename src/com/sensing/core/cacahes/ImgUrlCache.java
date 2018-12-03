package com.sensing.core.cacahes;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * 
 * com.sensing.core.cacahes
 * 本地缓存
 * @author haowenfeng
 * @date 2018年4月12日 下午7:52:03
 */
@Service
public class ImgUrlCache {
	
	/**
	 * 存储未被删除的区域信息（根据通道ID查询出的数据）
	 * key = orgId ; value = imgUrl
	 */
	public static ConcurrentHashMap<String, String> imgData = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<Integer, String> ftData = new ConcurrentHashMap<Integer, String>();	//存储ftdbId,imgserverUrl
	public static ConcurrentHashMap<String, String> channelData = new ConcurrentHashMap<String, String>();//存储channelId,imgserverUrl
	public static Object capCacheMapMutex = new Object();	
	
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static synchronized void putImgUrl(String key,String value){
		synchronized(capCacheMapMutex){
			imgData.put(key, value);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static synchronized void putFtUrl(Integer key,String value){
		synchronized(capCacheMapMutex){
			ftData.put(key, value);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static synchronized void putChannelUrl(String key,String value){
		synchronized(capCacheMapMutex){
			channelData.put(key, value);
		}
	}
	
	/**
	 * 
	 * @param key
	 */
	public static synchronized void removeImgUrl(String key){
		synchronized(capCacheMapMutex){
			imgData.remove(key);
		}
	}
}
