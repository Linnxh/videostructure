//package com.sensing.core.alarm;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.stereotype.Component;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.sensing.core.dao.IAlarmInfoSaveDAO;
//import com.sensing.core.dao.ICmpDAO;
//import com.sensing.core.dao.IDataInitDAO;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {
//		"classpath*:/applicationContext.xml",
//		"classpath*:/spring-mybatis.xml"
//		})
//public class StartUp {
//
//	AtomicInteger i = new AtomicInteger(1) ;
//
//	@Resource
//	public ICmpDAO cmpDAO;
//	@Resource
//	public IAlarmInfoSaveDAO alarmInfoSaveDAO;
//	@Resource
//	public IDataInitDAO dataInitDAO;
//
//	 @Test
//	public void Testafsd() throws Exception{
//		List<Map<String,Object>> list = dataInitDAO.queryTemplateDbList();
//		if ( list != null && list.size() > 0 ) {
//			List<Integer> temp = new ArrayList<Integer>();
//
//			for (int i = 0; i < list.size() ; i++) {
//				Map<String, Object> param = list.get(i);
//				temp.add(Integer.parseInt(param.get("id").toString()));
//			}
//			AlarmCache.templateDbList = temp;
//		}
//		System.out.println(AlarmCache.templateDbList.toString());
//	}
//
//	@PostConstruct
//	public void process(){
//
//		final String[] plateNos = {"浙A1L612","京C123DV","豫C7451D","川H65231","川H65232","川H65233","川H65234","川H65235","川H65230","豫A1L61x"};
//		final Integer[] plateColors = {1,2,3,4,5};
//		final String[] vehicleBrandTags = {"大众","宝马","别克","日产","红旗","凯瑞"};
//		final Integer[] vehicleColors = {1,2,3,4,5};
//		final Integer[] vehicleClasses = {1,2,3,4,5,6,7,8,9,10};
//		final String[] identityIds = {"ids1","ids2","ids3","ids4","ids5","ids6","ids7","ids8","ids9","ids10","ids11"};
//
//		/*
//		for (int i = 0; i < 50; i++) {
//			AlarmTask.dataSaveService.submit(new AlarmProcess(alarmInfoSaveDAO));
//		}
//
//
//		long startTime = System.currentTimeMillis()/1000;
//		for (int j = 0; j < 1; j++) {
//			ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
//			service.scheduleAtFixedRate(new Runnable() {
//				public void run() {
//					int k = i.getAndIncrement();
//					if ( k == Integer.MAX_VALUE ) {
//						i.set(1);
//					}
//					CapBean capBean = new CapBean();
//					capBean.setPlateNo(plateNos[k%plateNos.length]);
//					capBean.setPlateColor(plateColors[k%plateColors.length]);
//					capBean.setVehicleBrandTag(vehicleBrandTags[k%vehicleBrandTags.length]);
//					capBean.setVehicleColor(vehicleColors[k%vehicleColors.length]);
//					capBean.setVehicleClass(vehicleClasses[k%vehicleClasses.length]);
//					capBean.setDeviceUuid("deviceUuid5232125487");
//					capBean.setIdentityId(identityIds[k%identityIds.length]);
//					capBean.setCapTime(System.currentTimeMillis()/1000);
//					capBean.setCapUuid(UuidUtil.getUuid());
//
//					capBean.setCapImgUrl("capImgUrl...");
//					capBean.setCapSceneUrl("capSceneUrl...");
//
//					//AlarmTask.cmpService.submit(new CmpProcess(capBean,cmpDAO));
//					long endTime = System.currentTimeMillis()/1000;
//					if ( k % 1000 == 0 ) {
//						System.out.println("每秒抓拍量:"+(k/(endTime-startTime)*1.0));
//					}
//				}
//			}, 10000, 10, TimeUnit.MILLISECONDS);
//		}
//
//		*/
//	}
//
//}
