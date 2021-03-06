package com.sensing.core.dao;

import java.util.List;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.utils.Pager;

/**
 * @author wenbo
 */
public interface IMotorVehicleDAO {
	public int saveMotorVehicle(MotorVehicle MotorVehicle) throws Exception;

	public MotorVehicle getMotorVehicle(java.lang.String uuid) throws Exception;

	public int removeMotorVehicle(java.lang.String uuid) throws Exception;

	public int updateMotorVehicle(MotorVehicle MotorVehicle) throws Exception;

	public List<MotorVehicle> queryList(Pager pager) throws Exception;

	public int selectCount(Pager pager) throws Exception;

}
