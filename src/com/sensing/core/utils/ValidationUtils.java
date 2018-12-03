/**
 * <p>Title: ValidationUtils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: www.sensingtech.com</p>
 * @author mingxingyu
 * @date 2017年8月7日下午1:33:36
 * @version 1.0
 */
package com.sensing.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title: ValidationUtils</p>
 * <p>Description:校验类 </p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2017年8月7日下午1:33:36
 * @version 1.0
 */
public class ValidationUtils {
	
	private static final Log log = LogFactory.getLog(ValidationUtils.class);
	
	/**
	 * 验证是否是日期格式的字符串，日期格式为 yyyy-MM-dd
	 *
	 * @author mingxingyu
	 * @date   2017年8月7日下午1:34:47
	 */
	public static boolean checkDateType(String dateStr){
		if(!StringUtils.isNotEmpty(dateStr) || dateStr.length() != 10){
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(dateStr);
		} catch (ParseException e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 验证是否是日期时间格式的字符串，格式为 yyyy-MM-dd HH:mm:ss
	 *
	 * @author mingxingyu
	 * @date   2017年8月7日下午1:34:47
	 */
	public static boolean checkTimeType(String dateStr){
		if(!StringUtils.isNotEmpty(dateStr)){
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			sdf.parse(dateStr);
		} catch (ParseException e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 验证字符串的长度是否在某一范围
	 *
	 * @author mingxingyu
	 * @date   2017年8月7日下午1:39:11
	 */
	public static boolean checkStrLength(String str,int minLen,int maxLen){
		if(str == null){
			return false;
		}
		if( str.length() >= minLen && str.length() <= maxLen ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 验证字符串的长度是否小于等于某数
	 *
	 * @author mingxingyu
	 * @date   2017年8月7日下午1:39:11
	 */
	public static boolean checkStrLengthLess(String str,int length){
		if(str == null ){
			return false;
		}
		if( checkStrLength(str, 0, length) ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 返回某个数值是否在数组中
	 *
	 * @author mingxingyu
	 * @date   2017年8月7日下午2:04:02
	 */
	public static boolean checkValueRange(Integer num,Integer[] arr){
		List<Integer> list = new ArrayList<Integer>();
		Collections.addAll(list, arr);
		if( list.contains(num) ){
			return true;
		}
		return false;
	}
	
	/**
	 * 返回某个数值是否在某一范围内
	 * @param f 比较的数值
	 * @param minf 最小值
	 * @param maxf 最大值
	 * @return
	 * @author mingxingyu
	 * @date   2017年8月9日上午9:53:50
	 */
	public static boolean checkValueScope(float f,float minf,float maxf){
		if( f <= maxf && f >= minf){
			return true;
		}
		return false;
	}
	/**
	 * 返回某个数值是否在某一范围内
	 * @param f 比较的数值
	 * @param minf 最小值
	 * @param maxf 最大值
	 * @return
	 * @author mingxingyu
	 * @date   2017年8月9日上午9:53:50
	 */
	public static boolean checkValueScope(int f,int minf,int maxf){
		if( f <= maxf && f >= minf){
			return true;
		}
		return false;
	}
	
	/**
	 * 验证字符串的长度是否在某一范围
	 *
	 * @author dongsl
	 * @date   2017年8月7日下午1:39:11
	 */
	public static boolean checkStrLengthWithNull(String str,int minLen,int maxLen){
		if(str==null && minLen<=0){
			return true;
		}else if(str==null && minLen >0){
			return false;
		}else{
			if( str.length() >= minLen && str.length() <= maxLen ){
				return true;
			}else{
				return false;
			}
		}
		
	}

	/**
	 * 验证字符串中是否只有汉字字母和数字
	 * @param str
	 * @return 没有其他字符：true；有其他字符：false
	 * @author mingxingyu
	 * @date   2017年9月1日下午2:59:43
	 */
	public static boolean checkHanziLetterNum(String str){
		if( !StringUtils.isNotEmpty(str) ){
			return false;
		}
		String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(str);
		return match.matches();
	}
	
	/**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
    /**
     * 正则匹配
     * @param pattern
     * @param content
     * @return
     */
    public static boolean checkStrByPattern(String pattern,String content) {
    	boolean isMatch = Pattern.matches(pattern, content);
    	return isMatch;
    }
}
