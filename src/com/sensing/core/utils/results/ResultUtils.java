package com.sensing.core.utils.results;

import java.util.Map;

import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;

/**
 * http请求返回success以及error方法封装
 */
@SuppressWarnings("all")
public class ResultUtils {
	private static final String SUCCESS_MSG = "successful";

	/**
	 * http回调成功
	 * 
	 * @param object
	 * @return
	 */
	public static ResponseBean success() {
		ResponseBean result = new ResponseBean();
		result.setError(0);
		result.setMessage(SUCCESS_MSG);
		return result;
	}
	/**
	 * http回调成功
	 * 
	 * @param object
	 * @return
	 */
	public static ResponseBean success(Map map) {
		ResponseBean result = new ResponseBean();
		result.setError(0);
		result.setMessage(SUCCESS_MSG);
		result.setMap(map);
		return result;
	}

	/**
	 * http回调成功
	 * 
	 * @param object
	 * @return
	 */
	public static ResponseBean success(String key, Object o) {
		ResponseBean result = new ResponseBean();
		result.setError(0);
		result.setMessage(SUCCESS_MSG);
		result.getMap().put(key, o);
		return result;
	}

	/**
	 * http回调成功
	 * 
	 * @param object
	 * @return
	 */
	public static ResponseBean success(ResponseBean result, String key, Object o) {
		result.setError(0);
		result.setMessage(SUCCESS_MSG);
		if (StringUtils.isNotEmpty(key)) {
			result.getMap().put(key, o);
		}
		return result;
	}

	/**
	 * http回调错误
	 * 
	 * @param code
	 * @param msg
	 * @return
	 */
	public static ResponseBean error(Integer code, String msg) {
		ResponseBean result = new ResponseBean();
		result.setError(code);
		result.setMessage(msg);
		return result;
	}

	/**
	 * http回调错误
	 * 
	 * @param code
	 * @param msg
	 * @return
	 */
	public static ResponseBean error(ResponseBean result, Integer code, String msg) {
		result.setError(code);
		result.setMessage(msg);
		return result;
	}

	public static ResponseBean FAIL() {
		ResponseBean result = new ResponseBean(ResultEnum.FAIL.getCode(), ResultEnum.FAIL.getMsg());
		return result;
	}

	public static ResponseBean UNKONW_ERROR() {
		ResponseBean result = new ResponseBean(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		return result;
	}

	public static ResponseBean REQUIRED_EMPTY_ERROR() {
		ResponseBean result = new ResponseBean(ResultEnum.REQUIRED_EMPTY_ERROR.getCode(),
				ResultEnum.REQUIRED_EMPTY_ERROR.getMsg());
		return result;
	}

	public static ResponseBean UNIQUE_VIOLATION() {
		ResponseBean result = new ResponseBean(ResultEnum.UNIQUE_VIOLATION.getCode(),
				ResultEnum.UNIQUE_VIOLATION.getMsg());
		return result;
	}

	public static ResponseBean DATA_ERROR() {
		ResponseBean result = new ResponseBean(ResultEnum.DATA_ERROR.getCode(), ResultEnum.DATA_ERROR.getMsg());
		return result;
	}

	public static ResponseBean BEYOND_CHAR_LIMIT() {
		ResponseBean result = new ResponseBean(ResultEnum.BEYOND_CHAR_LIMIT.getCode(),
				ResultEnum.BEYOND_CHAR_LIMIT.getMsg());
		return result;
	}

}
