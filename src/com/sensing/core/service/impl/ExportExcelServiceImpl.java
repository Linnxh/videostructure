package com.sensing.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.Channel;
import com.sensing.core.bean.RpcLog;
import com.sensing.core.resp.CapResp;
import com.sensing.core.service.IExportExcelService;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.utils.POIUtils;
import com.sensing.core.utils.Pager;
import com.sensing.core.utils.StringUtils;
import com.sensing.core.utils.time.DateUtil;

@Service
public class ExportExcelServiceImpl implements IExportExcelService {

	@Autowired
	public IRpcLogService rpcLogService;

	/**
	 * 根据查询条件导出日志，获取SXSSFWorkbook对象
	 */
	public SXSSFWorkbook logExportToExcel(Pager pagerParams) {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		try {
			// 创建excel表单
			SXSSFSheet sheet = workbook.createSheet("表格1");

			// 设置表头样式
			Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
			CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "1111");

			// 设置样式
			Font font = POIUtils.getFont(workbook, "宋体", false, 11);
			CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "1111");
			CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "1111");
			// 设置文本自动换行
			alignCellStyle.setWrapText(true);
			leftCellStyle.setWrapText(true);

			// 第一行
			SXSSFRow hssfRow1 = sheet.createRow(0);

			// 查询要导出的数据
//			pagerParams.addF("type","1");
//			pagerParams.addF("callTimeFrom","2018-11-11 16:39:31");
//			pagerParams.addF("callTimeEnd","2018-11-15 16:39:31");
//			pagerParams.setPageNo(1);
//			pagerParams.setPageRows(20);

			Pager pager = rpcLogService.queryPage(pagerParams);

			Integer type = Integer.parseInt(pagerParams.getF().get("type"));
			if (pager != null && pager.getResultList() != null && pager.getResultList().size() > 0) {
				// 登录日志
				if (type == 1) {
					// 设置列宽
					POIUtils.setColumnWidth(sheet, new Integer[] { 5, 15, 20, 30, 15, 15 });

					POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
					POIUtils.setCell(hssfRow1, topCellStyle, "日期时间", 2);
					POIUtils.setCell(hssfRow1, topCellStyle, "IP", 3);
					POIUtils.setCell(hssfRow1, topCellStyle, "用户角色", 4);
					POIUtils.setCell(hssfRow1, topCellStyle, "用户", 5);
					POIUtils.setCell(hssfRow1, topCellStyle, "操作", 6);

					// 设置每行的数据
					for (int i = 0; i < pager.getResultList().size(); i++) {
						RpcLog rpcLog = (RpcLog) pager.getResultList().get(i);
						SXSSFRow row = sheet.createRow((i + 1));

						POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
						POIUtils.setCell(row, alignCellStyle, DateUtil.DateToString(rpcLog.getCallTime()), 2);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getIp(), 3);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getRoleName(), 4);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getCreateUser(), 5);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getTodo(), 6);

					}
				}

				// 操作日志
				if (type == 2) {
					// 设置列宽
					POIUtils.setColumnWidth(sheet, new Integer[] { 5, 15, 15, 20, 15 });

					POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
					POIUtils.setCell(hssfRow1, topCellStyle, "日期时间", 2);
					POIUtils.setCell(hssfRow1, topCellStyle, "用户", 3);
					POIUtils.setCell(hssfRow1, topCellStyle, "内容", 4);
					POIUtils.setCell(hssfRow1, topCellStyle, "模块", 5);

					// 设置每行的数据
					for (int i = 0; i < pager.getResultList().size(); i++) {
						RpcLog rpcLog = (RpcLog) pager.getResultList().get(i);
						SXSSFRow row = sheet.createRow((i + 1));

						POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
						POIUtils.setCell(row, alignCellStyle, DateUtil.DateToString(rpcLog.getCallTime()), 2);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getUserName(), 3);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getTodo(), 4);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getModule(), 5);
					}
				}

				// 运行日志
				if (type == 3) {
					// 设置列宽
					POIUtils.setColumnWidth(sheet, new Integer[] { 5, 15, 40, 15 });

					POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
					POIUtils.setCell(hssfRow1, topCellStyle, "日期时间", 2);
					POIUtils.setCell(hssfRow1, topCellStyle, "内容", 3);
					POIUtils.setCell(hssfRow1, topCellStyle, "类型", 4);

					// 设置每行的数据
					for (int i = 0; i < pager.getResultList().size(); i++) {
						RpcLog rpcLog = (RpcLog) pager.getResultList().get(i);
						SXSSFRow row = sheet.createRow((i + 1));

						POIUtils.setCell(row, alignCellStyle, (i + 1) + "", 1);
						POIUtils.setCell(row, alignCellStyle, DateUtil.DateToString(rpcLog.getCallTime()), 2);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getUserName(), 3);
						POIUtils.setCell(row, leftCellStyle, rpcLog.getErrorMsg(), 4);
						POIUtils.setCell(row, alignCellStyle, rpcLog.getTodo(), 5);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workbook;
	}

	@Override
	public SXSSFWorkbook exportSearchInfoToExcel(Integer capType, @SuppressWarnings("rawtypes") List list)
			throws Exception {

		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		// 创建excel表单
		SXSSFSheet sheet = workbook.createSheet("表格1");

		// 设置表头样式
		Font topFont = POIUtils.getFont(workbook, "宋体", true, 11);
		CellStyle topCellStyle = POIUtils.getCellStyle(workbook, topFont, 2, 2, "1111");

		// 画图的顶级管理器，一个sheet只能获取一个
		SXSSFDrawing patriarch = sheet.createDrawingPatriarch();

		// 设置样式
		Font font = POIUtils.getFont(workbook, "宋体", false, 11);
		CellStyle alignCellStyle = POIUtils.getCellStyle(workbook, font, 2, 2, "1111");
		CellStyle leftCellStyle = POIUtils.getCellStyle(workbook, font, 1, 2, "1111");
		// 设置文本自动换行
		alignCellStyle.setWrapText(true);
		leftCellStyle.setWrapText(true);

		// 第一行
		SXSSFRow hssfRow1 = sheet.createRow(0);
		String folder = "pictures/";
		switch (capType) {
		case 1:
			capPeopleToExcel(list, workbook, sheet, topCellStyle, patriarch, alignCellStyle, hssfRow1, folder);
			break;
		case 3:
			capMotorToExcel(list, workbook, sheet, topCellStyle, patriarch, alignCellStyle, hssfRow1, folder);
			break;
		case 4:
			capNonmotorToExcel(list, workbook, sheet, topCellStyle, patriarch, alignCellStyle, hssfRow1, folder);
			break;
		default:
			break;
		}
		return workbook;

	}

	private void capPeopleToExcel(@SuppressWarnings("rawtypes") List list, SXSSFWorkbook workbook, SXSSFSheet sheet,
			CellStyle topCellStyle, SXSSFDrawing patriarch, CellStyle alignCellStyle, SXSSFRow hssfRow1, String folder)
			throws Exception {
		POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
		POIUtils.setCell(hssfRow1, topCellStyle, "图片（原图）", 2);
		POIUtils.setCell(hssfRow1, topCellStyle, "图片（场景图）", 3);
		POIUtils.setCell(hssfRow1, topCellStyle, "分类", 4);
		POIUtils.setCell(hssfRow1, topCellStyle, "通道", 5);
		POIUtils.setCell(hssfRow1, topCellStyle, "地点", 6);
		POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", 7);
		POIUtils.setCell(hssfRow1, topCellStyle, "性别", 8);
		POIUtils.setCell(hssfRow1, topCellStyle, "年龄", 9);
		POIUtils.setCell(hssfRow1, topCellStyle, "朝向", 10);
		POIUtils.setCell(hssfRow1, topCellStyle, "背包", 11);
		POIUtils.setCell(hssfRow1, topCellStyle, "运动状态", 12);
		POIUtils.setCell(hssfRow1, topCellStyle, "拎东西", 13);
		POIUtils.setCell(hssfRow1, topCellStyle, "帽子", 14);
		POIUtils.setCell(hssfRow1, topCellStyle, "眼镜", 15);
		POIUtils.setCell(hssfRow1, topCellStyle, "口罩", 16);
		POIUtils.setCell(hssfRow1, topCellStyle, "上身颜色", 17);
		POIUtils.setCell(hssfRow1, topCellStyle, "上身类型", 18);
		POIUtils.setCell(hssfRow1, topCellStyle, "上身纹理", 19);
		POIUtils.setCell(hssfRow1, topCellStyle, "下身颜色", 20);
		POIUtils.setCell(hssfRow1, topCellStyle, "下身类型", 21);
		POIUtils.setCell(hssfRow1, topCellStyle, "下身纹理", 22);

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				SXSSFRow row = sheet.createRow((i + 1));
				row.setHeight((short) 1900);
				CapResp bean = (CapResp) list.get(i);
				POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
				POIUtils.setCell(row, alignCellStyle, "行人", 4);
				POIUtils.setCell(row, alignCellStyle, bean.getChannel().getChannelName(), 5);
				POIUtils.setCell(row, alignCellStyle, bean.getChannel().getChannelArea(), 6);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCapTimeStr(), 7);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getGenderCodeTag(), 8);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getAgeTag(), 9);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getOrientationTag(), 10);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getBagStyleTag(), 11);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getMotionTag(), 12);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getBigBagStyleTag(), 13);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCapTag(), 14);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getGlassTag(), 15);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getRespiratorTag(), 16);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCoatColorTag(), 17);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCoatLengthTag(), 18);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getCoatTextureTag(), 19);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getTrousersColorTag(), 20);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getTrousersLenTag(), 21);
				POIUtils.setCell(row, alignCellStyle, bean.getCapPeople().getTrousersTextureTag(), 22);
				if (StringUtils.isNotEmpty(bean.getCapPeople().getCapUrl() + "")) {
					String suffix = bean.getCapPeople().getCapUrl()
							.substring(bean.getCapPeople().getCapUrl().lastIndexOf("."));
					POIUtils.setLinkCell(workbook, row, alignCellStyle, (i + 1) + "-1" + suffix,
							folder + (i + 1) + "-1" + suffix, 2);
				}
				if (StringUtils.isNotEmpty(bean.getCapPeople().getSeceneUrl() + "")) {
					String suffix = bean.getCapPeople().getSeceneUrl()
							.substring(bean.getCapPeople().getSeceneUrl().lastIndexOf("."));
					POIUtils.setLinkCell(workbook, row, alignCellStyle, (i + 1) + "-2" + suffix,
							folder + (i + 1) + "-2" + suffix, 3);
				}
			}
		}

	}

	private void capMotorToExcel(@SuppressWarnings("rawtypes") List list, SXSSFWorkbook workbook, SXSSFSheet sheet,
			CellStyle topCellStyle, SXSSFDrawing patriarch, CellStyle alignCellStyle, SXSSFRow hssfRow1, String folder)
			throws Exception {
		POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
		POIUtils.setCell(hssfRow1, topCellStyle, "图片（原图）", 2);
		POIUtils.setCell(hssfRow1, topCellStyle, "图片（场景图）", 3);
		POIUtils.setCell(hssfRow1, topCellStyle, "分类", 4);
		POIUtils.setCell(hssfRow1, topCellStyle, "通道", 5);
		POIUtils.setCell(hssfRow1, topCellStyle, "地点", 6);
		POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", 7);
		POIUtils.setCell(hssfRow1, topCellStyle, "车牌号", 8);
		POIUtils.setCell(hssfRow1, topCellStyle, "车辆颜色", 9);
		POIUtils.setCell(hssfRow1, topCellStyle, "朝向", 10);
		POIUtils.setCell(hssfRow1, topCellStyle, "车牌颜色", 11);
		POIUtils.setCell(hssfRow1, topCellStyle, "车型", 12);
		POIUtils.setCell(hssfRow1, topCellStyle, "品牌", 13);
		POIUtils.setCell(hssfRow1, topCellStyle, "子品牌", 14);
		POIUtils.setCell(hssfRow1, topCellStyle, "年款", 15);
		POIUtils.setCell(hssfRow1, topCellStyle, "年检标", 16);
		POIUtils.setCell(hssfRow1, topCellStyle, "纸巾盒", 17);
		POIUtils.setCell(hssfRow1, topCellStyle, "挂饰", 18);
		POIUtils.setCell(hssfRow1, topCellStyle, "遮阳板", 19);
		POIUtils.setCell(hssfRow1, topCellStyle, "主驾驶系安全带", 20);
		POIUtils.setCell(hssfRow1, topCellStyle, "副驾驶系安全带", 21);
		POIUtils.setCell(hssfRow1, topCellStyle, "主驾驶打手机", 22);

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				SXSSFRow row = sheet.createRow((i + 1));
				row.setHeight((short) 1900);
				CapResp bean = (CapResp) list.get(i);
				POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
				POIUtils.setCell(row, alignCellStyle, "机动车", 4);
				POIUtils.setCell(row, alignCellStyle, bean.getChannel().getChannelName(), 5);
				POIUtils.setCell(row, alignCellStyle, bean.getChannel().getChannelArea(), 6);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getCapTimeStr(), 7);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getPlateNo(), 8);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleColorTag(), 9);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getOrientationTag(), 10);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getPlateColorTag(), 11);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleClassTag(), 12);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleBrandTag(), 13);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleModelTag(), 14);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleStylesTag(), 15);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleMarkerMotTag(), 16);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleMarkerTissueboxTag(), 17);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getVehicleMarkerPendantTag(), 18);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getSunvisorTag(), 19);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getSafetyBeltTag(), 20);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getSafetyBeltCopilotTag(), 21);
				POIUtils.setCell(row, alignCellStyle, bean.getMotorVehicle().getCallingTag(), 22);
				if (StringUtils.isNotEmpty(bean.getMotorVehicle().getCapUrl() + "")) {
					String suffix = bean.getMotorVehicle().getCapUrl()
							.substring(bean.getMotorVehicle().getCapUrl().lastIndexOf("."));
					POIUtils.setLinkCell(workbook, row, alignCellStyle, (i + 1) + "-1" + suffix,
							folder + (i + 1) + "-1" + suffix, 2);
				}
				if (StringUtils.isNotEmpty(bean.getMotorVehicle().getSeceneUrl() + "")) {
					String suffix = bean.getMotorVehicle().getSeceneUrl()
							.substring(bean.getMotorVehicle().getSeceneUrl().lastIndexOf("."));
					POIUtils.setLinkCell(workbook, row, alignCellStyle, (i + 1) + "-2" + suffix,
							folder + (i + 1) + "-2" + suffix, 3);
				}
			}
		}
	}

	private void capNonmotorToExcel(@SuppressWarnings("rawtypes") List list, SXSSFWorkbook workbook, SXSSFSheet sheet,
			CellStyle topCellStyle, SXSSFDrawing patriarch, CellStyle alignCellStyle, SXSSFRow hssfRow1, String folder)
			throws Exception {
		POIUtils.setCell(hssfRow1, topCellStyle, "序号", 1);
		POIUtils.setCell(hssfRow1, topCellStyle, "图片（原图）", 2);
		POIUtils.setCell(hssfRow1, topCellStyle, "图片（场景图）", 3);
		POIUtils.setCell(hssfRow1, topCellStyle, "分类", 4);
		POIUtils.setCell(hssfRow1, topCellStyle, "通道", 5);
		POIUtils.setCell(hssfRow1, topCellStyle, "地点", 6);
		POIUtils.setCell(hssfRow1, topCellStyle, "抓拍时间", 7);
		POIUtils.setCell(hssfRow1, topCellStyle, "车型", 8);
		POIUtils.setCell(hssfRow1, topCellStyle, "朝向", 9);
		POIUtils.setCell(hssfRow1, topCellStyle, "车辆颜色", 10);
		POIUtils.setCell(hssfRow1, topCellStyle, "运动状态", 11);
		POIUtils.setCell(hssfRow1, topCellStyle, "年龄", 12);
		POIUtils.setCell(hssfRow1, topCellStyle, "性别", 13);
		POIUtils.setCell(hssfRow1, topCellStyle, "帽子", 14);
		POIUtils.setCell(hssfRow1, topCellStyle, "上身颜色", 15);
		POIUtils.setCell(hssfRow1, topCellStyle, "上身类型", 16);
		POIUtils.setCell(hssfRow1, topCellStyle, "眼镜", 17);
		POIUtils.setCell(hssfRow1, topCellStyle, "口罩", 18);

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				SXSSFRow row = sheet.createRow((i + 1));
				row.setHeight((short) 1900);
				CapResp bean = (CapResp) list.get(i);
				POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
				POIUtils.setCell(row, alignCellStyle, "非机动车", 4);
				POIUtils.setCell(row, alignCellStyle, bean.getChannel().getChannelName(), 5);
				POIUtils.setCell(row, alignCellStyle, bean.getChannel().getChannelArea(), 6);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCapTimeStr(), 7);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getVehicleClassTag(), 8);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getOrientationTag(), 9);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getVehicleColorTag(), 10);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getMotionTag(), 11);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getAgeTag(), 12);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getGenderCodeTag(), 13);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCapTag(), 14);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCoatColorTag(), 15);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getCoatLengthTag(), 16);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getGlassTag(), 17);
				POIUtils.setCell(row, alignCellStyle, bean.getCapNonmotor().getRespiratorTag(), 18);

				if (StringUtils.isNotEmpty(bean.getCapNonmotor().getCapUrl() + "")) {
					String suffix = bean.getCapNonmotor().getCapUrl()
							.substring(bean.getCapNonmotor().getCapUrl().lastIndexOf("."));
					POIUtils.setLinkCell(workbook, row, alignCellStyle, (i + 1) + "-1" + suffix,
							folder + (i + 1) + "-1" + suffix, 2);
				}
				if (StringUtils.isNotEmpty(bean.getCapNonmotor().getSeceneUrl() + "")) {
					String suffix = bean.getCapNonmotor().getSeceneUrl()
							.substring(bean.getCapNonmotor().getSeceneUrl().lastIndexOf("."));
					POIUtils.setLinkCell(workbook, row, alignCellStyle, (i + 1) + "-2" + suffix,
							folder + (i + 1) + "-2" + suffix, 3);
				}

				// 写图片
//				POIUtils.setCell(row, alignCellStyle, "", 2);
//				POIUtils.setCell(row, alignCellStyle, "", 3);
//				BufferedImage objImage = null;
//				String objFileType = null;
//				BufferedImage capImage = null;
//				String capFileType = null;
//				if (StringUtils.isNotEmpty(bean.getCapNonmotor().getCapUrl() + "")) {
////					capImage = ImgErToFileUtil.getImageFromNetByUrl(bean.getCapNonmotor().getCapUrl() + "");
////					capFileType = bean.getCapNonmotor().getCapUrl().substring(
////							bean.getCapNonmotor().getCapUrl().lastIndexOf(".") + 1,
////							bean.getCapNonmotor().getCapUrl().length());
//					String suffix = bean.getCapNonmotor().getCapUrl()
//							.substring(bean.getCapNonmotor().getCapUrl().lastIndexOf("."));
//					POIUtils.setLinkCell(workbook, row, alignCellStyle, (i + 1) + "-1" + suffix,
//							(i + 1) + "-1" + suffix, 2);
//				}
//				if (StringUtils.isNotEmpty(bean.getCapNonmotor().getCapUrl() + "")) {
////					objImage = ImgErToFileUtil.getImageFromNetByUrl(bean.getCapNonmotor().getSeceneUrl() + "");
////					objFileType = bean.getCapNonmotor().getSeceneUrl().substring(
////							bean.getCapNonmotor().getSeceneUrl().lastIndexOf(".") + 1,
////							bean.getCapNonmotor().getSeceneUrl().length());
//				}
//				POIUtils.setPicture(workbook, patriarch, capImage, capFileType, (i + 2), 2);// 抓拍图
//				POIUtils.setPicture(workbook, patriarch, objImage, objFileType, (i + 2), 3);// 模板图
			}
		}

	}

	@Override
	public SXSSFWorkbook exporttrafficCountInfoToExcel(List<Map<String, Object>> mList) {
		// 创建excel对象
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		// 创建excel表单
		SXSSFSheet sheet = workbook.createSheet("表格1");

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
		POIUtils.setCell(hssfRow1, topCellStyle, "通道", 2);
		POIUtils.setCell(hssfRow1, topCellStyle, "日期", 3);
		POIUtils.setCell(hssfRow1, topCellStyle, "行人", 4);
		POIUtils.setCell(hssfRow1, topCellStyle, "轿车", 5);
		POIUtils.setCell(hssfRow1, topCellStyle, "面包车", 6);
		POIUtils.setCell(hssfRow1, topCellStyle, "越野车（SUV）", 7);
		POIUtils.setCell(hssfRow1, topCellStyle, "商务车（MPV）", 8);
		POIUtils.setCell(hssfRow1, topCellStyle, "皮卡", 9);
		POIUtils.setCell(hssfRow1, topCellStyle, "小型客车", 10);
		POIUtils.setCell(hssfRow1, topCellStyle, "中型客车", 11);
		POIUtils.setCell(hssfRow1, topCellStyle, "大型客车", 12);
		POIUtils.setCell(hssfRow1, topCellStyle, "微型货车", 13);
		POIUtils.setCell(hssfRow1, topCellStyle, "小型货车", 14);
		POIUtils.setCell(hssfRow1, topCellStyle, "中型货车", 15);
		POIUtils.setCell(hssfRow1, topCellStyle, "重型货车", 16);
		POIUtils.setCell(hssfRow1, topCellStyle, "二轮自行车", 17);
		POIUtils.setCell(hssfRow1, topCellStyle, "二轮电动车/摩托车", 18);
		POIUtils.setCell(hssfRow1, topCellStyle, "三轮摩托车（带棚）", 19);
		POIUtils.setCell(hssfRow1, topCellStyle, "三轮摩托车（车厢封闭）", 20);
		POIUtils.setCell(hssfRow1, topCellStyle, "三轮摩托车（无棚&不封闭）", 21);
		POIUtils.setCell(hssfRow1, topCellStyle, "其他", 22);
		POIUtils.setCell(hssfRow1, topCellStyle, "未知", 23);
		for (int i = 0; i < mList.size(); i++) {
			SXSSFRow row = sheet.createRow((i + 1));// 第n行
			Map<String, Object> map = mList.get(i);
			if (!map.isEmpty() && map.size() > 0) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("resultList" + "");
				String time = (String) map.get("time" + "");
				@SuppressWarnings("unchecked")
				List<Channel> channels = (List<Channel>) map.get("channels" + "");
				String names = "";
				if (channels != null) {
					List<String> channelNameList = channels.stream().map(a -> a.getChannelName())
							.collect(Collectors.toList());
					names = StringUtils.listToString(channelNameList);
				}
				row.setHeight((short) 1900);
				POIUtils.setCell(row, alignCellStyle, "" + (i + 1), 1);
				POIUtils.setCell(row, alignCellStyle, names.isEmpty() ? "" : names, 2);
				POIUtils.setCell(row, alignCellStyle, time, 3);
				if (list != null && list.size() > 0) {
					for (int j = 0; j < list.size(); j++) {
						String type = (String) list.get(j).get("type" + "");
						Integer sum = (Integer) list.get(j).get("count" + "");
						String count = String.valueOf(sum);
						switch (type) {
						case "行人":
							POIUtils.setCell(row, topCellStyle, count, 4);
							break;
						case "轿车":
							POIUtils.setCell(row, topCellStyle, count, 5);
							break;
						case "面包车":
							POIUtils.setCell(row, topCellStyle, count, 6);
							break;
						case "越野车（SUV）":
							POIUtils.setCell(row, topCellStyle, count, 7);
							break;
						case "商务车（MPV）":
							POIUtils.setCell(row, topCellStyle, count, 8);
							break;
						case "皮卡":
							POIUtils.setCell(row, topCellStyle, count, 9);
							break;
						case "小型客车":
							POIUtils.setCell(row, topCellStyle, count, 10);
							break;
						case "中型客车":
							POIUtils.setCell(row, topCellStyle, count, 11);
							break;
						case "大型客车":
							POIUtils.setCell(row, topCellStyle, count, 12);
							break;
						case "微型货车":
							POIUtils.setCell(row, topCellStyle, count, 13);
							break;
						case "小型货车":
							POIUtils.setCell(row, topCellStyle, count, 14);
							break;
						case "中型货车":
							POIUtils.setCell(row, topCellStyle, count, 15);
							break;
						case "重型货车":
							POIUtils.setCell(row, topCellStyle, count, 16);
							break;
						case "二轮电动车/摩托车":
							POIUtils.setCell(row, topCellStyle, count, 17);
							break;
						case "三轮摩托车（带棚）":
							POIUtils.setCell(row, topCellStyle, count, 18);
							break;
						case "三轮摩托车（车厢封闭）":
							POIUtils.setCell(row, topCellStyle, count, 19);
							break;
						case "三轮摩托车（无棚&不封闭）":
							POIUtils.setCell(row, topCellStyle, count, 20);
							break;
						case "二轮自行车":
							POIUtils.setCell(row, topCellStyle, count, 21);
							break;
						case "其他":
							POIUtils.setCell(row, topCellStyle, count, 22);
							break;
						case "未知":
							POIUtils.setCell(row, topCellStyle, count, 23);
							break;
						default:
							POIUtils.setCell(row, topCellStyle, "0", j + 4);
							break;
						}
					}
				} else {
					for (int i1 = 0; i1 < 20; i1++) {
						POIUtils.setCell(row, topCellStyle, "0", (i1 + 4));
					}
				}
			}
		}
		return workbook;
	}

}
