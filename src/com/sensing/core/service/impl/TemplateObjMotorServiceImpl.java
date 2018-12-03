package com.sensing.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.ImageFile;
import com.sensing.core.bean.Template;
import com.sensing.core.bean.TemplateDb;
import com.sensing.core.bean.TemplateObjMotor;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.UuidUtil;
import com.sensing.core.utils.httputils.HttpDeal;
import com.sensing.core.utils.props.RemoteInfoUtil;
import com.sensing.core.utils.results.ResultUtils;

import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.ITemplateDbService;
import com.sensing.core.service.ITemplateObjMotorService;
import com.sensing.core.service.ITemplateService;
import com.sensing.core.dao.ITemplateDAO;
import com.sensing.core.dao.ITemplateDbDAO;
import com.sensing.core.dao.ITemplateObjMotorDAO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mingxingyu
 */
@Service
@Transactional(rollbackFor = { Exception.class })
public class TemplateObjMotorServiceImpl implements ITemplateObjMotorService {

	private static final Log log = LogFactory.getLog(ITemplateObjMotorService.class);

	@Resource
	public ITemplateObjMotorDAO templateObjMotorDAO;
	@Resource
	public ITemplateDbDAO templateDbDAO;
	@Resource
	public ITemplateDAO templateDAO;
	@Resource
	public ICapAttrConvertService capAttrConvertService;
	@Resource
	public ITemplateDbService templateDbService;
	@Resource
	public ITemplateService templateService;

	public TemplateObjMotorServiceImpl() {
		super();
	}

	@Override
	public ResponseBean saveNewTemplateObjMotor(JSONObject m, TemplateObjMotor templateObjMotor, ResponseBean result)
			throws Exception {
		try {
			String id = UuidUtil.getUuid();
			templateObjMotor.setUuid(id);
			templateObjMotor.setCreateTime(new Date().getTime() / 1000);
			templateObjMotor.setIsDeleted((short) 0);
			templateObjMotorDAO.saveTemplateObjMotor(templateObjMotor);
			// 对应目标库添加数量
			TemplateDb db = templateDbDAO.getTemplateDb(templateObjMotor.getTemplatedbId(), 0);
			Integer size = 0;
			if (db.getTemplateDbSize() != null) {
				size = db.getTemplateDbSize() + 1;
			} else {
				size = 1;
			}
			db.setTemplateDbSize(size);
			db.setModifyTime(new Date().getTime() / 1000);
			templateDbService.updateTemplateDb(db);
			// 保存图片
			if (m != null) {
				String motorListStr = m.getString("motorList");
				if (StringUtils.isNotEmpty(motorListStr)) {
					JSONArray motorList = JSONArray.parseArray(motorListStr);
					List<Template> templateList = JSONObject.parseArray(motorList.toJSONString(), Template.class);
					for (Template template : templateList) {
						String data = "";
						String postfix = "";
						byte[] imgByte = null;
						String image = template.getImageData();
						String[] d = image.split(";base64,");
						if (d != null && d.length == 2) {
							postfix = "." + d[0].split(":")[1].split("/")[1];
							data = d[1];
						} else {
							throw new RuntimeException("数据格式错误！");
						}
						imgByte = Base64.decodeBase64(data.getBytes("UTF-8"));
						if (imgByte == null || imgByte.length == 0) {
							throw new RuntimeException("图片错误！");
						}
						String uuid = UuidUtil.getUuid();
						// 保存图片到服务器
						String seceneURI = uuid + postfix;
						String secenePut = HttpDeal.doPut(seceneURI, imgByte);
						ImageFile seceneImageFile = JSONObject.toJavaObject(JSONObject.parseObject(secenePut),
								ImageFile.class);
						String imgUrl = "";
						if (seceneImageFile.getError() >= 0) {
							imgUrl = seceneImageFile.getMessage();
							Template t = new Template();
							t.setUuid(uuid);
							t.setCreateTime(new Date().getTime() / 1000);
							t.setIsDeleted((short) 0);
							t.setObjUuid(templateObjMotor.getUuid());
							t.setTemplatedbId(templateObjMotor.getTemplatedbId());
							t.setIndex(template.getIndex());
							t.setImageUrl(imgUrl);
							templateService.saveNewTemplate(t);
						} else {
							throw new RuntimeException("未获取到图片的地址！");
						}
						// 保存主图片的uuid
						if (templateObjMotor.getMainTemplateIndex() != null) {
							int index = templateObjMotor.getMainTemplateIndex();
							if (template.getIndex() == index) {
								templateObjMotor.setMainTemplateUuid(uuid);
								templateObjMotor.setMainTemplateUrl(imgUrl);
								updateTemplateObjMotor(templateObjMotor, result);
							}
						}
					}
				}
				result = ResultUtils.success(result, "model", templateObjMotor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用saveNewTemplateObjMotor方法保存车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚                                                                                   
			result = ResultUtils.error(result, 100, "添加车辆信息失败！");
		}
		return result;
	}

	@Override
	public ResponseBean updateTemplateObjMotor(TemplateObjMotor templateObjMotor, ResponseBean result)
			throws Exception {
		try {
			TemplateObjMotor motor = templateObjMotorDAO.getTemplateObjMotor(templateObjMotor.getUuid());
			if (templateObjMotor.getIsDeleted() == null) {
				templateObjMotor.setIsDeleted(motor.getIsDeleted());
			}
			if (templateObjMotor.getMemo() == null) {
				templateObjMotor.setMemo(motor.getMemo());
			}
			if (templateObjMotor.getOwnerAddr() == null) {
				templateObjMotor.setOwnerAddr(motor.getOwnerAddr());
			}
			if (templateObjMotor.getOwnerContactinfo() == null) {
				templateObjMotor.setOwnerContactinfo(motor.getOwnerContactinfo());
			}
			if (templateObjMotor.getOwnerIdno() == null) {
				templateObjMotor.setOwnerIdno(motor.getOwnerIdno());
			}
			if (templateObjMotor.getOwnerName() == null) {
				templateObjMotor.setOwnerName(motor.getOwnerName());
			}
			if (templateObjMotor.getOwnerTel() == null) {
				templateObjMotor.setOwnerTel(motor.getOwnerTel());
			}
			if (templateObjMotor.getPlateColor() == null) {
				templateObjMotor.setPlateColor(motor.getPlateColor());
			}
			if (templateObjMotor.getPlateNo() == null) {
				templateObjMotor.setPlateNo(motor.getPlateNo());
			}
			if (templateObjMotor.getTemplatedbId() == null) {
				templateObjMotor.setTemplatedbId(motor.getTemplatedbId());
			}
			if (templateObjMotor.getVehicleBrandTag() == null) {
				templateObjMotor.setVehicleBrandTag(motor.getVehicleBrandTag());
			}
			if (templateObjMotor.getVehicleClass() == null) {
				templateObjMotor.setVehicleClass(motor.getVehicleClass());
			}
			if (templateObjMotor.getVehicleColor() == null) {
				templateObjMotor.setVehicleColor(motor.getVehicleColor());
			}
			if (templateObjMotor.getVehicleModelTag() == null) {
				templateObjMotor.setVehicleColor(motor.getVehicleColor());
			}
			if (templateObjMotor.getVehicleStylesTag() == null) {
				templateObjMotor.setVehicleStylesTag(motor.getVehicleStylesTag());
			}
			templateObjMotor.setModifyTime(new Date().getTime() / 1000);
			templateObjMotorDAO.updateTemplateObjMotor(templateObjMotor);
			// 逻辑删除
			if (templateObjMotor.getIsDeleted() != null && templateObjMotor.getIsDeleted() == 1) {
				// 更新图片信息
				List<Template> list = templateDAO.getTemplateByObjUuid(templateObjMotor.getUuid());
				for (Template template : list) {
					template.setModifyTime(new Date().getTime() / 1000);
					template.setIsDeleted((short) 1);
					templateDAO.updateTemplate(template);
				}
				// 更新目标库数量
				TemplateDb templateDb = templateDbDAO.getTemplateDb(templateObjMotor.getTemplatedbId(), 0);
				templateDb.setTemplateDbSize(templateDb.getTemplateDbSize() - 1);
				templateDbDAO.updateTemplateDb(templateDb);
			}
			result = ResultUtils.success(result, "model", templateObjMotor);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用updateTemplateObjMotor方法更新车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚 
			result = ResultUtils.error(result, 100, "更新车辆信息失败！");
		}
		return result;
	}

	@Override
	public TemplateObjMotor findTemplateObjMotorById(java.lang.String uuid) throws Exception {
		return templateObjMotorDAO.getTemplateObjMotor(uuid);
	}

	@Override
	public void removeTemplateObjMotor(String uuid) throws Exception {
		templateObjMotorDAO.removeTemplateObjMotor(uuid);
	}

	@Override
	public Pager queryPage(Pager pager) throws Exception {
		pager.getF().put("isDeleted", "0");
		List<TemplateObjMotor> list = templateObjMotorDAO.queryList(pager);
		for (TemplateObjMotor templateObjMotor : list) {
			templateObjMotor = capAttrConvertService.templateObjMotorConvert(templateObjMotor);
			List<Template> templateList = templateDAO.getTemplateByObjUuid(templateObjMotor.getUuid());
			if (templateList != null && templateList.size() > 0) {
				for (Template template : templateList) {
					template.setImageUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + template.getImageUrl());
					if (template.getUuid().equals(templateObjMotor.getMainTemplateUuid())) {
						templateObjMotor.setMainTemplateUrl(template.getImageUrl());
					}
				}
				templateObjMotor.setTemplateList(templateList);
			}
		}
		int totalCount = templateObjMotorDAO.selectCount(pager);
		pager.setTotalCount(totalCount);
		pager.setResultList(list);
		return pager;
	}

	@Override
	public void logicalDeleted(Integer templateDbId) throws Exception {
		templateObjMotorDAO.logicalDeleted(templateDbId);
	}

	@Override
	public List<TemplateObjMotor> queryByTemplateDbId(Integer templateDbId) throws Exception {
		List<TemplateObjMotor> list = templateObjMotorDAO.queryByTemplateDbId(templateDbId);
		return list;
	}

	@Override
	public void saveObjMotorInSimpleDB(String plateNo) {
		try {
			TemplateObjMotor templateObjMotor = new TemplateObjMotor();
			String id = UuidUtil.getUuid();
			templateObjMotor.setUuid(id);
			templateObjMotor.setCreateTime(new Date().getTime() / 1000);
			templateObjMotor.setIsDeleted((short) 0);
			templateObjMotor.setPlateNo(plateNo);
			templateObjMotor.setTemplatedbId(1);// 单目标库
			templateObjMotorDAO.saveTemplateObjMotor(templateObjMotor);

			// 对应目标库添加数量
			TemplateDb db = templateDbDAO.getTemplateDb(1, 0);
			Integer size = 0;
			if (db.getTemplateDbSize() != null) {
				size = db.getTemplateDbSize() + 1;
			} else {
				size = 1;
			}
			db.setTemplateDbSize(size);
			db.setModifyTime(new Date().getTime() / 1000);
			templateDbService.updateTemplateDb(db);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用saveObjMotorInSimpleDB方法保存车辆信息失败，失败信息为：" + e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 加上本句如果方法2失败方法1会回滚 
		}

	}

}