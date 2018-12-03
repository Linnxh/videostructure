package com.sensing.core.alarm;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.KafkaCapMsgM;
import com.sensing.core.bean.KafkaCapMsgM.pbcapturemsg;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.kafka.AbstractKafkaMessageListener;

/**
 * 从kafka订阅抓拍数据
 * <p>Title: CapConsumerListener</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年9月14日
 * @version 1.0
 */
@Component
public class CapAlarmConsumer extends AbstractKafkaMessageListener implements BatchAcknowledgingMessageListener<String,byte[]> {

	private Log log = LogFactory.getLog(CapAlarmConsumer.class);
	
	/**
	 * 接收订阅的kafka消息
	 */
    public void onMessage(List<ConsumerRecord<String, byte[]>> consumerRecords, Acknowledgment acknowledgment) {
        try {
            if(consumerRecords.size()>0){

                for(ConsumerRecord<String, byte[]> record : consumerRecords){
                    //读取数据key value 反序列化
                    //引入protoBuf 进行数据反序列化，转成抓拍对象传出
                    if(record.key()==null && record.value()== null){
                        continue;
                    }else{
                    	//订阅的kafka消息转为抓拍对象
                    	pbcapturemsg pbcapturemsg = KafkaCapMsgM.pbcapturemsg.parseFrom(record.value());
                    	
                    	
                    	int capType = pbcapturemsg.getCapType();
                    	if ( capType == Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE ) {
                    		MotorVehicle motor = new MotorVehicle();
							
                    		CapBean capBean = new CapBean();
                    		capBean.setPlateNo(pbcapturemsg.getPlateNo());
                    		capBean.setPlateColor(pbcapturemsg.getPlateColor());
                    		capBean.setVehicleBrandTag(pbcapturemsg.getVehicleBrandTag());
                    		capBean.setVehicleModelTag(pbcapturemsg.getVehicleModelTag());
                    		capBean.setVehicleStylesTag(pbcapturemsg.getVehicleStylesTag());
                    		capBean.setVehicleColor(pbcapturemsg.getVehicleColor());
                    		capBean.setVehicleClass(pbcapturemsg.getVehicleClass());
//                    		AlarmTask.capLinkedQuene.put(capBean);
                    		//AlarmTask.cmpService.submit(new CmpProcess(capBean));
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
}
