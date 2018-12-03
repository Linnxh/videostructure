package com.sensing.core.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.service.ISearchQueryService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.results.ResultUtils;

@Controller
@RequestMapping("/overview")
public class DataOverviewController extends BaseController {

	private static final Log log = LogFactory.getLog(DataOverviewController.class);
	@Resource
	private ISearchQueryService searchQueryService;

	/**
	 * 通行量统计
	 * 
	 * @param json
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/trafficCount")
	public ResponseBean trafficCount(@RequestBody JSONObject json) {
		log.info("开始进行通道通行量统计，调用 overview/trafficCount 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			result = ResultUtils.FAIL();
			return result;
		}
		String ids = json.getString("channelUuids");
		String startTime = json.getString("startTime");
		String endTime = json.getString("endTime");
		try {
			Map<String, Object> map = searchQueryService.trafficCount(ids, startTime, endTime);
			map.put("channelUuids", ids);
			map.put("startTime", startTime);
			map.put("endTime", endTime);
			result = ResultUtils.success(map);
		} catch (Exception e) {
			e.printStackTrace();
			result = ResultUtils.FAIL();
		}
		return result;

	}

}
