package com.sensing.core.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.IdCardUtils;
import com.sensing.core.utils.MatchUtil;
import com.sensing.core.utils.POIUtils;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ReadExcel;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.UuidUtil;
import com.sensing.core.utils.ValidationUtils;
import com.sensing.core.utils.httputils.HttpDeal;
import com.sensing.core.utils.props.RemoteInfoUtil;
import com.sensing.core.utils.results.ResultUtils;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sensing.core.bean.ImageFile;
import com.sensing.core.bean.Template;
import com.sensing.core.bean.TemplateDb;
import com.sensing.core.bean.TemplateObjMotor;
import com.sensing.core.service.IJobsService;
import com.sensing.core.service.ITemplateDbService;
import com.sensing.core.service.ITemplateObjMotorService;
import com.sensing.core.service.ITemplateService;

@Controller
@RequestMapping("/templateObjMotor")
public class TemplateObjMotorController extends BaseController {

	private static final Log log = LogFactory.getLog(TemplateObjMotorController.class);

	@Resource
	public ITemplateObjMotorService templateObjMotorService;
	@Resource
	public IJobsService jobsService;
	@Resource
	public ITemplateService templateService;
	@Resource
	public ITemplateDbService templateDbService;

	@ResponseBody
	@RequestMapping("/query")
	public ResponseBean query(@RequestBody JSONObject p) throws Exception {
		log.info("开始查询车辆信息，调用 templateObjMotor/query 接口，传递参数为: " + p);
		Pager pager = JSONObject.toJavaObject(p, Pager.class);
		ResponseBean result = new ResponseBean();
		pager = templateObjMotorService.queryPage(pager);
		result = ResultUtils.success(result, "pager", pager);
		return result;
	}

	@ResponseBody
	@RequestMapping("/update")
	public ResponseBean update(@RequestBody JSONObject m) throws Exception {
		log.info("开始更新车辆信息，调用 templateObjMotor/update 接口，传递参数为: " + m);
		TemplateObjMotor model = JSONObject.toJavaObject(m, TemplateObjMotor.class);
		ResponseBean result = new ResponseBean();
		String validateInfo = validateData(model, 2);
		if (validateInfo != null) {
			return ResultUtils.error(result, 1000, validateInfo);
		}
		// 判断目标库是否存在
		if (model.getTemplatedbId() != null) {
			TemplateDb templateDb = templateDbService.findTemplateDbById(model.getTemplatedbId(), 0);
			if (templateDb == null) {
				return ResultUtils.error(result, 100, "目标库不存在！");
			}
		}
		// 校验车主信息
		model = validateOwnerContactinfo(model);
		result = templateObjMotorService.updateTemplateObjMotor(model, result);
		return result;
	}

	/**
	 * 保存车辆信息数据
	 * 
	 * @param m
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/save")
	public ResponseBean save(@RequestBody JSONObject m) throws Exception {
		TemplateObjMotor model = JSONObject.toJavaObject(m, TemplateObjMotor.class);
		ResponseBean result = new ResponseBean();
		String validateInfo = validateData(model, 1);
		if (validateInfo != null) {
			return ResultUtils.error(result, 1000, validateInfo);
		}
		model = validateOwnerContactinfo(model);
		result = templateObjMotorService.saveNewTemplateObjMotor(m, model, result);
		return result;
	}

	private TemplateObjMotor validateOwnerContactinfo(TemplateObjMotor model) {
		if (StringUtils.isNotEmpty(model.getOwnerName()) || StringUtils.isNotEmpty(model.getOwnerTel())
				|| StringUtils.isNotEmpty(model.getOwnerIdno()) || StringUtils.isNotEmpty(model.getMemo())
				|| StringUtils.isNotEmpty(model.getOwnerAddr())) {
			model.setOwnerContactinfo((short) (1));
		} else {
			model.setOwnerContactinfo((short) (0));
		}
		return model;
	}

	// 1-保存 2-更新
	private String validateData(TemplateObjMotor model, int i) {
		if (model == null) {
			return "数据转换失败！";
		}
		if (i == 1) {
			// 车辆库唯一标识校验
			if (model.getTemplatedbId() == null || model.getTemplatedbId() == 0) {
				return "车辆库唯一标识校验失败！";
			}
			// 车牌号校验
			if (!StringUtils.isNotEmpty(model.getPlateNo())) {
				return "车牌号校验失败！";
			}
			// 主模版索引校验
			if (model.getMainTemplateIndex() == null || !ValidationUtils
					.checkValueRange(model.getMainTemplateIndex() - 0, new Integer[] { 0, 1, 2, 3, 4, 5 })) {
				return "主模版索引校验失败！";
			}
		}
		if (i == 2) {
			// 唯一标识校验
			if (!StringUtils.isNotEmpty(model.getUuid())) {
				return "唯一标识校验失败，唯一标识不能为空！";
			}
		}
		// 联系方式校验
		if (StringUtils.isNotEmpty(model.getOwnerTel()) && !MatchUtil.isMobile(model.getOwnerTel())
				&& !MatchUtil.isPhone(model.getOwnerTel())) {
			return "联系方式校验失败，联系方式输入错误！";
		}
		// 身份证号码校验
		if (StringUtils.isNotEmpty(model.getOwnerIdno()) && !IdCardUtils.validateCard(model.getOwnerIdno())) {
			return "身份证号码校验失败，身份证号码输入错误！";
		}
		// 描述校验，长度不超过300字符
		if (StringUtils.isNotEmpty(model.getMemo()) && !ValidationUtils.checkStrLengthLess(model.getMemo(), 300)) {
			return "描述校验失败，长度不能超过300字符！";
		}
		// 名字校验，长度不超过20字符
		if (StringUtils.isNotEmpty(model.getOwnerName())
				&& !ValidationUtils.checkStrLengthLess(model.getOwnerName(), 20)) {
			return "描述校验失败，长度不能超过20字符！";
		}
		return null;
	}

	@ResponseBody
	@RequestMapping("/deleteByUuid")
	public ResponseBean deleteByUuid(@RequestBody JSONObject json) {
		log.info("开始逻辑删除车辆信息，调用 templateObjMotor/deleteByUuid 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			return ResultUtils.error(result, 100, "请求参数非法");
		}
		String uuid = json.getString("uuid");
		try {
			TemplateObjMotor templateObjMotor = templateObjMotorService.findTemplateObjMotorById(uuid);
			if (templateObjMotor != null) {
				// 判断是否有布控任务
				int i = jobsService.selectTemplatedbIdCount(templateObjMotor.getTemplatedbId());
				if (i != 0 && i > 0) {
					return ResultUtils.error(result, 100, "布控任务关联此库，不能删除！");
				}
				templateObjMotor.setIsDeleted((short) 1);
				result = templateObjMotorService.updateTemplateObjMotor(templateObjMotor, result);
			} else {
				result = ResultUtils.error(result, 100, "此车辆信息不存在！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("调用逻辑删除车辆信息发生异常，异常信息:" + e.getMessage());
			result = ResultUtils.error(result, 100, e.getMessage());
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/delete")
	public ResponseBean delete(@RequestBody String[] idarr) {
		ResponseBean result = new ResponseBean();
		try {
			for (int i = 0; idarr != null && i < idarr.length; i++) {
				templateObjMotorService.removeTemplateObjMotor(idarr[i]);
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
	@RequestMapping("/uploadImage")
	public ResponseBean uploadImage(@RequestBody JSONObject json) throws Exception {
		log.info("开始上传图片到服务器，调用 templateObjMotor/uploadImage 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		String validateInfo = validateData(json);
		if (validateInfo != null) {
			return ResultUtils.error(result, 1000, validateInfo);
		}
		String index = json.getString("index");
		String objUuid = json.getString("objUuid");
		String imageData = json.getString("imageData");
		List<Template> list = templateService.getTemplateByObjUuid(objUuid);
		if (list != null && list.size() > 0) {
			for (Template template : list) {
				if (String.valueOf(template.getIndex()).equals(index)) {
					return ResultUtils.error(result, 100, "该索引已存在！");
				}
			}
		}
		String data = "";
		String postfix = "";
		byte[] imgByte = null;
		String[] d = imageData.split(";base64,");
		if (d != null && d.length == 2) {
			postfix = "." + d[0].split(":")[1].split("/")[1];
			data = d[1];
		} else {
			throw new RuntimeException("数据格式错误！");
		}
		imgByte = Base64.decodeBase64(data.getBytes("UTF-8"));
		if (imgByte == null || imgByte.length == 0) {
			throw new RuntimeException("图片错误！");
		}
		String prefix = UuidUtil.getUuid();
		// 保存图片到服务器
		String seceneURI = prefix + postfix;
		String secenePut = HttpDeal.doPut(seceneURI, imgByte);
		ImageFile seceneImageFile = JSONObject.toJavaObject(JSONObject.parseObject(secenePut), ImageFile.class);
		if (seceneImageFile.getError() >= 0) {
			TemplateObjMotor templateObjMotor = templateObjMotorService.findTemplateObjMotorById(objUuid);
			String imgUrl = seceneImageFile.getMessage();
			Template t = new Template();
			t.setUuid(prefix);
			t.setCreateTime(new Date().getTime() / 1000);
			t.setIsDeleted((short) 0);
			t.setObjUuid(objUuid);
			t.setTemplatedbId(templateObjMotor.getTemplatedbId());
			t.setIndex(new Short(index));
			t.setImageUrl(imgUrl);
			templateService.saveNewTemplate(t);
			t.setImageUrl(RemoteInfoUtil.REMOTE_IMG_SERVER + imgUrl);
			result = ResultUtils.success(result, "model", t);
		} else {
			result = ResultUtils.error(result, 1000, "未获取到图片的地址！");
		}
		return result;
	}

	private String validateData(JSONObject json) {
		if (json == null || json.isEmpty()) {
			return "请求参数非法";
		}
		String index = json.getString("index");
		String objUuid = json.getString("objUuid");
		String imageData = json.getString("imageData");
		if (StringUtils.isEmptyOrNull(index)
				|| !ValidationUtils.checkValueRange(Integer.parseInt(index) - 0, new Integer[] { 0, 1, 2, 3, 4, 5 })) {
			return "索引校验失败，不能为空，且数值需在指定范围内！";
		}
		if (StringUtils.isEmptyOrNull(objUuid)) {
			return "车辆信息唯一标识校验失败，数值不能为空！";
		}
		if (StringUtils.isEmptyOrNull(imageData)) {
			return "图片信息校验失败，值不能为空！";
		}
		return null;
	}

	@ResponseBody
	@RequestMapping("/deleteImage")
	public ResponseBean deleteImage(@RequestBody JSONObject json) throws Exception {
		log.info("开始删除图片，调用 templateObjMotor/deleteImage 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			return ResultUtils.error(result, 100, "请求参数非法");
		}
		String uuid = json.getString("templateUuid");
		templateService.removeTemplate(uuid);
		result = ResultUtils.success();
		return result;
	}

	@ResponseBody
	@RequestMapping("/setMainTemplate")
	public ResponseBean setMainTemplate(@RequestBody JSONObject json) throws Exception {
		log.info("开始设置主模版，调用 templateObjMotor/setMainTemplate 接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			return ResultUtils.error(result, 100, "请求参数非法");
		}
		String templateUuid = json.getString("templateUuid");
		String objUuid = json.getString("objUuid");
		TemplateObjMotor templateObjMotor = templateObjMotorService.findTemplateObjMotorById(objUuid);
		templateObjMotor.setMainTemplateUuid(templateUuid);
		result = templateObjMotorService.updateTemplateObjMotor(templateObjMotor, result);
		return result;
	}

	// 模板下载
	@RequestMapping(value = "/exportExcel", method = RequestMethod.GET)
	public void exportExcel(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
//		OutputStream ouputStream = null;
		try {
			// 创建excel对象
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			// 创建excel表单
			SXSSFSheet sheet = workbook.createSheet("Sheet1");

			// 设置表头样式
			Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
			CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "0000");

			// 设置样式
			Font font = POIUtils.getFont(workbook, "宋体", false, 11);
			CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "0000");
			CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "0000");
			// 设置文本自动换行
			alignCellStyle.setWrapText(true);
			leftCellStyle.setWrapText(true);

			// 第一行
			SXSSFRow hssfRow1 = sheet.createRow(0);
			POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
			POIUtils.setCell(hssfRow1, topCellStyle, "主图", 2);
			POIUtils.setCell(hssfRow1, topCellStyle, "车牌号码", 3);
			POIUtils.setCell(hssfRow1, topCellStyle, "车辆颜色", 4);
			POIUtils.setCell(hssfRow1, topCellStyle, "车辆类型", 5);
			POIUtils.setCell(hssfRow1, topCellStyle, "车牌颜色", 6);
			POIUtils.setCell(hssfRow1, topCellStyle, "车辆品牌", 7);
			POIUtils.setCell(hssfRow1, topCellStyle, "车主姓名", 8);
			POIUtils.setCell(hssfRow1, topCellStyle, "联系电话", 9);
			POIUtils.setCell(hssfRow1, topCellStyle, "身份证号", 10);
			POIUtils.setCell(hssfRow1, topCellStyle, "家庭住址", 11);
			POIUtils.setCell(hssfRow1, topCellStyle, "描述", 12);
			POIUtils.setCell(hssfRow1, topCellStyle, "车辆库名称", 13);

			String fileName = "车辆导入模板.xlsx";// 设置下载时客户端Excel的名称
			ZipOutputStream zipos = null;
			try {
				zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
				zipos.setMethod(ZipOutputStream.DEFLATED); // 设置压缩方法
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 循环将文件写入压缩流
			try {
				String downloadFilename = "车辆导入模板.zip";// 文件的名称
				downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");// 转换中文否则可能会产生乱码
				response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
				response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
				// excel写入压缩流
				zipos.putNextEntry(new ZipEntry(fileName));
				workbook.write(zipos);
				zipos.flush();
				zipos.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 操作结束，关闭文件
		}
	}

	// 模板导入
	@RequestMapping(value = "/importTemplateObjMotor", method = RequestMethod.POST)
	public ResponseBean importTemplateObjMotor(@RequestParam("file") MultipartFile file) throws Exception {
		ResponseBean result = new ResponseBean();
		try {
			// 创建处理EXCEL的类
			ReadExcel readExcel = new ReadExcel();
			// 解析excel，获取上传的事件单
			List<TemplateObjMotor> motorList = readExcel.getExcelInfo(file);
			// 至此已经将excel中的数据转换到list里面了,接下来就可以操作list,可以进行保存到数据库,或者其他操作,

			if (motorList != null && !motorList.isEmpty()) {
				for (TemplateObjMotor motor : motorList) {
					// 校验车辆库名称
					List<TemplateDb> list = templateDbService.queryTemplateDbByName(motor.getTemplatedbName());
					if (list != null && list.size() > 0) {
						TemplateDb templateDb = list.get(0);
						motor.setTemplatedbId(templateDb.getId());
					} else {
						return ResultUtils.error(result, 1000, "车辆库名称错误");
					}
					// 校验数据格式
					String validateInfo = validateData(motor, 1);
					if (validateInfo != null) {
						return ResultUtils.error(result, 1000, validateInfo);
					}
					templateObjMotorService.saveNewTemplateObjMotor(null, motor, result);
					result = ResultUtils.success();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
}
