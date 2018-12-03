package com.sensing.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.dao.IMotorVehicleDAO;
import com.sensing.core.service.IMotorVehicleService;
import com.sensing.core.utils.BussinessException;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.UuidUtil;

/**
 *@author wenbo
 */
@Service
public class MotorVehicleServiceImpl implements IMotorVehicleService{


	private static final Log log = LogFactory.getLog(IMotorVehicleService.class);

	@Resource
	public IMotorVehicleDAO motorVehicleDAO;

	public MotorVehicleServiceImpl() {
		super();
	}

	@Override
	public MotorVehicle saveNewMotorVehicle(MotorVehicle MotorVehicle)  throws Exception{
		try {
			String uuid = UuidUtil.getUuid();
			MotorVehicle.setUuid(uuid);
			motorVehicleDAO.saveMotorVehicle(MotorVehicle);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return MotorVehicle;
	}

	@Override
	public MotorVehicle updateMotorVehicle(MotorVehicle MotorVehicle)  throws Exception{
		motorVehicleDAO.updateMotorVehicle(MotorVehicle);
		return MotorVehicle;
	}

	@Override
	public MotorVehicle findMotorVehicleById(java.lang.String uuid) throws Exception{
		try {
			return motorVehicleDAO.getMotorVehicle(uuid);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	@Override
	public void removeMotorVehicle(String uuid) throws Exception{
		try {
		motorVehicleDAO.removeMotorVehicle(uuid);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	@Override
	public Pager queryPage(Pager pager) throws Exception{
		try {
			List<MotorVehicle> list = motorVehicleDAO.queryList(pager);
			int totalCount = motorVehicleDAO.selectCount(pager);
			pager.setTotalCount(totalCount);
			pager.setResultList(list);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return pager;
	}

}