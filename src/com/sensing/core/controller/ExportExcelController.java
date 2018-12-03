package com.sensing.core.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.Channel;
import com.sensing.core.bean.MotorVehicle;
import com.sensing.core.bean.NonmotorVehicle;
import com.sensing.core.bean.Person;
import com.sensing.core.resp.CapResp;
import com.sensing.core.service.IChannelService;
import com.sensing.core.service.IExportExcelService;
import com.sensing.core.service.ISearchQueryService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.FileUtil;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.results.ResultUtils;
import com.sensing.core.utils.time.DateStyle;
import com.sensing.core.utils.time.DateUtil;
import com.sensing.core.utils.time.TransfTimeUtil;

@Controller
@RequestMapping("export")
//@SuppressWarnings("all")
public class ExportExcelController extends BaseController {

	private static final Log log = LogFactory.getLog(ExportExcelController.class);
	@Resource
	private IExportExcelService exportExcelService;
	@Resource
	private ISearchQueryService searchQueryService;
	@Resource
	private IChannelService channelService;

	/**
	 * 日志文件导出
	 * 
	 * @param p        检索条件
	 * @param response
	 * @return
	 * @author mingxingyu
	 * @date 2018年11月16日 下午4:48:07
	 */
	@ResponseBody
	@RequestMapping("/logExportToExcel")
	public ResponseBean testExport(@RequestBody JSONObject p, HttpServletResponse response) {

		Pager pager = new Pager();
		if (!p.isEmpty()) {
			pager = JSONObject.toJavaObject(p, Pager.class);
		}
		SXSSFWorkbook workbook = exportExcelService.logExportToExcel(pager);

		if (workbook != null) {
			// 设置压缩流：直接写入response，实现边压缩边下载
			OutputStream os = null;
			try {
				os = new BufferedOutputStream(response.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 循环将文件写入压缩流
			try {
				String downloadFilename = "日志文件.xlsx";// 文件的名称
				downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");// 转换中文否则可能会产生乱码
				response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
				response.setHeader("Content-Disposition",
						"attachment;filename=" + URLEncoder.encode(downloadFilename, "UTF-8"));// 设置在下载框默认显示的文件名

				workbook.write(os);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ResultUtils.success(null);
	}

	/**
	 * 导出检索数据
	 * 
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
//	@ResponseBody
	@RequestMapping("/exportSearchToExcel")
	public void exportSearchInfoToExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("开始导出检索数据，调用 export/exportSearchToExcel接口");
		List<CapResp> respList = new ArrayList<CapResp>();
		List<?> list = null;
		Channel channel = null;
		long l1 = System.currentTimeMillis();
//		if (json == null || json.isEmpty()) {
//			log.error("请求参数非法!");
//		}
		String capType = request.getParameter("capType");
		String uuids = request.getParameter("uuids");

//		String capType = pager.getF().get("capType" + "");
//		String uuids = pager.getF().get("uuids" + "");
		if (StringUtils.isNotEmpty(uuids)) {
			Map<String, Object> map = searchQueryService.queryCapByUuids(Integer.valueOf(capType), uuids);
			list = (List<?>) map.get("list" + "");
			channel = (Channel) map.get("channel" + "");
		} else {
			Map<String, String> f = new HashMap<String, String>();
			Enumeration<String> parameterNames = request.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				String paramName = parameterNames.nextElement();
				String paramValue = request.getParameter(paramName);
				// 形成键值对应的map
				f.put(paramName, paramValue);
			}
			Pager pager = new Pager();
			pager.setF(f);
			pager = searchQueryService.queryPage(pager);
			list = pager.getResultList();
			List<Channel> channels = channelService.getChannelByIds(pager.getF().get("deviceId" + ""));
			if (channels != null && channels.size() > 0) {
				channel = channels.get(0);
			}
		}
		switch (capType) {
		case "1":
			for (Object capPeople : list) {
				CapResp resp = new CapResp();
				resp.setCapPeople((Person) capPeople);
				resp.setChannel(channel);
				respList.add(resp);
			}
			break;
		case "3":
			for (Object motorVehicle : list) {
				CapResp resp = new CapResp();
				resp.setMotorVehicle((MotorVehicle) motorVehicle);
				resp.setChannel(channel);
				respList.add(resp);
			}
			break;
		case "4":
			for (Object capNonmotor : list) {
				CapResp resp = new CapResp();
				resp.setCapNonmotor((NonmotorVehicle) capNonmotor);
				resp.setChannel(channel);
				respList.add(resp);
			}
			break;
		default:
			break;
		}
		long l2 = System.currentTimeMillis();
		System.out.println("查询mongo耗时----------------" + (l2 - l1) / 1000);
		long l3 = System.currentTimeMillis();
		SXSSFWorkbook workbook = null;
		try {
			workbook = exportExcelService.exportSearchInfoToExcel(Integer.valueOf(capType), respList);
			long l4 = System.currentTimeMillis();
			System.out.println("生成excel耗时----------------" + (l4 - l3) / 1000);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
//		String realPath = null;
		String dateStr = DateUtil.DateToString(new Date(), "yyyyMMddHHmmssSSS");
//		File file = null;
		long l5 = System.currentTimeMillis();
//		if (workbook != null) {
//			dateStr = DateUtil.DateToString(new Date(), "yyyyMMddHHmmssSSS");
//			String path = request.getSession().getServletContext().getRealPath("/");
//			// D:\3_myspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\ROOT/file/20181113134822647.xlsx
//			realPath = path.substring(0, path.lastIndexOf(Constants.PROJECT_NAME)) + Constants.PROJECT_ROOT + "/"
//					+ Constants.EXPORT_EXCEL_FILENAME + "/" + dateStr + ".xlsx";
//			file = new File(realPath);
//			if (!file.exists()) {
//				try {
//					file.getParentFile().mkdir();
//					file.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//					log.error(e);
//				}
//			}
//			try {
//				FileOutputStream outStream = new FileOutputStream(file);
//				workbook.write(outStream);
//				outStream.flush();
//				outStream.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.error(e);
//			}
//		}
		long l6 = System.currentTimeMillis();
		System.out.println("保存excel耗时----------------" + (l6 - l5) / 1000);
		// 设置压缩流：直接写入response，实现边压缩边下载
		long l7 = System.currentTimeMillis();
		ZipOutputStream zipos = null;
		try {
			zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
			zipos.setMethod(ZipOutputStream.DEFLATED); // 设置压缩方法
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 循环将文件写入压缩流
		try {
			String downloadFilename = "dataexport.zip";// 文件的名称
			downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");// 转换中文否则可能会产生乱码
			response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
			response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
			String folder = "pictures/";
			switch (capType) {
			case "1":
				for (int i = 0; i < respList.size(); i++) {
					String uri = respList.get(i).getCapPeople().getCapUrl();
					URL url = new URL(uri);
					String suffix = respList.get(i).getCapPeople().getCapUrl()
							.substring(respList.get(i).getCapPeople().getCapUrl().lastIndexOf("."));
					zipos.putNextEntry(new ZipEntry(folder + (i + 1) + "-1" + suffix));
					InputStream fis = url.openConnection().getInputStream();
					byte[] buffer = new byte[1024];
					int r = 0;
					while ((r = fis.read(buffer)) != -1) {
						zipos.write(buffer, 0, r);
					}
					String url2 = respList.get(i).getCapPeople().getSeceneUrl();
					URL url1 = new URL(url2);
					String suffix1 = respList.get(i).getCapPeople().getSeceneUrl()
							.substring(respList.get(i).getCapPeople().getSeceneUrl().lastIndexOf("."));
					zipos.putNextEntry(new ZipEntry(folder + (i + 1) + "-2" + suffix1));
					InputStream fis1 = url1.openConnection().getInputStream();
					byte[] buffer1 = new byte[1024];
					int r1 = 0;
					while ((r1 = fis1.read(buffer1)) != -1) {
						zipos.write(buffer1, 0, r1);
					}
					fis.close();
					fis1.close();
				}
				break;
			case "3":
				for (int i = 0; i < respList.size(); i++) {
					String uri = respList.get(i).getMotorVehicle().getCapUrl();
					URL url = new URL(uri);
					String suffix = respList.get(i).getMotorVehicle().getCapUrl()
							.substring(respList.get(i).getMotorVehicle().getCapUrl().lastIndexOf("."));
					zipos.putNextEntry(new ZipEntry(folder + (i + 1) + "-1" + suffix));
					InputStream fis = url.openConnection().getInputStream();
					byte[] buffer = new byte[1024];
					int r = 0;
					while ((r = fis.read(buffer)) != -1) {
						zipos.write(buffer, 0, r);
					}
					String url2 = respList.get(i).getMotorVehicle().getSeceneUrl();
					URL url1 = new URL(url2);
					String suffix1 = respList.get(i).getMotorVehicle().getSeceneUrl()
							.substring(respList.get(i).getMotorVehicle().getSeceneUrl().lastIndexOf("."));
					zipos.putNextEntry(new ZipEntry(folder + (i + 1) + "-2" + suffix1));
					InputStream fis1 = url1.openConnection().getInputStream();
					byte[] buffer1 = new byte[1024];
					int r1 = 0;
					while ((r1 = fis1.read(buffer1)) != -1) {
						zipos.write(buffer1, 0, r1);
					}
					fis.close();
					fis1.close();
				}
				break;
			case "4":
				for (int i = 0; i < respList.size(); i++) {
					String uri = respList.get(i).getCapNonmotor().getCapUrl();
					URL url = new URL(uri);
					String suffix = respList.get(i).getCapNonmotor().getCapUrl()
							.substring(respList.get(i).getCapNonmotor().getCapUrl().lastIndexOf("."));
					zipos.putNextEntry(new ZipEntry(folder + (i + 1) + "-1" + suffix));
					InputStream fis = url.openConnection().getInputStream();
					byte[] buffer = new byte[1024];
					int r = 0;
					while ((r = fis.read(buffer)) != -1) {
						zipos.write(buffer, 0, r);
					}
					String url2 = respList.get(i).getCapNonmotor().getSeceneUrl();
					URL url1 = new URL(url2);
					String suffix1 = respList.get(i).getCapNonmotor().getSeceneUrl()
							.substring(respList.get(i).getCapNonmotor().getSeceneUrl().lastIndexOf("."));
					zipos.putNextEntry(new ZipEntry(folder + (i + 1) + "-2" + suffix1));
					InputStream fis1 = url1.openConnection().getInputStream();
					byte[] buffer1 = new byte[1024];
					int r1 = 0;
					while ((r1 = fis1.read(buffer1)) != -1) {
						zipos.write(buffer1, 0, r1);
					}
					fis.close();
					fis1.close();
				}
				break;
			default:
				break;
			}
			String suffix2 = ".xlsx";
			zipos.putNextEntry(new ZipEntry(dateStr + suffix2));
//			InputStream inputStream = new FileInputStream(realPath);
//			byte[] buffer2 = new byte[1024];
//			int r2 = -1;
//			while ((r2 = inputStream.read(buffer2)) != -1) {
//				zipos.write(buffer2, 0, r2);
//			}
			workbook.write(zipos);
			long l8 = System.currentTimeMillis();
			System.out.println("边压缩边下载耗时-----------------------" + (l8 - l7) / 1000);
//			inputStream.close();
			zipos.flush();
			zipos.close();
//			FileUtil.deleteExcelPath(file);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出通行量数据
	 * 
	 * @param json
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/exporttrafficCountInfoToExcel")
	public ResponseBean exporttrafficCountInfoToExcel(@RequestBody JSONObject json, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("开始导出通行量数据，调用 export/exporttrafficCountInfoToExcel接口，传递参数为: " + json);
		ResponseBean result = new ResponseBean();
		Map<String, Object> param = new HashMap<String, Object>();
		if (json == null || json.isEmpty()) {
			log.error("请求参数非法!");
			result = ResultUtils.FAIL();
			return result;
		}
		String channelUuids = json.getString("channelUuids" + "");
		String[] ids = channelUuids.split(",");
		List<Channel> channels = channelService.getChannelByIds(ids);

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> mList = new ArrayList<>();
		String startTime = json.getString("startTime" + "");
		String endTime = json.getString("endTime" + "");
		Long sTime = TransfTimeUtil.Date2TimeStampReturnLong(startTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
		Long eTime = TransfTimeUtil.Date2TimeStampReturnLong(endTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
		if (eTime - sTime > 86400) {// 导出多天数据
			int i = (int) ((eTime - sTime) / 86400);// 导出i天数据
			for (int j = i; j > 0; j--) {
				String time = TransfTimeUtil.UnixTimeStamp2Date(sTime, DateStyle.YYYY_MM_DD);
				map.put("channels", channels);
				eTime = sTime + 86400;
				map = searchQueryService.trafficCount(channelUuids,
						TransfTimeUtil.UnixTimeStamp2Date(sTime, DateStyle.YYYY_MM_DD_HH_MM_SS),
						TransfTimeUtil.UnixTimeStamp2Date(eTime, DateStyle.YYYY_MM_DD_HH_MM_SS));
				map.put("time", time);
				sTime = eTime;
				mList.add(map);
			}
			System.out.println(mList);
		} else {
			map = searchQueryService.trafficCount(channelUuids, startTime, endTime);
			String time = TransfTimeUtil.TimeStamp2DateByString(startTime, DateStyle.YYYY_MM_DD_HH_MM_SS,
					DateStyle.YYYY_MM_DD);
			map.put("time", time);
			map.put("channels", channels);
			mList.add(map);
		}
		SXSSFWorkbook workbook = null;
		String filePath = null;
		try {
			workbook = exportExcelService.exporttrafficCountInfoToExcel(mList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出通行量数据失败，错误信息为：" + e);
			result = ResultUtils.UNKONW_ERROR();
		}
		if (workbook != null) {
			String dateStr = DateUtil.DateToString(new Date(), "yyyyMMddHHmmssSSS");
			String path = request.getSession().getServletContext().getRealPath("/");
			String realPath = path.substring(0, path.lastIndexOf(Constants.PROJECT_NAME)) + Constants.PROJECT_ROOT + "/"
					+ Constants.EXPORT_EXCEL_FILENAME + "/" + dateStr + ".xlsx";
			File file = new File(realPath);
			if (!file.exists()) {
				try {
					file.getParentFile().mkdir();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e);
					result = ResultUtils.UNKONW_ERROR();
				}
			}
			try {
				FileOutputStream outStream = new FileOutputStream(file);
				workbook.write(outStream);
				outStream.flush();
				outStream.close();
				filePath = Constants.WEB_DOWNLAOD_URL + Constants.EXPORT_EXCEL_FILENAME + "/" + dateStr + ".xlsx";
				param.put("filePath", filePath);
				param.put("realPath", realPath);
				result = ResultUtils.success(param);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				result = ResultUtils.UNKONW_ERROR();
			}
		}
		return result;
	}

}
