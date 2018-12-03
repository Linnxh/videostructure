package com.sensing.core.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.bean.TemplateObjMotor;
import com.sensing.core.dao.ICapAttrConvertDAO;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.IMotorVehicleService;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.props.RemoteInfoUtil;

/**
 * 抓拍属性值的转换
 * <p>
 * Title: CapAttrConvertServiceImpl
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: www.sensingtech.com.cn
 * </p>
 * 
 * @author mingxingyu
 * @date 2018年8月14日
 * @version 1.0
 */
@Service
public class CapAttrConvertServiceImpl implements ICapAttrConvertService {

	private static final Log log = LogFactory.getLog(CapAttrConvertServiceImpl.class);

	@Resource
	public ICapAttrConvertDAO capAttrConvertDAO;

	/**
	 * 将抓拍人对象的属性转换为可知的属性值
	 * 
	 * @param capPeople
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年8月11日 下午5:02:32
	 */
	public Person personConvert(Person person) throws Exception {
//		if (person == null || person.getUuid() == null) {
		if (person == null) {
			log.error("personConvert属性转换为传入的对象为空");
			return null;
		}
		try {
			long l1 = System.currentTimeMillis();
			Map<String, String> attrMap = capAttrConvertDAO.personConvert(person);
			BeanUtils.copyProperties(person, attrMap);
			// 文件路径添加地址
			if (StringUtils.isNotEmpty(person.getCapUrl())) {
				person.setCapUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + person.getCapUrl());
			}
			if (StringUtils.isNotEmpty(person.getVideoUrl())) {
				person.setVideoUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + person.getVideoUrl());
			}
			if (StringUtils.isNotEmpty(person.getSeceneUrl())) {
				person.setSeceneUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + person.getSeceneUrl());
			}
			person.setCapType(Constants.CAP_ANALY_TYPE_PERSON);
			long l2 = System.currentTimeMillis();
			log.info("方法personConvert抓拍人员属性翻译耗时:" + (l2 - l1));
			return person;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("方法capPeopleConvert抓拍人员属性翻译时发生异常，" + e.getMessage());
		}
		return person;
	}

	/**
	 * 将抓拍非机动车对象的属性转换为可知的属性值
	 * 
	 * @param nonmotorVehicle
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年8月11日 下午5:02:32
	 */
	public NonmotorVehicle nonmotorVehicleConvert(NonmotorVehicle nonmotorVehicle) throws Exception {
//		if (capNonmotor == null || capNonmotor.getUuid() == null) {
		if (nonmotorVehicle == null) {
			log.error("capNonmotorConvert属性转换为传入的对象为空");
			return null;
		}
		try {
			long l1 = System.currentTimeMillis();
			Map<String, String> attrMap = capAttrConvertDAO.nonmotorVehicleConvert(nonmotorVehicle);
			BeanUtils.copyProperties(nonmotorVehicle, attrMap);
			// 文件路径添加地址
			if (StringUtils.isNotEmpty(nonmotorVehicle.getCapUrl())) {
				nonmotorVehicle.setCapUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + nonmotorVehicle.getCapUrl());
			}
			if (StringUtils.isNotEmpty(nonmotorVehicle.getVideoUrl())) {
				nonmotorVehicle.setVideoUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + nonmotorVehicle.getVideoUrl());
			}
			if (StringUtils.isNotEmpty(nonmotorVehicle.getSeceneUrl())) {
				nonmotorVehicle.setSeceneUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + nonmotorVehicle.getSeceneUrl());
			}
			nonmotorVehicle.setCapType(Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE);
			long l2 = System.currentTimeMillis();
			log.info("方法nonmotorVehicleConvert抓拍非机动车属性翻译耗时:" + (l2 - l1));
			return nonmotorVehicle;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("方法nonmotorVehicleConvert抓拍非机动车属性翻译时发生异常，" + e.getMessage());
		}
		return nonmotorVehicle;
	}

	/**
	 * 将抓拍机动车对象的属性转换为可知的属性值
	 * 
	 * @param capPeople
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年8月11日 下午5:02:32
	 */
	public MotorVehicle motorVehicleConvert(MotorVehicle motorVehicle) throws Exception {
//		if (motorVehicle == null || motorVehicle.getUuid() == null) {
		if (motorVehicle == null) {
			log.error("capMotorConvert属性转换为传入的对象为空");
			return null;
		}
		try {
			long l1 = System.currentTimeMillis();
			Map<String, String> attrMap = capAttrConvertDAO.motorVehicleConvert(motorVehicle);
			BeanUtils.copyProperties(motorVehicle, attrMap);
			// 文件路径添加地址
			if (StringUtils.isNotEmpty(motorVehicle.getCapUrl())) {
				motorVehicle.setCapUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + motorVehicle.getCapUrl());
			}
			if (StringUtils.isNotEmpty(motorVehicle.getVideoUrl())) {
				motorVehicle.setVideoUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + motorVehicle.getVideoUrl());
			}
			if (StringUtils.isNotEmpty(motorVehicle.getSeceneUrl())) {
				motorVehicle.setSeceneUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + motorVehicle.getSeceneUrl());
			}
			motorVehicle.setCapType(Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE);
			long l2 = System.currentTimeMillis();
			log.info("方法motorVehicleConvert抓拍机动车属性翻译耗时:" + (l2 - l1));
			return motorVehicle;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("方法motorVehicleConvert抓拍机动车属性翻译时发生异常，" + e.getMessage());
		}
		return motorVehicle;
	}

	@Override
	public TemplateObjMotor templateObjMotorConvert(TemplateObjMotor templateObjMotor) throws Exception {
		if (templateObjMotor == null) {
			log.error("templateObjMotorConvert属性转换为传入的对象为空");
			return null;
		}
		try {
			long l1 = System.currentTimeMillis();
			Map<String, String> attrMap = capAttrConvertDAO.templateObjMotorConvert(templateObjMotor);
			BeanUtils.copyProperties(templateObjMotor, attrMap);
			// 文件路径添加地址
			if (StringUtils.isNotEmpty(templateObjMotor.getMainTemplateUrl())) {
				templateObjMotor.setMainTemplateUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + templateObjMotor.getMainTemplateUrl());
			}
			if (templateObjMotor.getOwnerContactinfo() != null) {
				if (templateObjMotor.getOwnerContactinfo() == 0) {
					templateObjMotor.setOwnerContactinfoTag("无");
				}
				if (templateObjMotor.getOwnerContactinfo() == 1) {
					templateObjMotor.setOwnerContactinfoTag("查看");
				}
			}
			long l2 = System.currentTimeMillis();
			log.info("方法templateObjMotorConvert机动车属性翻译耗时:" + (l2 - l1));
			return templateObjMotor;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("方法templateObjMotorConvert机动车属性翻译时发生异常，" + e.getMessage());
		}
		return templateObjMotor;
	}
}