package com.sensing.core.service;

import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.utils.Pager;

/**
 *@author wenbo
 */
public interface IMotorVehicleService {

	public abstract MotorVehicle saveNewMotorVehicle(MotorVehicle MotorVehicle)  throws Exception;

	public MotorVehicle updateMotorVehicle(MotorVehicle MotorVehicle)  throws Exception;

	public abstract MotorVehicle findMotorVehicleById(java.lang.String uuid) throws Exception;

	public abstract void removeMotorVehicle(java.lang.String uuid) throws Exception;

	public Pager queryPage(Pager pager) throws Exception;

}