package com.sensing.core.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.bean.Channel;
import com.sensing.core.service.ICapService;
import com.sensing.core.service.IChannelService;
import com.sensing.core.service.ISearchQueryService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.ImgUtil;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.results.ResultUtils;

import sun.misc.BASE64Encoder;

/**
 * 检索管理
 * 
 * @author wangdandan
 *
 */

@Controller
@RequestMapping("/search")
//@SuppressWarnings("all")
public class SearchQueryController extends BaseController {

	private static final Log log = LogFactory.getLog(SearchQueryController.class);

	@Resource
	private ISearchQueryService searchQueryService;
	@Resource
	private ICapService capService;
	@Resource
	private IChannelService channelService;

	/**
	 * 根据uuid获取图片的特征信息并进行比对
	 * 
	 * @param json
	 * @return
	 * @author mingxingyu
	 * @date 2018年9月19日 下午3:24:47
	 */
	@ResponseBody
	@RequestMapping("/queryResultByCmpByUuid")
	@SuppressWarnings("unchecked")
	public Object queryResultByCmpByUuid(@RequestBody JSONObject json) {
		log.info("根据抓拍的uuid查询以图搜图的结果，调用 search/queryResultByCmpByUuid接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			result = ResultUtils.FAIL();
			return result;
		}
		String capUuid = json.getString("uuid");
		Integer capType = json.getInteger("capType");
		if (StringUtils.isEmpty(capUuid) || capType == null) {
			result.setError(-1);
			result.setMessage("调用以图搜图发生异常,请求参数为空!");
			return result;
		}
		try {
			List<Object> resultList = searchQueryService.queryResultByCmpByUuid(json);
			Object obj = searchQueryService.getObjectByUuid(capUuid, Constants.CAP_TYPE_MAP.get(capType));
			result.setError(0);
			result.getMap().put("resultList", resultList);
			result.getMap().put("model", obj);
		} catch (Exception e) {
			result.setError(-2);
			result.setMessage("调用以图搜图发生异常!");
			return result;
		}
		return result;
	}

	/**
	 * 以图搜图检索
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年9月11日 下午6:35:53
	 */
	@ResponseBody
	@RequestMapping("/queryResultByCmp")
	@SuppressWarnings("unchecked")
	public ResponseBean queryResultByCmp(@RequestBody JSONObject json) throws Exception {
		log.info("开始进行通道通行量统计，调用 search/queryResultByCmp 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数为空!");
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
			return result;
		}
		try {
			List<Object> resultList = searchQueryService.queryResultByCmp(json);
			result.setError(0);
			result.setMessage("successful");
			result.getMap().put("resultList", resultList);
		} catch (Exception e) {
			log.error("调用以图搜图发生异常，异常信息:" + e.getMessage());
			result.setError(-2);
			result.setMessage("调用以图搜图发生异常!");
			return result;
		}
		return result;
	}

	/**
	 * 语义检索
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/query")
	public ResponseBean query(@RequestBody JSONObject p) throws Exception {
		log.info("开始进行搜索，调用 search/query 接口，传递参数为: " + p);
		long l1 = System.currentTimeMillis();
		ResponseBean result = new ResponseBean();
		// 重置前台传递参数中的f值
		// 将f中传递的值为0,99的字段去掉
		JSONObject jo = (JSONObject) p.get("f");
		Set<String> keySet = jo.keySet();
		List<String> removeKey = new ArrayList<String>();
		for (String key : keySet) {
			Object object = jo.get(key);
			if (object != null) {
				String val = jo.get(key).toString();
				if (
//					"0".equals(val) || "99".equals(val) || 
				"".equals(val) || val == null
//					|| "未知".equals(val)
						|| "全部".equals(val)) {
					removeKey.add(key);
				}
			} else {
				removeKey.add(key);
			}
		}
		for (String key : removeKey) {
			jo.remove(key);
		}
		p.put("f", jo);
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		if (pager.getF().get("capType") == null) {
			log.error("类型识别错误！");
			result = ResultUtils.UNKONW_ERROR();
			return result;
		}
		String capType = pager.getF().get("capType");
		pager = searchQueryService.queryPage(pager);
		pager.getF().put("capType", capType);
		long l2 = System.currentTimeMillis();
		result = ResultUtils.success("pager", pager);
		log.info("搜索耗时-----------------" + (l2 - l1));
		return result;
	}

	/**
	 * 查询搜索详情
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/detail")
	public ResponseBean detail(@RequestBody JSONObject p) throws Exception {
		log.info("开始查询搜索详情，调用 search/detail 接口，传递参数为: " + p);
		String uuid = p.getString("uuid");
		Integer capType = Integer.parseInt(p.getString("capType"));
		ResponseBean result = new ResponseBean();
		if (StringUtils.isNotEmpty(uuid)) {
			Map<String, Object> map = searchQueryService.queryCapByUuid(uuid, capType);
			if (map == null) {
				result = ResultUtils.UNKONW_ERROR();
			} else {
				result = ResultUtils.success(map);
			}
		} else {
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		return result;
	}

	/**
	 * 对某通道通行量进行统计
	 * 
	 * @param json
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/channelTrafficCount")
	public ResponseBean channelTrafficCount(@RequestBody JSONObject json, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("开始进行通道通行量统计，调用 search/channelTrafficCount 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数为空!");
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
			return result;
		}
		String deviceId = json.getString("deviceId");
		Integer dateScope = json.getInteger("dateScope");
		if (StringUtils.isNotEmpty(deviceId)) {
			List<Map<String, Object>> list = searchQueryService.queryChannelTraffic(deviceId, dateScope);
			result = ResultUtils.success("resultList", list);
		} else {
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/channelTrafficCountActual")
	public ResponseBean channelTrafficCountActual(@RequestBody JSONObject json, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("开始进行通道通行量统计，调用 search/channelTrafficCountActual 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数为空!");
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
			return result;
		}
		String deviceId = json.getString("deviceId");
		if (StringUtils.isNotEmpty(deviceId)) {
			List<Map<String, Object>> list = searchQueryService.queryChannelTraffic(deviceId, 1);
			if (list != null && list.size() > 0) {
				result = ResultUtils.success("resultList", list.get(0));
			}
		} else {
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		return result;
	}

	/**
	 * 以图搜图查询图片特征信息，返回至前台
	 * 
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryImageFeature")
	public Object queryImageFeature(@RequestBody JSONObject json, HttpServletRequest request,
			HttpServletResponse response) {
		log.info("开始查询图片特征信息，调用 search/queryImageFeature接口");
		long l1 = System.currentTimeMillis();
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			result = ResultUtils.FAIL();
			return result;
		}
		String data = "";
		byte[] imgByte = null;
		int width = 0;
		int height = 0;
		String imgData = "";
		try {
			imgData = json.getString("imgData");
			String[] d = imgData.split("base64,");
			if (d != null && d.length == 2) {
				data = d[1];
			} else {
				throw new Exception("上传失败，数据不合法");
			}
			imgByte = Base64.decodeBase64(data);
		} catch (Exception e) {
			log.error("base64转换异常，异常信息是：" + e);
			e.printStackTrace();
		}
		if (imgByte == null || imgByte.length == 0) {
			return ResultUtils.FAIL();
		}
		ByteArrayInputStream in = new ByteArrayInputStream(imgByte); // 将imgByte作为输入流；
		try {
			ImageIO.setUseCache(false);
			BufferedImage image = ImageIO.read(in);
			if (image != null) {
				width = image.getWidth();
				height = image.getHeight();
			}
		} catch (IOException e1) {
			log.error("图片尺寸转换异常，异常信息是：" + e1);
			e1.printStackTrace();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = capService.queryCap(imgByte);
			map.put("imgData", imgData);
			map.put("width", width);
			map.put("height", height);
			result = ResultUtils.success(map);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询图片特征信息异常，异常信息是 :", e);
//			result = ResultUtils.UNKONW_ERROR();
			result.setError(-1);
			result.setMap(null);
			result.setMessage(e.getMessage());
		}
		long l2 = System.currentTimeMillis();
		log.info("查询图片特征耗时-----------" + (l2 - l1));
		return result;
	}

	/**
	 * 根据uuid获取图片特征信息并进行比对，返回比对结果
	 * 
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryImageFeatureByUuid1")
	public Object queryImageFeatureByUuid1(@RequestBody JSONObject json, HttpServletRequest request,
			HttpServletResponse response) {
		log.info("开始查询图片特征信息，调用 search/queryImageFeatureByUuid接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			result = ResultUtils.FAIL();
			return result;
		}
		String uuid = json.getString("uuid");
		String capType = json.getString("capType");
		byte[] imgByte = null;
		int width = 0;
		int height = 0;
		String imgData = "";
		String imgBase64 = "";
		if (StringUtils.isNotEmpty(uuid) && StringUtils.isNotEmpty(capType)) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map = searchQueryService.queryCapByUuid(uuid, Integer.parseInt(capType));
			} catch (NumberFormatException e1) {
				log.error("数据转换异常，异常信息是：" + e1);
				e1.printStackTrace();
			} catch (Exception e1) {
				log.error("根据uuid查询抓拍异常，异常信息是：：" + e1);
				e1.printStackTrace();
			}
			String capUrl = "";
			if (Integer.parseInt(capType) == Constants.CAP_ANALY_TYPE_PERSON) {
				Person o = (Person) map.get("model");
				capUrl = o.getCapUrl();// http://192.168.1.217:8500/image/group1/M00/00/00/wKgB2Vt-eRCISa2UAABP8P_qcD0AAAAAQAAAAAAAFAI385.jpg
			} else if (Integer.parseInt(capType) == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE) {
				MotorVehicle o = (MotorVehicle) map.get("model");
				capUrl = o.getCapUrl();
			} else if (Integer.parseInt(capType) == Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE) {
				NonmotorVehicle o = (NonmotorVehicle) map.get("model");
				capUrl = o.getCapUrl();
			}
			String suffix = "";
			if (StringUtils.isNotEmpty(capUrl)) {
				suffix = capUrl.substring(capUrl.lastIndexOf(".") + 1, capUrl.length());
			}
			String imgPrefix = "data:image/" + suffix + ";base64,";
			// 去图片服务器下载图片
			try {
				imgByte = ImgUtil.loadRawDataFromURL(capUrl);
			} catch (Exception e) {
				log.error("下载图片异常，异常信息是：" + e);
				e.printStackTrace();
			}
			ByteArrayInputStream in = new ByteArrayInputStream(imgByte); // 将b作为输入流；
			try {
				BufferedImage image = ImageIO.read(in);
				if (image != null) {
					width = image.getWidth();
					height = image.getHeight();
				}
			} catch (IOException e1) {
				log.error("图片尺寸转换异常，异常信息是：" + e1);
				e1.printStackTrace();
			}
			// 对字节数组Base64编码
			BASE64Encoder encoder = new BASE64Encoder();
			imgData = encoder.encode(imgByte);
			StringBuffer sb = new StringBuffer();
			imgBase64 = sb.append(imgPrefix).append(imgData).toString();
		} else {
			log.error("参数错误！");
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
			return result;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = capService.queryCap(imgByte);
			map.put("imgData", imgBase64);
			map.put("width", width);
			map.put("height", height);
			ResultUtils.success(map);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("查询图片特征信息异常，异常信息是 :", e);
			ResultUtils.UNKONW_ERROR();
		}

		return result;
	}

	/**
	 * 查询以图搜图结果对应的视频(暂不用)
	 * 
	 * @param p
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/queryVideo")
	public ResponseBean queryVideo(@RequestBody JSONObject p) throws Exception {
		log.info("开始查询以图搜图结果对应的视频，调用 search/queryVideo 接口，传递参数为: " + p);
		Map<String, Object> map = new HashMap<>();
		String deviceId = p.getString("deviceId");
		ResponseBean result = new ResponseBean();
		if (StringUtils.isNotEmpty(deviceId)) {
			Channel channel = channelService.findChannelById(deviceId);
			if (channel == null) {
				result = ResultUtils.UNKONW_ERROR();
			} else {
				map.put("channel", channel);
				result = ResultUtils.success(map);
			}
		} else {
			result = ResultUtils.REQUIRED_EMPTY_ERROR();
		}
		return result;
	}

}
