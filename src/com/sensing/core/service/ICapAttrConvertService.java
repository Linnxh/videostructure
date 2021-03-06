package com.sensing.core.service;

import com.sensing.core.bean.Person;
import com.sensing.core.bean.TemplateObjMotor;
import com.sensing.core.bean.NonmotorVehicle;

import java.util.Map;

import com.sensing.core.bean.MotorVehicle;

/**
 * 抓拍对象的属性转换
 * <p>
 * Title: ICapAttrConvertService
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: www.sensingtech.com
 * </p>
 * 
 * @author mingxingyu
 * @date 2018年8月11日
 * @version 1.0
 */
public interface ICapAttrConvertService {

	/**
	 * 将抓拍对象的属性转换为可知的属性值
	 * 
	 * @param person
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年8月11日 下午5:02:32
	 */
	public Person personConvert(Person person) throws Exception;

	/**
	 * 将抓拍非机动车对象的属性转换为可知的属性值
	 * 
	 * @param nonmotorVehicle
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年8月11日 下午5:02:32
	 */
	public NonmotorVehicle nonmotorVehicleConvert(NonmotorVehicle nonmotorVehicle) throws Exception;

	/**
	 * 将抓拍机动车对象的属性转换为可知的属性值
	 * 
	 * @param motorVehicle
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年8月11日 下午5:02:32
	 */
	public MotorVehicle motorVehicleConvert(MotorVehicle motorVehicle) throws Exception;

	public TemplateObjMotor templateObjMotorConvert(TemplateObjMotor templateObjMotor) throws Exception;

}
