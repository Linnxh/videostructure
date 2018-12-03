package com.sensing.core.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.ChannelQueryResult;
import com.sensing.core.bean.Regions;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.bean.SysUser;
import com.sensing.core.bean.TaskChannel;
import com.sensing.core.service.IAuthorizationService;
import com.sensing.core.service.IChannelService;
import com.sensing.core.service.IRegionsService;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.service.ISysUserService;
import com.sensing.core.service.ITaskService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.ExcelHelper;
import com.sensing.core.utils.ExpUtil;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.ValidationUtils;
import com.sensing.core.utils.props.PropUtils;

/**
 * 通道管理
 * @author admin
 *
 */
@Controller
@RequestMapping("/channel")
@SuppressWarnings("all")
public class ChannelController extends BaseController {

	private static final Log log = LogFactory.getLog(ChannelController.class);

	@Resource
	public IAuthorizationService authorizationService;
	@Resource
	public IChannelService channelService;
	@Resource
	public IRegionsService regionsService;
	@Resource
	public ISysUserService sysUserService;
	@Resource
	public ITaskService taskService;
	
	@ResponseBody
	@RequestMapping("/query")
	public ResponseBean query(@RequestBody JSONObject p,HttpServletRequest request) {
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		ResponseBean result = new ResponseBean();
		try {
			//如果regionId为－1，表示查询所有通道
			if(pager.getF().get("regionId")!=null && pager.getF().get("regionId").equals("-1")){
				pager.getF().put("regionId", "");
			}
			String token = request.getHeader("token");
			pager = channelService.queryPage(pager);
			result.getMap().put("pager", pager);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			result.setError(-1);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	/**
	 * 查询通道的数量
	 * @param p
	 * @return
	 * @author mingxingyu
	 * @date   2018年7月27日 上午10:14:01
	 */
	@ResponseBody
	@RequestMapping("/queryChannelCount")
	public ResponseBean queryChannelCount(@RequestBody JSONObject p,HttpServletRequest request) {
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		ResponseBean result = new ResponseBean();
		try {
			//如果regionId为－1，表示查询所有通道
			if(pager.getF().get("regionId")!=null && pager.getF().get("regionId").equals("-1")){
				pager.getF().put("regionId", "");
			}
			String token = request.getHeader("token");
			pager.getF().put("token",token);
			int channelCount = channelService.queryChannelCount(pager);
			result.getMap().put("channelCount", channelCount);
			result.setError(0);
			result.setMessage("successful");
		} catch (Exception e) {
			log.error(e);
			result.setError(-1);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 查询全通道信息
	 * @param json
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryChannels")
	public ResponseBean queryChannels(@RequestBody JSONObject json) {
		ResponseBean result = new ResponseBean();
		List<Channel> channelList  =  null;
		try {
			channelList = channelService.queryChannelList();
		} catch (Exception e) {
			log.error("查询通道信息异常!",e);
			result.setError(-1);
			result.setMessage("查询通道信息异常!");
			return result;
		}
		if(channelList!=null&&channelList.size()>0){
			result.setError(0);
			result.setMessage("success");
			result.getMap().put("channelList", channelList);
			return result;
		}else{
			log.info("查询到的数据库通道信息为空!");
			result.setError(100);
			result.setMessage("fail");
			return result;
		}
		
		
	}
	
	/**
	 * @Description: 根据通道id查询通道信息 
	 * @param json
	 * @return    
	 * @return ResponseBean    
	 * @author dongsl
	 * @Date 2017年9月5日上午9:35:28
	 */
	@ResponseBody
	@RequestMapping("/queryById")
	public ResponseBean queryById(@RequestBody JSONObject json) {
		long long1 = System.currentTimeMillis();
		ResponseBean result = new ResponseBean();
		if(json==null||json.isEmpty()){
			log.error("请求参数非法!");
			result.setError(100);
			result.setMessage("请求参数非法!");
			return result;
		}
		String channelId = json.getString("channelId");
		Channel channel = null;
		try {
			channel = channelService.getOneChannelByUuid(channelId,Constants.DELETE_NO.toString());
		} catch (Exception e) {
			log.error("查询通道信息异常!",e);
			result.setError(-1);
			result.setMessage("查询通道信息异常!");
			return result;
		}
		result.setError(0);
		result.getMap().put("model", channel);
		result.setMessage("success");
		return result;
	}
	/**
	 * 修改通道
	 * author dongsl
	 * date 2017年8月8日下午1:41:48
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/update")
	public ResponseBean update(@RequestBody JSONObject json,HttpServletRequest request)
	{
		String uuid=request.getHeader("uuid");
		ResponseBean result = new ResponseBean();
		if(json==null||json.isEmpty()){
			log.error("请求参数非法!");
			result.setError(100);
			result.setMessage("请求参数非法!");
			return result;
		}	
		Channel model = JSONObject.toJavaObject(json, Channel.class);
		String valRes = null;
		try {
			valRes = validateParam(model,2);
		} catch (Exception e) {
			log.error("请求参数非法!",e);
			result.setError(-1);
			result.setMessage("请求参数非法");
			return result;
		}	
		//1表示验证添加方法，2表示验证修改方法
		if(StringUtils.isNotEmpty(valRes)){	//如果验证通过，则进行下一步，否则返回错误信息
			result.setError(2002);
			result.setMessage(valRes);
		}else{
			try {
				//设置数据的修改记录
				model.setModifyUser(uuid);
				model = channelService.updateChannel(model);
				result.getMap().put("model", model);
				result.setError(0);
				result.setMessage("successful");
			} catch (Exception e) {
				log.error(e);
				result.setError(-1);
				result.setMessage(e.getMessage());
			}
		}
		return result;
	}
	/**
	 * 通道保存
	 * author dongsl
	 * date 2017年8月8日上午11:41:39
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/save")
	public ResponseBean save(@RequestBody JSONObject json,HttpServletRequest request) {
		ResponseBean result = new ResponseBean();
		String uuid=request.getHeader("uuid");
		Long d1 = new Date().getTime();
		if(json==null||json.isEmpty()){
			result.setError(100);
			result.setMessage("请求参数非法,参数传递为空!");
			return result;
		}	
		Channel model = JSONObject.toJavaObject(json, Channel.class);		
		String valRes = validateParam(model,1);
		if(StringUtils.isNotEmpty(valRes)){	//如果验证通过，则进行下一步，否则返回错误信息
			result.setError(100);
			result.setMessage(valRes);
			return result;
		}
		try {
			if(model.getReserved()==null){
				model.setReserved(1);
			}
			//从rtsp地址增加rtmp流地址
			if ( (model.getChannelRtmp() == null || "".equals(model.getChannelRtmp())) 
					&& model.getChannelAddr() != null  && !"".equals(model.getChannelAddr()) ) {
				if ( model.getChannelAddr().startsWith("rtsp://") ) {
					String[] addrArr = model.getChannelAddr().split("/");
					if ( addrArr != null && addrArr.length >= 2  ) {
						String ip = addrArr[2];
						//rtsp的ip没有554的情况下，需要添加该端口
						String addr = model.getChannelAddr();
						if ( !ip.contains(":") && !ip.contains("554")) {
							addr = addr.replaceAll(ip,ip+":554");
						}
						addr = addr.replaceAll("rtsp://","rtmp://"+PropUtils.getString("streamMedia.ip")+"/live/");
						model.setChannelRtmp(addr);
					}
				}
			}
			model.setCreateUser(uuid);
			model = channelService.saveNewChannel(model);
			result.getMap().put("model", model);
			result.setError(0);
			result.setMessage("successful");
			} catch (Exception e) {
				result.setError(-1);
				result.setMessage(e.getMessage());
			}
	
		return result;
	}
	/**
	 * 保存通道信息之前验证	type=11验证save方法，type=2验证update方法
	 * author dongsl
	 * date 2017年8月8日上午11:42:40
	 */
	public String validateParam(Channel channel,int type){
		try{
			//更新通道校验
			if (type == 2) {
				// 通道uuid是否存在
				if (!StringUtils.isNotEmpty(channel.getUuid())) {
					return "通道uuid不能为空！！";
				}
				Channel oldChannel = channelService.findChannelById(channel.getUuid());
				//当修改通道时，查看通道名称是否和已存在的其它通道重复，重复则不允许修改。
				if(StringUtils.isNotEmpty(oldChannel.getChannelName()) && !oldChannel.getChannelName().equals(channel.getChannelName())){
					List listt = channelService.queryChannelByChannelNameAndRegionId(channel.getChannelName(), channel.getRegionId(), Constants.DELETE_NO);
					if (listt != null && listt.size() > 0) {
						return "该分组已存在名为：" + channel.getChannelName() + "的通道！";
					}
				}
				//当修改通道时，查看通道号和通道地址是否和已存在的其它通道重复，重复则不允许修改。
				if(StringUtils.isNotEmpty(oldChannel.getChannelNo()) && !oldChannel.getChannelNo().equals(channel.getChannelNo())){
					if(!channel.getChannelAddr().contains("rtsp:")){
						List list = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),channel.getChannelAddr());
						if (list != null && list.size() > 0) {
							return "通道号和通道地址已存在！";
						}
					}
				}
				//当修改通道时，查看通道号和通道地址是否和已存在的其它通道重复，重复则不允许修改。
				if(StringUtils.isNotEmpty(oldChannel.getChannelAddr()) && !oldChannel.getChannelAddr().equals(channel.getChannelAddr())){
						
					if(!channel.getChannelAddr().contains("rtsp:")){
						List list = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),channel.getChannelAddr());
						if (list != null && list.size() > 0) {
							return "通道号和通道地址已存在！";
						}
					}
				}
			//添加通道校验
			}else{
				//通道号是否重复
				if (channel.getChannelNo() != null && !"".equals(channel.getChannelNo())) {
					List list = channelService.queryChannelByChannelNoAndAddr(channel.getChannelNo(),channel.getChannelAddr());
					if (list != null && list.size() > 0) {
						return "通道号和通道地址已存在！";
					}
				}
				List listt = channelService.queryChannelByChannelNameAndRegionId(channel.getChannelName(), channel.getRegionId(), Constants.DELETE_NO);
				if (listt != null && listt.size() > 0) {
					return "该分组已存在名为：" + channel.getChannelName() + "的通道！";
				}
			}
			// 通道名称是否为空
			if (!StringUtils.isNotEmpty(channel.getChannelName())) {
				return "通道名称不能为空！";
			}
			// 区域id是否为空
			if (channel.getRegionId() == null) {
				return "通道必须属于某个分组下！";
			} else {
				Regions region = regionsService.findRegionsById(channel.getRegionId());
				if (region == null) {
					return "所属分组不存在！";
				}
			}
			
			// 通道名称长度不超过50
			if (!ValidationUtils.checkStrLength(channel.getChannelName(), 1, 50)) {
				return "通道名称长度最大为50位！！";
			}
			// 通道Guid是否为空
			if (!StringUtils.isNotEmpty(channel.getChannelName())) {
				return "通道名称不能为空！";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelVerid(),0, 64)) {
				return "通道verid最大长度为64位！";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelDescription(), 0, 512)) {
				return "通道描述信息最大长度为512位！";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelAddr(),0, 512)) {
				return "通道通道地址最大长度为512位！";
			}
			if(!(channel.getReserved()==null)&&channel.getReserved()==2)
			{
				if (!ValidationUtils.checkStrByPattern( "(rtsp):\\/\\/[^\\u4e00-\\u9fa5]+", channel.getChannelAddr())) {
					return "通道地址必须rtsp://开头的非中文组合";
				}
			}
			if(!(channel.getReserved()==null)&&channel.getReserved()==6)
			{
				if (!ValidationUtils.checkStrByPattern( "(gb):\\/\\/[^\\u4e00-\\u9fa5]+", channel.getChannelAddr())) {
					return "通道地址必须gb://开头的非中文组合";
				}
			}
			// 通道位置长度不超过120
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelArea(),0, 512)) {
				return "通道位置长度最大为512位！！";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelNo(), 0,30)) {
				return "通道NO长度不能超过30!";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelUid(), 0,20)) {
				return "channelUid长度不能超过20!";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getChannelPsw(), 0,20)) {
				return "channelPsw长度不能超过20!";
			}
			if (!ValidationUtils.checkStrLengthWithNull(channel.getSdkVer(), 0,32)) {
				return "channelSdkVer长度不能超过32!";
			}
		}catch(Exception e){
			log.error("通道校验失败！" + ExpUtil.getExceptionDetail(e));
		}
		return "";
	}
	/**
	 * 删除通道信息(逻辑删除)
	 * author dongsl
	 * date 2017年8月8日下午1:42:39
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public ResponseBean delete(@RequestBody JSONObject json,HttpServletRequest request , HttpServletResponse response) {
		ResponseBean result = new ResponseBean();
		String id=json.getString("id");
		String uuid=request.getHeader("uuid");
		if(id==null || "".equals(id)){
			result.setError(2003);
			result.setMessage("所删除的通道不存在！");
		}else{
			try {
				Channel channel=channelService.findChannelById(id);
				if(channel==null)
				{
					result.setError(100);
					result.setMessage("通道不存在或已删除");
				}else {
					List<String> list=new ArrayList<String>();
					list.add(id);
					List<TaskChannel> listc;
					listc = taskService.getTaskChannelByChannelIds(list);
					if(listc!=null&&listc.size()>0)
					{
						result.setError(100);
						result.setMessage("该通道正在执行任务,请删除任务后删除");
					}else {
						int c=channelService.removeChannel(id,uuid);
						if(c==1) {
							result.setError(0);
							result.setMessage("删除成功");
						}
						else {
							result.setError(100);
							result.setMessage("删除失败");
						}
					}
					
				}
			} catch (Exception e) {
				log.error(e);
				result.setError(-1);
				result.setMessage(e.getMessage());
			}
		}
		
		return result;
	}
	@ResponseBody
	@RequestMapping("/queryThriftChannel")
	public ChannelQueryResult queryThriftChannel(@RequestBody JSONObject json) {		
		Integer regionID = json.getInteger("regionID");
		Integer nStartNum = json.getInteger("nStartNum");
		Integer nCount = json.getInteger("nCount");
		ChannelQueryResult cqr = new ChannelQueryResult();
		try {
			cqr = channelService.QueryChannelsByRegionID(regionID, nStartNum, nCount);
		} catch (Exception e) {
			log.error("查询通道信息异常!",e);
			e.printStackTrace();
			return cqr;
		}
		return cqr;
	}
	 /**
     * 导出通道模板
     * 不完整，需要等待产品确认字段
     * @throws IOException 
     */
    @RequestMapping("/exportChannel")
    @ResponseBody
    public void exportChannel( HttpServletResponse response) throws IOException {
        List<String> nameList = new ArrayList<String>();
        ExcelHelper<Channel> util = new ExcelHelper(Channel.class);
        List<Channel> datas = new ArrayList<Channel>();
        String fileName = "通道模板.xlsx";
        fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
        response.reset();
        response.setContentType("application/ms-excel;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        OutputStream output = response.getOutputStream();
        util.exportExcel(datas, "模板项", 60000, output);
    }
	/**
	 * 导入数据
	 * 不完整，需要确认导入的数据和存储
	 * @param file
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value = "/improtExcel", method = { RequestMethod.POST })  
	public ResponseBean ImprotExcel(@RequestParam(value="uploadFile")MultipartFile file) { 
		ResponseBean result = new ResponseBean();
		ExcelHelper<Channel> util = new ExcelHelper(Channel.class);
		try {  
		    List<Channel> list =util.importExcel("模板项", file);
//		    for(Channel c:list)
//		    {
//		    	System.out.println("name:"+c.getChannelName());
//		    }
		} catch (Exception e) {  
		    e.printStackTrace();  
		}  
		return result;
	}  
//	 /**
//     * 导出通道模板测试
//     * @throws IOException 
//     */
//    @RequestMapping("/Channeljsp")
//    public ModelAndView exportjsp(Model model) throws IOException {
//    	ModelAndView modelAndView = new ModelAndView("list");
//    	return modelAndView;
//    }
//	 /**
//   * 导入通道测试
//   * @throws IOException 
//   */
//  @RequestMapping("/Channelimport")
//  public ModelAndView exportjsp(Model model) throws IOException {
//  	ModelAndView modelAndView = new ModelAndView("import");
//  	return modelAndView;
//  }
	public static void main(String[] args) {
		String []strs= {"rtsp://dsfad","gb://sdafdsa","sfdsafa","rtsp://","rtsp://eee"};
		 String pattern = "(rtsp|gb):\\/\\/[^\\u4e00-\\u9fa5]+";//"^(rtsp:\\/\\/)";
		 for(String str:strs)
		 {
			 boolean isMatch = Pattern.matches(pattern, str);
			 System.out.println(isMatch);
		 }

	}
}
