package com.sensing.core.alarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sensing.core.dao.ICmpDAO;

/**
 * 
 * <p>Title: CmpProcess</p>
 * <p>Description:比对的流程</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年9月14日
 * @version 1.0
 */
public class CmpProcess implements Runnable{
	
	public ICmpDAO cmpDAO;

	private CapBean capBean;
	
	//日志记录
	private Log log = LogFactory.getLog(CmpProcess.class);
	
	
	public CmpProcess(){}
	public CmpProcess(CapBean capBean,ICmpDAO cmpDAO){this.capBean = capBean;this.cmpDAO=cmpDAO;}
	
	//主流程
	public void run() {
		try {
			//库检索
			List<CmpResultBean> cmpResultList = capToCmp(capBean);
			if ( cmpResultList != null && cmpResultList.size() > 0  ) {
				for (int i = 0; i < cmpResultList.size() ; i++) {
					CmpResultBean cmpResultBean = cmpResultList.get(i);

					cmpResultBean.setCapImgUrl(capBean.getCapImgUrl());
					cmpResultBean.setCapTime(capBean.getCapTime());
					cmpResultBean.setCapUuid(capBean.getCapUuid());
					cmpResultBean.setDeviceUuid(capBean.getDeviceUuid());
					cmpResultBean.setIdentityId(capBean.getIdentityId());
					cmpResultBean.setSceneUrl(capBean.getCapSceneUrl());
					
					AlarmTask.cmpResultQuene.put(cmpResultBean);
//					log.info("告警检索结果为:"+cmpResultList.toString());
				}
			}
		} catch (Exception e) {
			log.error("后台告警流程异常:"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public List<CmpResultBean> capToCmp(CapBean capBean){
		try {
			if ( AlarmCache.templateDbList == null || AlarmCache.templateDbList.size() == 0 ) {
				return null;
			}
			Map<String,Object> params = new HashMap<String,Object>();
			
			params.put("plateNo",capBean.getPlateNo());
			params.put("plateColor",capBean.getPlateColor()==null?null:capBean.getPlateColor());
			params.put("vehicleBrandTag",capBean.getVehicleBrandTag());
			params.put("vehicleModelTag",capBean.getVehicleModelTag());
			params.put("vehicleStylesTag",capBean.getVehicleStylesTag());
			params.put("vehicleColor",capBean.getVehicleColor()==null?null:capBean.getVehicleColor());
			params.put("vehicleClass ",capBean.getVehicleClass()==null?null:capBean.getVehicleClass());
			
			long l1 = System.currentTimeMillis();
			List<CmpResultBean> cmpResultList = cmpDAO.cmpQuery(params,AlarmCache.templateDbList);
			log.info("比对信息:"+params.toString());
			long l2 = System.currentTimeMillis();
			log.info("检索耗时:(cmpDAO)"+(l2-l1));
			return cmpResultList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}
	
}
