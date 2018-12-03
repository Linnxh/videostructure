package com.sensing.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.bean.Channel;
import com.sensing.core.dao.IChannelDAO;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.ICapService;
import com.sensing.core.service.ISearchQueryService;
import com.sensing.core.service.IStaticTaskServce;
import com.sensing.core.service.ITaskService;
import com.sensing.core.thrift.cmp.bean.FeatureInfo;
import com.sensing.core.utils.BeanUtil;
import com.sensing.core.utils.BussinessException;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.NetUtils;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.httputils.HttpDeal;
import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.time.DateStyle;
import com.sensing.core.utils.time.DateUtil;
import com.sensing.core.utils.time.TransfTimeUtil;

@Service
@SuppressWarnings("all")
public class SearchQueryServiceImpl<Obejct> implements ISearchQueryService {

	private static final Log log = LogFactory.getLog(ISearchQueryService.class);
	@Resource
	private MongoTemplate mongoTemplate;
	@Resource
	public ICapAttrConvertService capAttrConvertService;
	@Resource
	public IChannelDAO channelDAO;
	@Resource
	public ICapService capService;
	@Resource
	public ITaskService taskService;

	private long total = 0;

	/**
	 * 根据uuid获取比对特征文件
	 * 
	 * @param jo 参数
	 * @return
	 * @author mingxingyu
	 * @throws Exception
	 * @date 2018年9月20日 下午2:47:56
	 */
	public List<Object> queryResultByCmpByUuid(JSONObject jo) throws Exception {
		String capUuid = jo.getString("uuid");
		Integer capType = jo.getInteger("capType");

		// 检索参数
		JSONObject cmpJson = new JSONObject();
		cmpJson.put("capType", capType + "");
		if (StringUtils.isNotEmpty(jo.getString("deviceId"))) {
			cmpJson.put("deviceId", jo.getString("deviceId"));
		} else {
			cmpJson.put("deviceId", "");
		}
		if (StringUtils.isNotEmpty(jo.getString("startTime"))) {
			cmpJson.put("startTime", jo.getString("startTime"));
		}
		if (StringUtils.isNotEmpty(jo.getString("endTime"))) {
			cmpJson.put("endTime", jo.getString("endTime"));
		}
		if (jo.getInteger("score") != null) {
			cmpJson.put("score", jo.getInteger("score"));
		} else {
			cmpJson.put("score", Constants.PHOTO_SEARCH_SCORE);
		}

		// 机动车和行人获取特征文件
		if (Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE == capType || Constants.CAP_ANALY_TYPE_PERSON == capType) {
			// 查询特征文件
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uuid", capUuid);
			params.put("type", capType);
			String cmpResult = HttpDeal.sendPost(Constants.CMP_FEATURE_URL, JSONObject.toJSONString(params));
			JSONObject resultJson = JSONObject.parseObject(cmpResult, JSONObject.class);
			Integer error = resultJson.getInteger("error");
			if (error == null || error != 0) {
				log.error("抓拍以图搜图调用失败，错误信息:" + resultJson.getString("message"));
				throw new BussinessException("抓拍以图搜图调用失败!");
			}
			// 获取特征文件
			JSONObject map = (JSONObject) resultJson.get("map");
			String capFeature = map.getString("fea");

			// 组装调用以图搜图的接口的对象
			cmpJson.put("capFeature", capFeature);
			// cmpJson.put("score", Constants.PHOTO_SEARCH_SCORE);
		}

		// 非机动车查询mongodb，已属性检索
		if (Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE == capType) {
			NonmotorVehicle nonmotor = (NonmotorVehicle) getObjectByUuid(capUuid, Constants.NONMOTOR_VEHICLE);

			cmpJson.remove("capFeature");
			cmpJson.remove("score");

			cmpJson.put("age", nonmotor.getAge().toString());
			cmpJson.put("genderCode", nonmotor.getGenderCode().toString());
			cmpJson.put("vehicleClass", nonmotor.getVehicleClass().toString());
			cmpJson.put("glass", nonmotor.getGlass().toString());
			cmpJson.put("cap", nonmotor.getCap().toString());
			cmpJson.put("respirator", nonmotor.getRespirator().toString());
			cmpJson.put("motion", nonmotor.getMotion().toString());
			cmpJson.put("orientation", nonmotor.getOrientation().toString());
			cmpJson.put("coatColor", nonmotor.getCoatColor().toString());
			cmpJson.put("coatLength", nonmotor.getCoatLength().toString());

		}
		// 删除不必要查询的key值
		removeObjKey(cmpJson);

		List<Object> list = queryResultByCmp(cmpJson);

		return list;
	}

	/**
	 * 以图搜图调用比对查询
	 * 
	 * @param jo
	 * @return
	 * @throws Exception
	 * @author mingxingyu
	 * @date 2018年9月17日 下午4:44:02
	 */
	public List<Object> queryResultByCmp(JSONObject jo) throws Exception {
		// 日期转换的格式
		SimpleDateFormat sdfLess = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfMore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 传入的参数
		String capType = jo.getString("capType");
		Integer capTypeInt = jo.getInteger("capType");
		String deviceId = jo.getString("deviceId");
		String startTime = jo.getString("startTime");
		String endTime = jo.getString("endTime");
		String capFeature = jo.getString("capFeature");
		Integer score = jo.getInteger("score");
		Long startTimeUnix = 0L;
		Long endTimeUnix = 0L;

		if (StringUtils.isBlank(deviceId)) {
			deviceId = "";
		}

		// 删除不必要查询的key值
		removeObjKey(jo);

		// 结果集
		List<Object> resultList = new ArrayList<Object>();
		// 非机动车检索mongodb
		if (capTypeInt == Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE) {
			jo.put("capType", capType);
			Map map = JSON.parseObject(jo.toJSONString());
			map.remove("score");
			map.remove("capFeature");
			Pager pager = new Pager();
			pager.setPageNo(1);
			pager.setPageRows(200);
			pager.setF(map);
			pager = queryPage(pager);
			resultList.addAll(pager.getResultList());
		}

		// 行人、机动车调用比对
		else if (capTypeInt == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE
				|| capTypeInt == Constants.CAP_ANALY_TYPE_PERSON) {
			// 查询时间转换
			Date date = new Date();
			if (startTime == null || "".equals(startTime)) {
				String string = sdfLess.format(date) + " 00:00:00";
				startTimeUnix = sdfMore.parse(string).getTime() / 1000;
			} else {
				startTimeUnix = sdfMore.parse(startTime).getTime() / 1000;
			}
			if (endTime == null || "".equals(endTime)) {
				endTimeUnix = date.getTime() / 1000;
			} else {
				endTimeUnix = sdfMore.parse(endTime).getTime() / 1000;
			}
			// 组装请求的参数
			JSONObject params = new JSONObject();
			params.put("fea1", capFeature);
			params.put("fea2", "");
			params.put("fea3", "");
			params.put("fea4", "");
			params.put("fea5", "");
			params.put("channel_array", deviceId);
			params.put("threshold", score * 0.01);
			params.put("cap_time_start", startTimeUnix);
			params.put("cap_time_end", endTimeUnix);
			params.put("type", capTypeInt);
			params.put("retNums", 200);
			// 比对
			long cmpStartTime = System.currentTimeMillis();
			String cmpResult = HttpDeal.sendPost(Constants.CMP_SERVER_URL, JSONObject.toJSONString(params));
			long cmpEndTime = System.currentTimeMillis();
			log.info("以图搜图调用比对返回的结果为:" + cmpResult);
			Map<String, Object> map = null;
			if (StringUtils.isNotEmpty(cmpResult)) {
				map = JSONObject.parseObject(cmpResult, Map.class);
			} else {
				log.error("以图搜图调用比对返回的结果为空.");
			}
			int error = (Integer) map.get("error");
			if (error != 0) {
				log.error("以图搜图调用比对发生异常." + (String) map.get("message"));
			}
			if (capTypeInt == Constants.CAP_ANALY_TYPE_PERSON) {
				Map<String, Object> mapObj = (Map<String, Object>) map.get("map");
				List<JSONObject> list = (List<JSONObject>) mapObj.get("result1");
				for (JSONObject resultJSON : list) {

					Person cp = new Person();
					cp.setUuid(resultJSON.getString("uuid"));
					cp.setCoatColor(resultJSON.getInteger("coatColor"));
					cp.setTrousersColor(resultJSON.getInteger("trousersColor"));
					cp.setBagStyle(resultJSON.getInteger("bagStyle"));
					cp.setGlass(resultJSON.getInteger("glass"));
					cp.setCap(resultJSON.getInteger("cap"));
					cp.setScore(resultJSON.getFloat("score"));
					cp.setCapTime(resultJSON.getLong("capTime"));
					cp = capAttrConvertService.personConvert(cp);
					long l1 = System.currentTimeMillis();
					Person people = (Person) getObjectByUuid(cp.getUuid(), Constants.PERSON);
					if (people != null && StringUtils.isNotEmpty(people.getCapUrl())) {
						cp.setCapUrl(people.getCapUrl());
					}
					long l2 = System.currentTimeMillis();
					System.out.println("根据uuid获取图片耗时"+(l2-l1));
					resultList.add(cp);
				}
			}
			if (capTypeInt == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE) {
				Map<String, Object> mapObj = (Map<String, Object>) map.get("map");
				List<JSONObject> list = (List<JSONObject>) mapObj.get("result1");
				for (JSONObject resultJSON : list) {

					MotorVehicle cm = new MotorVehicle();
					cm.setUuid(resultJSON.getString("uuid"));
					cm.setPlateNo(resultJSON.getString("plateNo"));
					cm.setVehicleColor(resultJSON.getInteger("vehicleColor"));
					cm.setVehicleBrandTag(resultJSON.getString("vehicleBrandTag"));
					cm.setVehicleClass(resultJSON.getInteger("vehicleClass"));
					cm.setScore(resultJSON.getFloat("score"));
					cm.setPlateColor(resultJSON.getInteger("plateColor"));
					cm.setCapTime(resultJSON.getLong("capTime"));
					cm = capAttrConvertService.motorVehicleConvert(cm);
					MotorVehicle motor = (MotorVehicle) getObjectByUuid(cm.getUuid(), Constants.MOTOR_VEHICLE);
					if (motor != null && StringUtils.isNotEmpty(motor.getCapUrl())) {
						cm.setCapUrl(motor.getCapUrl());
					}
					resultList.add(cm);
				}
			}
		}
		return resultList;
	}

	/**
	 * 获取mongodb的单个对象
	 * 
	 * @param uuid    抓拍的uuid
	 * @param capType 抓拍的类型(字符串格式)
	 * @return
	 * @author mingxingyu
	 * @throws Exception
	 * @date 2018年9月20日 下午2:47:56
	 */
	public Object getObjectByUuid(String uuid, String capType) throws Exception {
		DBObject objQuery = new BasicDBObject();
		objQuery.put("uuid", uuid);
		Query query = new BasicQuery(objQuery);
		
		long l3 = System.currentTimeMillis();
		DBObject dbObject = mongoTemplate.getCollection(capType).findOne(objQuery);
		long l4 = System.currentTimeMillis();
		System.out.println("查询mongo耗时-------"+(l4-l3));
		if (Constants.PERSON.equals(capType)) {
			long l1 = System.currentTimeMillis();
			Object c = BeanUtil.dbObject2Bean(dbObject, new Person());
			long l2 = System.currentTimeMillis();
			System.out.println("mongo转换耗时----"+(l2-l1));
			long l5 = System.currentTimeMillis();
			Person capPeopleConvert = capAttrConvertService.personConvert((Person) c);
			long l6 = System.currentTimeMillis();
			System.out.println("属性翻译耗时---------"+(l6-l5));
			return capPeopleConvert;
		} else if (Constants.MOTOR_VEHICLE.equals(capType)) {
			Object c = BeanUtil.dbObject2Bean(dbObject, new MotorVehicle());
			MotorVehicle capMotorConvert = capAttrConvertService.motorVehicleConvert((MotorVehicle) c);
			long l2 = System.currentTimeMillis();
			return capMotorConvert;
		} else if (Constants.NONMOTOR_VEHICLE.equals(capType)) {
			Object c = BeanUtil.dbObject2Bean(dbObject, new NonmotorVehicle());
			NonmotorVehicle capNonmotorConvert = capAttrConvertService.nonmotorVehicleConvert((NonmotorVehicle) c);
			long l2 = System.currentTimeMillis();
			return capNonmotorConvert;
		}
		return null;
	}

	/**
	 * 调用比对获取结果
	 * 
	 * @param postAddr 请求地址
	 * @param params   请求参数
	 * @return
	 * @author mingxingyu
	 * @date 2018年9月13日 下午8:27:18
	 */
	public List<Map<String, Object>> cmp(String postAddr, JSONObject params) {
		String res = HttpDeal.sendPost(postAddr, JSONObject.toJSONString(params));
		log.info("behavior  server return :" + res);
		Map<String, Object> map = null;
		if (StringUtils.isNotEmpty(res)) {
			map = JSONObject.parseObject(res, Map.class);
		} else {
			log.error("行为分析服务异常!");
		}
		int error = (Integer) map.get("error");
		if (error != 0) {
			log.error((String) map.get("message"));
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (int i = 1; i <= 5; i++) {
			resultList.addAll(((Map<String, List<Map<String, Object>>>) map.get("map")).get("result" + i));
		}
		return resultList;
	}

	@Override
	public Pager queryPage(Pager pager) throws Exception {
		if (Integer.parseInt(pager.getF().get("capType")) == Constants.CAP_ANALY_TYPE_PERSON) {
			Person people = new Person();
			pager = queryMongoDB(pager, (Obejct) people, Constants.PERSON);
		} else if (Integer.parseInt(pager.getF().get("capType")) == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE) {
			MotorVehicle motor = new MotorVehicle();
			pager = queryMongoDB(pager, (Obejct) motor, Constants.MOTOR_VEHICLE);
		} else if (Integer.parseInt(pager.getF().get("capType")) == Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE) {
			NonmotorVehicle nonmotor = new NonmotorVehicle();
			pager = queryMongoDB(pager, (Obejct) nonmotor, Constants.NONMOTOR_VEHICLE);
		}
		return pager;
	}

	private Pager queryMongoDB(Pager pager, Obejct o, String collection) throws Exception {
		List<Object> list = new ArrayList<>();
		Set<Entry<String, String>> entrySet = pager.getF().entrySet();
		Iterator iterator = entrySet.iterator();
		List<String> removeKey = new ArrayList<String>();
		while (iterator.hasNext()) {
			Map.Entry<String, String> key = (Entry<String, String>) iterator.next();
			// mongo里面没有capType字段，把这个检索字段去掉
			if (key.getKey().equals("capType")) {
				iterator.remove();
				entrySet.remove(key);
			}
		}
		Map map = pager.getF();
		Object bean = BeanUtil.transMap2Bean(map, o);
		DBObject query = new BasicDBObject(); // setup the query criteria 设置查询条件
		query = BeanUtil.bean2DBObject(bean);
//		BasicDBObject sort = new BasicDBObject();// 设置排序条件
		// 1表示正序-1表示倒序
//		BasicDBObject sort = new BasicDBObject("deviceId", 1).append("capTime", -1);
		// 调整排序
		BasicDBObject sort = new BasicDBObject("capTime", -1).append("deviceId", 1);
		String channel = pager.getF().get("deviceId" + "");
		String taskId = pager.getF().get("taskId" + "");
		if (StringUtils.isNotEmpty(taskId)) {
			Map<Long, Long> timeMap = taskService.getAnalyTimeByUuid(taskId);
			BasicDBList or = new BasicDBList();
			if (timeMap != null) {
				for (Map.Entry<Long, Long> entry : timeMap.entrySet()) {
					BasicDBObject clause = new BasicDBObject("capTime",
							new BasicDBObject().append("$gte", entry.getKey()).append("$lt", entry.getValue()));
					or.add(clause);
				}
				query = new BasicDBObject("$or", or);
			}
			if (StringUtils.isNotEmpty(pager.getF().get("startTime" + ""))
					&& StringUtils.isNotEmpty(pager.getF().get("endTime" + ""))) {
				BasicDBList and = new BasicDBList();
				Long startTime = TransfTimeUtil.Date2TimeStampReturnLong(pager.getF().get("startTime" + ""),
						DateStyle.YYYY_MM_DD_HH_MM_SS);
				Long endTime = TransfTimeUtil.Date2TimeStampReturnLong(pager.getF().get("endTime" + ""),
						DateStyle.YYYY_MM_DD_HH_MM_SS);
				BasicDBObject clause = new BasicDBObject("capTime",
						new BasicDBObject().append("$gte", startTime).append("$lt", endTime));
				and.add(clause);
				query = new BasicDBObject("$and", and);
			}
		}
		if (StringUtils.isNotEmpty(channel)) {
			List<String> channels = Arrays.asList(channel.split(",")).stream().map(s -> s.trim())
					.collect(Collectors.toList());
			query.put("deviceId", new BasicDBObject("$in", channels));
		}
		if (StringUtils.isNotEmpty(pager.getF().get("startTime" + ""))
				&& StringUtils.isNotEmpty(pager.getF().get("endTime" + "")) && !StringUtils.isNotEmpty(taskId)) {
			Long startTime = TransfTimeUtil.Date2TimeStampReturnLong(pager.getF().get("startTime" + ""),
					DateStyle.YYYY_MM_DD_HH_MM_SS);
			Long endTime = TransfTimeUtil.Date2TimeStampReturnLong(pager.getF().get("endTime" + ""),
					DateStyle.YYYY_MM_DD_HH_MM_SS);
			query.put("capTime", new BasicDBObject().append("$gte", startTime).append("$lt", endTime));
		}
		if (StringUtils.isNotEmpty(pager.getF().get("plateNo" + ""))) {
			String plateLicence = pager.getF().get("plateNo" + "");
			// 模糊匹配正则规则
			Pattern pattern = Pattern.compile("^.*" + plateLicence + ".*$", Pattern.CASE_INSENSITIVE);
			query.put("plateNo", pattern);
		}
		// 数据量过大时skip效率过低
//		DBCursor dbCursor = mongoTemplate.getCollection(collection).find(query)
		DBCursor dbCursor = mongoTemplate.getCollection(collection).find(query).sort(sort)
				.skip((pager.getPageNo() - 1) * pager.getPageRows()).limit(pager.getPageRows());
		Integer count = (int) mongoTemplate.getCollection(collection).count(query);
		while (dbCursor.hasNext()) {
			DBObject object = dbCursor.next();
			if (o instanceof Person) {
				Object c = BeanUtil.dbObject2Bean(object, new Person());
				Person capPeopleConvert = capAttrConvertService.personConvert((Person) c);
				list.add(capPeopleConvert);
			} else if (o instanceof MotorVehicle) {
				Object c = BeanUtil.dbObject2Bean(object, new MotorVehicle());
				MotorVehicle capMotorConvert = capAttrConvertService.motorVehicleConvert((MotorVehicle) c);
				list.add(capMotorConvert);
			} else if (o instanceof NonmotorVehicle) {
				Object c = BeanUtil.dbObject2Bean(object, new NonmotorVehicle());
				NonmotorVehicle capNonmotorConvert = capAttrConvertService.nonmotorVehicleConvert((NonmotorVehicle) c);
				list.add(capNonmotorConvert);
			}
		}
		pager.setResultList(list);
		pager.setTotalCount(count);
		pager.setTotalPages(count % pager.getPageRows() == 0 ? 1 : count / pager.getPageRows() + 1);
		return pager;
	}

	@Override
	public List<Map<String, Object>> queryChannelTraffic(String deviceId, Integer dateScope) throws Exception {
		long l1 = System.currentTimeMillis();
		DBObject query = new BasicDBObject(); // setup the query criteria 设置查询条件
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		query.put("deviceId", deviceId);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 统计一段时间范围的通道通行量
		if (dateScope == null) {
			dateScope = 7;
		}
		for (int i = dateScope - 1; i >= 0; i--) {
			Map<String, Object> map = new HashMap<String, Object>();
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, -i);
			Date time = c.getTime();
			String tagDay = sf.format(time);
			long beforTime = DateUtil.getDayStartTime(c.getTime()).getTime() / 1000;
			long afterTime = beforTime + 3600 * 24;
			query.put("capTime", new BasicDBObject().append("$gte", beforTime).append("$lt", afterTime));
			Long peopleCount = mongoTemplate.getCollection(Constants.PERSON).count(query);
			Long motorCount = mongoTemplate.getCollection(Constants.MOTOR_VEHICLE).count(query);
			Long nonmotorCount = mongoTemplate.getCollection(Constants.NONMOTOR_VEHICLE).count(query);
			map.put("deviceId", deviceId);
			map.put("date", tagDay);
			map.put("peopleCount", peopleCount);
			map.put("motorCount", motorCount);
			map.put("nonmotorCount", nonmotorCount);
			list.add(map);
		}
		long l2 = System.currentTimeMillis();
		return list;
	}

	@Override
	public Map<String, Object> queryCapByUuid(String uuid, Integer capType) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Object> list = new ArrayList<>();
		String deviceId = null;
		DBObject query = new BasicDBObject(); // setup the query criteria 设置查询条件
		query.put("uuid", uuid);
		if (capType == Constants.CAP_ANALY_TYPE_PERSON) {
			DBCursor dbCursor = mongoTemplate.getCollection(Constants.PERSON).find(query);
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				Person c = BeanUtil.dbObject2Bean(object, new Person());
				c = capAttrConvertService.personConvert(c);
				list.add(c);
				deviceId = c.getDeviceId();
			}
		} else if (capType == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE) {
			DBCursor dbCursor = mongoTemplate.getCollection(Constants.MOTOR_VEHICLE).find(query);
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				MotorVehicle c = BeanUtil.dbObject2Bean(object, new MotorVehicle());
				c = capAttrConvertService.motorVehicleConvert(c);
				list.add(c);
				deviceId = c.getDeviceId();
			}
		} else if (capType == Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE) {
			DBCursor dbCursor = mongoTemplate.getCollection(Constants.NONMOTOR_VEHICLE).find(query);
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				NonmotorVehicle c = BeanUtil.dbObject2Bean(object, new NonmotorVehicle());
				c = capAttrConvertService.nonmotorVehicleConvert(c);
				list.add(c);
				deviceId = c.getDeviceId();
			}
		}
		List<Channel> channels = channelDAO.getChannelByIds(deviceId);
		Channel channel = null;
		if (channels != null && channels.size() > 0) {
			channel = channels.get(0);
		}
		if (list != null && list.size() >= 1) {

			Map<String, Object> map = BeanUtil.transBean2Map(list.get(0));
			Object sceneUrlObj = map.get("seceneUrl");
			if (sceneUrlObj != null && StringUtils.isNotEmpty(sceneUrlObj.toString())) {
				String sceneUrl = sceneUrlObj.toString();
				BufferedImage sceneImg = ImageIO.read(new URL(sceneUrl));
				map.put("sceneWidth", sceneImg.getWidth());
				map.put("sceneHeight", sceneImg.getHeight());
			}
			param.put("model", map);
			param.put("channel", channel);
			return param;
		} else {
			return null;
		}
	}

	@Override
	public Map<String, Object> queryCapByUuids(Integer capType, String uuids) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		List<Object> list = new ArrayList<>();
		String deviceId = null;
		DBObject query = new BasicDBObject(); // setup the query criteria 设置查询条件
		List<String> uuid = Arrays.asList(uuids.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
		query.put("uuid", new BasicDBObject("$in", uuid));
		if (capType == Constants.CAP_ANALY_TYPE_PERSON) {
			DBCursor dbCursor = mongoTemplate.getCollection(Constants.PERSON).find(query);
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				Person c = BeanUtil.dbObject2Bean(object, new Person());
				c = capAttrConvertService.personConvert(c);
				list.add(c);
				deviceId = c.getDeviceId();
			}
		} else if (capType == Constants.CAP_ANALY_TYPE_MOTOR_VEHICLE) {
			DBCursor dbCursor = mongoTemplate.getCollection(Constants.MOTOR_VEHICLE).find(query);
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				MotorVehicle c = BeanUtil.dbObject2Bean(object, new MotorVehicle());
				c = capAttrConvertService.motorVehicleConvert(c);
				list.add(c);
				deviceId = c.getDeviceId();
			}
		} else if (capType == Constants.CAP_ANALY_TYPE_NONMOTOR_VEHICLE) {
			DBCursor dbCursor = mongoTemplate.getCollection(Constants.NONMOTOR_VEHICLE).find(query);
			while (dbCursor.hasNext()) {
				DBObject object = dbCursor.next();
				NonmotorVehicle c = BeanUtil.dbObject2Bean(object, new NonmotorVehicle());
				c = capAttrConvertService.nonmotorVehicleConvert(c);
				list.add(c);
				deviceId = c.getDeviceId();
			}
		}
		List<Channel> channels = channelDAO.getChannelByIds(deviceId);
		Channel channel = null;
		if (channels != null && channels.size() > 0) {
			channel = channels.get(0);
		}
		param.put("list", list);
		param.put("channel", channel);
		return param;
	}

	@Override
	public Map<String, Object> trafficCount(String ids, String startTime, String endTime) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> motorType = motorTypeConvert();
		Map<String, Object> nonmotorType = nonmotorTypeConvert();
		if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
			Map<String, Object> param = null;
			Long startTime1 = TransfTimeUtil.Date2TimeStampReturnLong(startTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
			Long endTime1 = TransfTimeUtil.Date2TimeStampReturnLong(endTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
			Aggregation aggregation1 = null;
			if (StringUtils.isNotEmpty(ids)) {
				List<String> channels = Arrays.asList(ids.split(",")).stream().map(s -> s.trim())
						.collect(Collectors.toList());
				aggregation1 = Aggregation.newAggregation(
						Aggregation.match(Criteria.where("deviceId").in(channels)
								.andOperator(Criteria.where("capTime").gte(startTime1).lt(endTime1))),
						Aggregation.group("vehicleClass").count().as("count"));
			} else {
				aggregation1 = Aggregation.newAggregation(
						Aggregation.match(Criteria.where("capTime").gte(startTime1).lt(endTime1)),
						Aggregation.group("vehicleClass").count().as("count"));
			}
			AggregationResults<BasicDBObject> outputTypeCount = mongoTemplate.aggregate(aggregation1, Constants.PERSON,
					BasicDBObject.class);
			for (Iterator<BasicDBObject> iterator = outputTypeCount.iterator(); iterator.hasNext();) {
				param = new HashMap<String, Object>();
				DBObject obj = iterator.next();// { "_id" : null , "count" : 10000}
				Integer count = (Integer) obj.get("count");
				param.put("type", "行人");
				param.put("count", count);
				resultList.add(param);
			}
			AggregationResults<BasicDBObject> outputTypeCount1 = mongoTemplate.aggregate(aggregation1,
					Constants.MOTOR_VEHICLE, BasicDBObject.class);
			for (Iterator<BasicDBObject> iterator = outputTypeCount1.iterator(); iterator.hasNext();) {
				param = new HashMap<String, Object>();
				DBObject obj = iterator.next();// { "_id" : 4 , "count" : 10000}
				Integer id = (Integer) obj.get("_id");
				Integer count = (Integer) obj.get("count");
				param.put("type", motorType.get(id.toString()));
				param.put("count", count);
				resultList.add(param);
			}
			AggregationResults<BasicDBObject> outputTypeCount2 = mongoTemplate.aggregate(aggregation1,
					Constants.NONMOTOR_VEHICLE, BasicDBObject.class);
			for (Iterator<BasicDBObject> iterator = outputTypeCount2.iterator(); iterator.hasNext();) {
				param = new HashMap<String, Object>();
				DBObject obj = iterator.next();// { "_id" : 2 , "count" : 100}
				Integer id = (Integer) obj.get("_id");
				Integer count = (Integer) obj.get("count");
				param.put("type", nonmotorType.get(id.toString()));
				param.put("count", count);
				resultList.add(param);
			}
		}
		map.put("resultList", resultList);
		return map;
	}

	/**
	 * 删除jsonObject中value值为0,99,"",未知,全部的key
	 * 
	 * @param jo
	 * @author mingxingyu
	 * @date 2018年9月19日 下午4:04:42
	 */
	public void removeObjKey(JSONObject jo) {
		Set<String> keySet = jo.keySet();
		List<String> removeKey = new ArrayList<String>();
		for (String key : keySet) {
//			String val = jo.get(key).toString();
			Object obj = jo.get(key);
			if (obj == null || StringUtils.isEmpty(obj.toString())) {
				removeKey.add(key);
				continue;
			}
			String val = obj.toString();
			if (
//					"0".equals(val) || "99".equals(val) || "未知".equals(val) || 
			"全部".equals(val)) {
				removeKey.add(key);
			}
		}
		for (String key : removeKey) {
			jo.remove(key);
		}
	}

	private Map<String, Object> nonmotorTypeConvert() {
		Map<String, Object> nonmotorType = new HashMap<String, Object>();
		nonmotorType.put("0", "未知");
		nonmotorType.put("1", "二轮自行车");
		nonmotorType.put("2", "二轮电动车/摩托车");
		nonmotorType.put("3", "三轮摩托车（带棚）");
		nonmotorType.put("4", "三轮摩托车（车厢封闭）");
		nonmotorType.put("5", "三轮摩托车（无棚&不封闭）");
		nonmotorType.put("99", "其他");
		return nonmotorType;
	}

	private Map<String, Object> motorTypeConvert() {
		Map<String, Object> motorType = new HashMap<String, Object>();
		motorType.put("0", "未知");
		motorType.put("1", "轿车");
		motorType.put("2", "面包车");
		motorType.put("3", "越野车（SUV）");
		motorType.put("4", "商务车（MPV）");
		motorType.put("5", "皮卡");
		motorType.put("6", "小型客车");
		motorType.put("7", "中型客车");
		motorType.put("8", "大型客车");
		motorType.put("9", "微型货车");
		motorType.put("10", "小型货车");
		motorType.put("11", "中型货车");
		motorType.put("12", "重型货车");
		motorType.put("99", "其他");
		return motorType;
	}

}
