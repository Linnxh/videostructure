package com.sensing.core.resp;

import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.bean.Channel;

public class CapResp {

	private Person capPeople;
	private MotorVehicle motorVehicle;
	private NonmotorVehicle capNonmotor;
	private Channel channel;

	public Person getCapPeople() {
		return capPeople;
	}

	public void setCapPeople(Person capPeople) {
		this.capPeople = capPeople;
	}

	public NonmotorVehicle getCapNonmotor() {
		return capNonmotor;
	}

	public void setCapNonmotor(NonmotorVehicle capNonmotor) {
		this.capNonmotor = capNonmotor;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public MotorVehicle getMotorVehicle() {
		return motorVehicle;
	}

	public void setMotorVehicle(MotorVehicle motorVehicle) {
		this.motorVehicle = motorVehicle;
	}
}
