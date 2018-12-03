package com.sensing.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.sensing.core.bean.TemplateObjMotor;

public class ReadExcel {
	// 总行数
	private int totalRows = 0;
	// 总条数
	private int totalCells = 0;
	// 错误信息接收器
	private String errorMsg;

	// 构造方法
	public ReadExcel() {
	}

	// 获取总行数
	public int getTotalRows() {
		return totalRows;
	}

	// 获取总列数
	public int getTotalCells() {
		return totalCells;
	}

	// 获取错误信息
	public String getErrorInfo() {
		return errorMsg;
	}

	/**
	 * 读EXCEL文件，获取信息集合
	 * 
	 * @param fielName
	 * @return
	 */
	@SuppressWarnings("resource")
	public List<TemplateObjMotor> getExcelInfo(MultipartFile mFile) {
		String fileName = mFile.getOriginalFilename();// 获取文件名
		List<TemplateObjMotor> list = null;
		try {
			if (!validateExcel(fileName)) {// 验证文件名是否合格
				return null;
			}
			boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
			if (isExcel2007(fileName)) {
				isExcel2003 = false;
			}
//			List<TemplateObjMotor> list = createExcel(mFile.getInputStream(), isExcel2003);
			Workbook wb = null;
			if (isExcel2003) {// 当excel是2003时,创建excel2003
				wb = new HSSFWorkbook(mFile.getInputStream());
			} else {// 当excel是2007时,创建excel2007
				wb = new XSSFWorkbook(mFile.getInputStream());
			}
			Sheet sheet = wb.getSheet("Sheet1");
			int rows = sheet.getLastRowNum();// 一共有多少行
			if (rows == 0) {
//				throw new Exception("请填写数据");
				return null;
			}
			list = readExcelValue(wb);// 读取Excel里面客户的信息
//				return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 根据excel里面的内容读取客户信息
	 * 
	 * @param is          输入流
	 * @param isExcel2003 excel是2003还是2007版本
	 * @return
	 * @throws IOException
	 */
	public List<TemplateObjMotor> createExcel(InputStream is, boolean isExcel2003) {
		try {
			Workbook wb = null;
			if (isExcel2003) {// 当excel是2003时,创建excel2003
				wb = new HSSFWorkbook(is);
			} else {// 当excel是2007时,创建excel2007
				wb = new XSSFWorkbook(is);
			}
			List<TemplateObjMotor> list = readExcelValue(wb);// 读取Excel里面客户的信息
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取Excel模版信息
	 * 
	 */
	private List<TemplateObjMotor> readExcelValue(Workbook wb) {
		// 得到第一个shell
		Sheet sheet = wb.getSheetAt(0);
		// 得到Excel的行数
		this.totalRows = sheet.getPhysicalNumberOfRows();
		// 得到Excel的列数(前提是有行数)
		if (totalRows > 1 && sheet.getRow(0) != null) {
			this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
		}
		List<TemplateObjMotor> motorList = new ArrayList<TemplateObjMotor>();
		// 循环Excel行数
		try {
			for (int r = 1; r < totalRows; r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				// 循环Excel的列
				TemplateObjMotor tom = new TemplateObjMotor();
				for (int c = 0; c < this.totalCells; c++) {
					Cell cell = row.getCell(c);
					if (null != cell && !cell.equals("")) {
						if (c == 2) {
							tom.setPlateNo(cell.getStringCellValue());
						} else if (c == 3) {
							String vehicleColor = String.valueOf(cell.getNumericCellValue());
							String s = vehicleColor.substring(0,
									vehicleColor.length() - 2 > 0 ? vehicleColor.length() - 2 : 1);
							tom.setVehicleColor(Short.parseShort(s));
						} else if (c == 4) {
							String vehicleClass = String.valueOf(cell.getNumericCellValue());
							String s = vehicleClass.substring(0,
									vehicleClass.length() - 2 > 0 ? vehicleClass.length() - 2 : 1);
							tom.setVehicleClass(Short.parseShort(s));
						} else if (c == 5) {
							String plateColor = String.valueOf(cell.getNumericCellValue());
							String s = plateColor.substring(0,
									plateColor.length() - 2 > 0 ? plateColor.length() - 2 : 1);
							tom.setPlateColor(Short.parseShort(s));
						} else if (c == 6) {
							String vehicleBrandTag = cell.getStringCellValue();
							tom.setVehicleBrandTag(vehicleBrandTag);
						} else if (c == 7) {
							String ownerName = cell.getStringCellValue();
							tom.setOwnerName(ownerName);
						} else if (c == 8) {
							String ownerTel = String.valueOf(cell.getNumericCellValue());
							String s = ownerTel.substring(0, ownerTel.length() - 2 > 0 ? ownerTel.length() - 2 : 1);
							tom.setOwnerTel(s);
						} else if (c == 9) {
							String ownerIdno = String.valueOf(cell.getNumericCellValue());
							String s = ownerIdno.substring(0, ownerIdno.length() - 2 > 0 ? ownerIdno.length() - 2 : 1);
							tom.setOwnerIdno(s);
						} else if (c == 10) {
							String ownerAddr = cell.getStringCellValue();
							tom.setOwnerAddr(ownerAddr);
						} else if (c == 11) {
							String memo = cell.getStringCellValue();
							tom.setMemo(memo);
						} else if (c == 12) {
							String templatedbName = cell.getStringCellValue();
							tom.setTemplatedbName(templatedbName);
						}
					}
				}
//				tom.setTemplatedbId(6);
				motorList.add(tom);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return motorList;
	}

	/**
	 * 验证EXCEL文件
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean validateExcel(String filePath) {
		if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
			errorMsg = "文件名不是excel格式";
			return false;
		}
		return true;
	}

	// @描述：是否是2003的excel，返回true是2003
	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	// @描述：是否是2007的excel，返回true是2007
	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}

}
