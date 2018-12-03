package com.sensing.core.utils.kafka;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.alarm.AlarmProcess;
import com.sensing.core.alarm.AlarmTask;
import com.sensing.core.alarm.CapBean;
import com.sensing.core.alarm.CmpProcess;
import com.sensing.core.bean.KafkaCapMsgM;
import com.sensing.core.bean.KafkaCapMsgM.pbcapturemsg;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.controller.CapDataPush;
import com.sensing.core.dao.IAlarmInfoSaveDAO;
import com.sensing.core.dao.ICmpDAO;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.utils.Constants;

/**
 * 订阅接收kafka抓拍数据
 * <p>Title: CapConsumerListener</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年11月16日
 * @version 1.0
 */
@Component
public class CapConsumerListener extends AbstractKafkaMessageListener
		implements BatchAcknowledgingMessageListener<String, byte[]> {


	@Resource
	public ICapAttrConvertService capAttrConvertService;
	@Resource
	public ICmpDAO cmpDAO;
	@Resource
	public IAlarmInfoSaveDAO alarmInfoSaveDAO;

	
	private static AtomicInteger ai = new AtomicInteger(1);
	private static Long countTimeFrom = null ;
	
	private Log log = LogFactory.getLog(CapConsumerListener.class);
	
	
	/**
	 * 接收订阅的kafka消息
	 */
	public void onMessage(List<ConsumerRecord<String, byte[]>> consumerRecords, Acknowledgment acknowledgment) {
		try {
			if (consumerRecords.size() > 0) {

				for (ConsumerRecord<String, byte[]> record : consumerRecords) {
					// 读取数据key value 反序列化
					// 引入protoBuf 进行数据反序列化，转成抓拍对象传出
					if (record.key() == null && record.value() == null) {
						continue;
					} else {
						// 订阅的kafka消息转为抓拍对象
						pbcapturemsg pbcapturemsg = KafkaCapMsgM.pbcapturemsg.parseFrom(record.value());
						log.info("接收到抓拍消息.deviceId:" + pbcapturemsg.getDeviceId() + ";capType:"
								+ pbcapturemsg.getCapType() + ";capTime:" + pbcapturemsg.getCapTime() + ";delay:"
								+ (System.currentTimeMillis() / 1000 - pbcapturemsg.getCapTime()));

						CopyOnWriteArraySet<CapDataPush> sockets = CapDataPush.capWbSockets;
						int capType = pbcapturemsg.getCapType();
						if (sockets != null && sockets.size() > 0) {
							for (CapDataPush dataPush : sockets) {
								// 判断客户端传递来的通道id是否 和接收到的通道id相等
								if ( dataPush.deviceId != null && pbcapturemsg.getDeviceId() != null
										&& dataPush.deviceId.equals(pbcapturemsg.getDeviceId()) ) {
									// 1行人，3非机动车，4机动车
									// 将接受到的kafka数据封装为对象，并翻译属性值，发送给客户端
									if (capType == Constants.CAP_ANALY_TYPE_PERSON) {
										Person capPeople = new Person();
										capPeople.setUuid(pbcapturemsg.getUuid());
										capPeople.setCoatColor(pbcapturemsg.getCoatColor());
										capPeople.setTrousersColor(pbcapturemsg.getTrousersColor());
										capPeople.setBagStyle(pbcapturemsg.getBagStyle());
										capPeople.setGlass(pbcapturemsg.getGlass());
										capPeople.setCap(pbcapturemsg.getCap());
										capPeople.setCapUrl(pbcapturemsg.getCapUrl());
										capPeople = capAttrConvertService.personConvert(capPeople);

										dataPush.sendMessage(JSONObject.toJSONString(capPeople));

									} else if (capType == Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE) {
										NonmotorVehicle nonmotor = new NonmotorVehicle();
										nonmotor.setUuid(pbcapturemsg.getUuid());
										nonmotor.setCoatColor(pbcapturemsg.getCoatColor());
										nonmotor.setGlass(pbcapturemsg.getGlass());
										nonmotor.setCap(pbcapturemsg.getCap());
										nonmotor.setCapUrl(pbcapturemsg.getCapUrl());

										nonmotor = capAttrConvertService.nonmotorVehicleConvert(nonmotor);
										dataPush.sendMessage(JSONObject.toJSONString(nonmotor));

									} else if (capType == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE) {
										MotorVehicle motor = new MotorVehicle();

										motor.setUuid(pbcapturemsg.getUuid());
										motor.setPlateNo(pbcapturemsg.getPlateNo());
										motor.setVehicleColor(pbcapturemsg.getVehicleColor());
										motor.setVehicleBrandTag(pbcapturemsg.getVehicleBrandTag());
										motor.setVehicleClass(pbcapturemsg.getVehicleClass());
										motor.setPlateColor(pbcapturemsg.getPlateColor());
										motor.setCapUrl(pbcapturemsg.getCapUrl());

										motor = capAttrConvertService.motorVehicleConvert(motor);
										dataPush.sendMessage(JSONObject.toJSONString(motor));
									}
								}
							}
						} else {
							log.info("获取到抓拍数据，但是没有客户端的链接。");
						}
						
						if (capType == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE && StringUtils.isNotEmpty(pbcapturemsg.getPlateNo()) ) {
							putData(pbcapturemsg);
						}
					}
				}
			}
			// 最后调用acknowledgment的ack方法，提交offset
			// acknowledgment.acknowledge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 往告警推送抓拍的车辆数据
	 * @param pbcapturemsg
	 * @author mingxingyu
	 * @date   2018年11月29日 上午11:01:43
	 */
	public void putData(pbcapturemsg pbcapturemsg){
		
		int k = ai.getAndIncrement();
		if ( k == Integer.MAX_VALUE ) {
			ai.set(1);
		}
		if ( countTimeFrom == null  ) {
			countTimeFrom = System.currentTimeMillis()/1000;
			for (int i = 0; i < 50; i++) {
				AlarmTask.dataSaveService.submit(new AlarmProcess(alarmInfoSaveDAO));
			}
		}
		
		CapBean capBean = new CapBean();
		
		capBean.setPlateNo(pbcapturemsg.getPlateNo());
		capBean.setPlateColor(pbcapturemsg.getPlateColor());
		capBean.setVehicleBrandTag(pbcapturemsg.getVehicleBrandTag());
		capBean.setVehicleModelTag(pbcapturemsg.getVehicleModelTag());
		capBean.setVehicleStylesTag(pbcapturemsg.getVehicleStylesTag());
		capBean.setVehicleColor(pbcapturemsg.getVehicleColor());
		capBean.setVehicleClass(pbcapturemsg.getVehicleClass());
		capBean.setDeviceUuid(pbcapturemsg.getDeviceId());
		capBean.setIdentityId(pbcapturemsg.getTargetId()+"");
		capBean.setIdentityId((k%15)+"");
		capBean.setCapTime(new Integer(pbcapturemsg.getCapTime()).longValue());
		capBean.setCapUuid(pbcapturemsg.getUuid());
		
		capBean.setCapImgUrl(pbcapturemsg.getCapUrl());
		capBean.setCapSceneUrl(pbcapturemsg.getSeceneUrl());
		
		AlarmTask.cmpService.submit(new CmpProcess(capBean,cmpDAO));
		long countTimeEnd = System.currentTimeMillis()/1000;
		if ( k % 100 == 0 && countTimeEnd != countTimeFrom) {
			log.info("已抓拍的车辆数:"+k+";每秒车辆的抓拍量:"+(k/(countTimeEnd-countTimeFrom)*1.0));
		}
		
	}
	
	
}
