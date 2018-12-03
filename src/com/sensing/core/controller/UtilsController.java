package com.sensing.core.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.imageio.stream.FileImageInputStream;
import javax.servlet.http.HttpServletResponse;

import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.results.ResultUtils;
import com.sensing.core.utils.time.DateUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.service.ICapAttrConvertService;
import com.sensing.core.service.ICapService;
import com.sensing.core.service.IExportExcelService;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.thrift.cmp.bean.FeatureInfo;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.UuidUtil;
import com.sun.net.httpserver.HttpServer;


@Controller
@RequestMapping("/utils")
@SuppressWarnings("all")
public class UtilsController extends BaseController {

    private static final Log log = LogFactory.getLog(UtilsController.class);

    @Autowired
    public ICapAttrConvertService capAttrConvertService;
    @Autowired
    public IRpcLogService rpcLogService;
    @Autowired
    public ICapService capService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    public IExportExcelService exportExcelService;

    @ResponseBody
    @RequestMapping("/testMongo")
    public ResponseBean testMongo(@RequestBody JSONObject p) {
        ResponseBean result = new ResponseBean();
        try {
            String uuid = p.getString("uuid");

            DBObject objQuery = new BasicDBObject();
            objQuery.put("uuid", uuid);
            BasicDBObject fieldsObject = new BasicDBObject();
            //指定返回的字段
            fieldsObject.put("capUrl", true);

            Query query = new BasicQuery(objQuery);
            for (int i = 0; i < 200; i++) {
                DBObject dbObject = mongoTemplate.getCollection("CapPeople").findOne(objQuery);
            }

            result.setError(0);
            result.setMessage("successful");
        } catch (Exception e) {
            log.error(e);
            result.setError(100);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/feaByImgBase")
    public ResponseBean feaByImgBase(@RequestBody JSONObject p) {
        ResponseBean result = new ResponseBean();
        try {
            Integer type = p.getInteger("type");
            FeatureInfo featureInfo = null;
            if (type == 1) {
                featureInfo = capService.getFeaByImgBase(p.getString("imgBase64"));
            } else {
                featureInfo = capService.getFeaByImgBytes(image2byte("imgBytes"));
            }
            result.setError(0);
            result.setMessage("successful");
        } catch (Exception e) {
            log.error(e);
            result.setError(100);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    public byte[] image2byte(String path) {
        byte[] data = null;
        FileImageInputStream input = null;
        try {
            input = new FileImageInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

    @ResponseBody
    @RequestMapping("/testTransactional")
    public ResponseBean testTransactional(@RequestBody JSONObject p) {
        log.info("调用/sysOrg/query接口参数：" + p);
        ResponseBean result = new ResponseBean();
        try {

            rpcLogService.saveNewRpcLog(new RpcLog(new Date(), "测试", "主动", "日志名称", "save", "192.168.1.109", 8000, "httpclient", 109, "成功", "事务测试数据", "", 2));
            result.setError(0);
            result.setMessage("successful");
        } catch (Exception e) {
            log.error(e);
            result.setError(100);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/test")
    public ResponseBean query(@RequestBody JSONObject p) {
        log.info("调用/sysOrg/query接口参数：" + p);
        Pager pager = new Pager();
        if (!p.isEmpty()) {
            pager = JSONObject.toJavaObject(p, Pager.class);
        }
        ResponseBean result = new ResponseBean();
        try {
            Person person = new Person();
            person.setUuid(UuidUtil.getUuid());
            person.setAge(2);
//            person.setSex(1);
            person.setGenderCode(1);
//            person.setLowerClothesTexture(3);
            person.setTrousersTexture(3);

            capAttrConvertService.personConvert(person);

            NonmotorVehicle nonmotorVehicle = new NonmotorVehicle();
            nonmotorVehicle.setUuid(UuidUtil.getUuid());
            nonmotorVehicle.setAge(2);
            nonmotorVehicle.setGenderCode(1);;
            nonmotorVehicle.setVehicleColor(11);

            capAttrConvertService.nonmotorVehicleConvert(nonmotorVehicle);

            MotorVehicle motorVehicle = new MotorVehicle();
            motorVehicle.setUuid(UuidUtil.getUuid());
            motorVehicle.setPlateColor(4);
            motorVehicle.setCalling(1);
            motorVehicle.setOrientation(3);
            motorVehicle.setPlateClass(8);

            capAttrConvertService.motorVehicleConvert(motorVehicle);

            result.getMap().put("pager", pager);
            result.setError(0);
            result.setMessage("successful");
        } catch (Exception e) {
            log.error(e);
            result.setError(100);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/testTime")
    public ResponseBean testTime(@RequestBody JSONObject p) {
        log.info("调用/utils/testTime接口参数：" + p);
        Pager pager = new Pager();
        ResponseBean result = new ResponseBean();
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            log.info("date:" + date);
            log.info("pretty:" + sdf.format(date));
        } catch (Exception e) {
            log.error(e);
            result.setError(100);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 用户测试tomcat是否启动的接口
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/tomcatheart")
    public ResponseBean tomcatHeart(@RequestBody JSONObject p) {
        return new ResponseBean();
    }

    @ResponseBody
    @RequestMapping("/testProFiles")
    public ResponseBean testProFiles(@RequestBody JSONObject p) {
        String string = PropUtils.getString("default.core.ip");
        log.info("info==="+string);
        return new ResponseBean(-1, "default.core.ip===="+string);
    }

}
	
