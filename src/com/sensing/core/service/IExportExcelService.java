package com.sensing.core.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.sensing.core.utils.Pager;

public interface IExportExcelService {

	SXSSFWorkbook exportSearchInfoToExcel(Integer capType, List list) throws Exception;

	SXSSFWorkbook exporttrafficCountInfoToExcel(List<Map<String, Object>> mList);

	/**
	 * 日志信息导出到excel
	 * @param pagerParams 查询条件
	 * @return
	 * @author mingxingyu
	 * @date   2018年11月16日 下午4:50:06
	 */
	public SXSSFWorkbook logExportToExcel(Pager pagerParams);
}
