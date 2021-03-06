package com.sensing.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sensing.core.bean.Channel;
import com.sensing.core.utils.Pager;

/**
 * @author wenbo
 */
public interface IChannelDAO {
	public int saveChannel(Channel channel) throws Exception;

	public Channel getChannel(@Param("uuid")String uuid,@Param("isDel") String isDel) throws Exception;
	
	public Channel getChannelAll(@Param("uuid")String uuid) throws Exception;

	public int removeChannel(Channel channel) throws Exception;

	public int updateChannel(Channel channel) ;
	
	public List<Channel> queryList(Pager pager) throws Exception;
	
	public int selectCount(Pager pager) throws Exception;
	
	public String queryMaxSearchCodeByCode(@Param("searchCode")String searchCode) throws Exception;
	
	/**
	 * 根据channel_no查询通道
	 * author dongsl
	 * date 2017年8月15日下午5:22:23
	 */
	public List<Channel> queryChannelByChannelNoAndAddr(@Param("channelNo") String channelNo,@Param("channelAddr")String channelAddr,@Param("isDel")short isDel) throws Exception;
	
	public List<Channel> queryChannelByChannelNameAndRegionId(@Param("channelName") String channelName,@Param("regionId") Integer regionId,@Param("isDel")short isDel) throws Exception;
	
	/**
	 * 根据通道uuid查询一条通道信息
	 * @param channelId
	 * @param isDel
	 * @return
	 * @throws Exception
	 */
	public Channel getOneChannelByUuid(String channelId,String isDel) throws Exception;

	/**
	 * 查询全通道数据
	 * @return
	 */
	public List<Channel> selectChannelList() throws Exception;

	/**
	 * @Description: 根据区域id查询所有子通道列表  
	 * @param regionId
	 * @return
	 * @throws Exception    
	 * @return List<String>    
	 * @author dongsl
	 * @Date 2017年10月24日下午1:00:34
	 */
	public List<String> queryChannelListByRegionSearchCode(@Param("regionSearchCode")String regionSearchCode) throws Exception;
	
	public List<Channel> getChannelByIds(@Param("ids")String... ids);

	public int queryVideoByChannelName(String fileName);
}
