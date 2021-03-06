package com.sensing.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
* @ClassName: ExcelUtil
* @Description: (这里用一句话描述这个类的作用)
* ExcelUtil工具类实现功能:<BR>
* 导出时传入list<T>,即可实现导出为一个excel,其中每个对象Ｔ为Excel中的一条记录.<BR>
* 导入时读取excel,得到的结果是一个list<T>.T是自己定义的对象.<BR>
* 需要导出的实体对象只需简单配置注解就能实现灵活导出,通过注解您可以方便实现下面功能:<BR>
* 1.实体属性配置了注解就能导出到excel中,每个属性都对应一列.<BR>
* 2.列名称可以通过注解配置.<BR>
* 3.导出到哪一列可以通过注解配置.<BR>
* 4.鼠标移动到该列时提示信息可以通过注解配置.<BR>
* 5.用注解设置只能下拉选择不能随意填写功能.<BR>
* 6.用注解设置是否只导出标题而不导出内容,这在导出内容作为模板以供用户填写时比较实用.<BR>
 */
public class ExcelHelper<T> {
    /**
     * 日志变量
     */
    protected static final Logger                    logger             = LoggerFactory.getLogger(ExcelHelper.class);

    Class<T> clazz;
    
    public ExcelHelper(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ExcelHelper() {
        
        // Non parametric construction method

    }

    /**
     * 
    * @Title: importExcel
    * @Description: (导入ExcelData)
    * @param sheetName
    * @param file
    * @return List<T>    返回类型
    * @throws
    * @date 2014年12月12日 上午11:24:48
     */
    public List<T> importExcel(String sheetName, MultipartFile file) {
        List<T> list = new ArrayList<>();
        InputStream fis = null;
        Workbook book = null;
        try {
            String filename = file.getOriginalFilename();
            if (filename == null || "".equals(filename)) {
                return null;
            }
            if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
                throw new Exception("传入的文件不是excel");
            }
            fis = file.getInputStream();
            if (filename.endsWith("xlsx")) {
                book = new XSSFWorkbook(fis);
            } else {
                book = new HSSFWorkbook(fis);
            }
            Sheet sheet = null;
            if (!"".equals(sheetName.trim())) {
                sheet = book.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
            }
            if (sheet == null) {
                sheet = book.getSheetAt(0);// 如果传入的sheet名不存在则默认指向第1个sheet.
            }
            int rows = sheet.getLastRowNum();// 得到数据的行数
            if (rows > 0) {// 有数据时才处理
                Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
                Map<Integer, Field> fieldsMap = new HashMap<>();// 定义一个map用于存放列的序号和field.
                Map<String, Field> fieldsMapOther = new HashMap<>();// 定义一个map用于存放列的序号和field.
                for (Field field : allFields) {
                    // 将有注解的field存放到map中.
                    if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                        ExcelVOAttribute attr = field
                                .getAnnotation(ExcelVOAttribute.class);
                        int col = getExcelCol(attr.column());// 获得列号
                        field.setAccessible(true);// 设置类的私有字段属性可访问.
                        fieldsMap.put(col, field);
                        if (!StringUtils.isEmpty(attr.dbdata_column()) && clazz.getGenericSuperclass() != null) {
                            Class superClass = clazz.getSuperclass();// 父类
                            Field fatherField = superClass.getDeclaredField(attr.dbdata_column());
                            fatherField.setAccessible(true);// 设置类的私有字段属性可访问.
                            fieldsMapOther.put(attr.dbdata_column(), fatherField);
                        }
                    }
                }
                boolean judgeFirst = true;
                //迭代行
                for (Iterator<Row> iter = sheet.rowIterator(); iter.hasNext();) {
                    Row row = iter.next();
                    if (judgeFirst) {
                        // 从第2行开始取数据,默认第一行是表头.
                        judgeFirst = false;
                        continue;
                    }
                    T entity = null;
                    //迭代列
                    int columnIndex = 0;
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i);
                        String content = "";
                        if (cell != null) {
                            if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC || cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
                            	DecimalFormat decimalFormat = new DecimalFormat();
                            	Double db = cell.getNumericCellValue();
                            	String num = decimalFormat.format(db);
                            	num = num.replace(",","");
//                                String num = Double.toString(cell.getNumericCellValue());
                                String num1 = num.substring(num.lastIndexOf('.') + 1);
                                if (Integer.valueOf(num1) == 0) {
                                    num = num.substring(0, num.lastIndexOf('.'));
                                }
                                content = num;
                            } else {
                                content = cell.getStringCellValue();
                            }
                        }
                        entity = entity == null ? clazz.newInstance() : entity;// 如果不存在实例则新建.
                        Field field = fieldsMap.get(columnIndex);// 从map中得到对应列的field.
                        columnIndex++;
                        if (field == null) {
                            continue;
                        }
                        // 将有注解的field存放到map中.
                        if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                            ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                            String dataConlum = attr.dbdata_column();
                            if (!StringUtils.isEmpty(dataConlum)) {
                                Field faField = fieldsMapOther.get(dataConlum);
                                String[] strs = content.split("-");
                                faField.set(entity, strs[1]);
                            }
                        }
                        // 取得类型,并根据对象类型设置值.
                        Class<?> fieldType = field.getType();
                        if ((Integer.TYPE == fieldType)
                                || (Integer.class == fieldType)) {
                            field.set(entity, Integer.parseInt(content));
                        } else if (String.class == fieldType) {
                            field.set(entity, String.valueOf(content));
                        } else if ((Long.TYPE == fieldType)
                                || (Long.class == fieldType)) {
                            field.set(entity, Long.valueOf(content));
                        } else if ((Float.TYPE == fieldType)
                                || (Float.class == fieldType)) {
                            field.set(entity, Float.valueOf(content));
                        } else if ((Short.TYPE == fieldType)
                                || (Short.class == fieldType)) {
                            field.set(entity, Short.valueOf(content));
                        } else if ((Double.TYPE == fieldType)
                                || (Double.class == fieldType)) {
                            field.set(entity, Double.valueOf(content));
                        } else if (Character.TYPE == fieldType) {
                            if ((content != null) && (content.length() > 0)) {
                                field.set(entity, Character.valueOf(content.charAt(0)));
                            } else {
                                field.set(entity, content);
                            }
                        }
                    }
                    if (entity != null) {
                        list.add(entity);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.debug(e.getMessage(),e);
                }
            }
        }
        return list;
    }

    /**
     * 
    * @Title: exportExcel
    * @Description: (对list数据源将其里面的数据导入到excel表单)
    * @param list
    * @param sheetName
    * @param sheetSize
    *  @param output
    * @return boolean    返回类型
    * @throws
    * @date 2014年12月12日 上午11:25:30
     */
    public boolean exportExcel(List<T> list, String sheetName, int sheetSize, OutputStream output) {
        return this.exportExcel(list, sheetName, sheetSize, output, null);
    }

    /**
     * 
     *  <功能简述> <br/>
     *  <功能详细描述>
     * @param list
     * @param sheetName
     * @param sheetSize
     * @param output
     * @param setFormatColumn 设置单元格格式为文本
     * @return
     */
    public boolean exportExcel( List<T> list, String sheetName, int sheetSize,
            OutputStream output, Integer setFormatColumn ) {
        Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段
        List<Field> fields = new ArrayList<>();
        // 得到所有field并存放到一个list中.
        for (Field field : allFields) {
            if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                fields.add(field);
            }
        }
        XSSFWorkbook workbook=null;
        try{
            workbook = new XSSFWorkbook();// 产生工作薄对象
            // excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
            int size = sheetSize;
            if (sheetSize > 65536 || sheetSize < 1) {
                size = 65536;
            }
            double sheetNo = (double)(list.size() / size);// 取出一共有多少个sheet.
            for (int index = 0; index <= sheetNo; index++) {
            	XSSFSheet sheet = workbook.createSheet();// 产生工作表对象
                workbook.setSheetName(index, sheetName);// 设置工作表的名称.
                //workbook.setSheetName(index, sheetName + index);// 设置工作表的名称.
                //设置单元格格式
                if (setFormatColumn != null && setFormatColumn.intValue() > 0) {
                	XSSFCellStyle  style = workbook.createCellStyle();
                	XSSFDataFormat dataFormat = workbook.createDataFormat();
                    style.setDataFormat(dataFormat.getFormat("@"));
                    for (int i = 0; i < setFormatColumn.intValue(); i++) {
                        sheet.setDefaultColumnStyle(i, style);
                    }
                }
                XSSFRow row;
                XSSFCell cell;// 产生单元格
                row = sheet.createRow(0);// 产生一行
                // 写入各个字段的列头名称
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                    int col = getExcelCol(attr.column());// 获得列号
                    cell = row.createCell(col);// 创建列
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型
                    cell.setCellValue(attr.name());// 写入列名
                    if (StringUtils.isNotEmpty(attr.name())) {
                        int width = (attr.name().getBytes().length > 18 ? attr.name().getBytes().length : 18) * 180;
                        sheet.setColumnWidth(i, width);
                    } else {
                        sheet.autoSizeColumn(i);
                    }
                    // 如果设置了combo属性则本列只能选择不能输入
                    if (attr.combo().length > 0) {
                        setHSSFValidation(sheet, attr.combo(), 1, 100, col, col);// 这里默认设了2-101列只能选择不能输入.
                    }
                }
                int startNo = index * size;
                int endNo = Math.min(startNo + size, list.size());
                // 写入各条记录,每条记录对应excel表中的一行
                for (int i = startNo; i < endNo; i++) {
                    row = sheet.createRow(i + 1 - startNo);
                    T vo = list.get(i); // 得到导出对象.
                    for (int j = 0; j < fields.size(); j++) {
                        Field field = fields.get(j);// 获得field.
                        field.setAccessible(true);// 设置实体类私有属性可访问
                        ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                        try {
                            // 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                            if (attr.isExport()) {
                                cell = row.createCell(getExcelCol(attr.column()));// 创建cell
                                if (attr.isNo()) {
                                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                                    cell.setCellValue((double)(i + 1));// 如果数据存在就填入,不存在填入空格.
                                } else {
                                    // 如果数据存在就填入,不存在填入空格.
                                    String cellValue = field.get(vo) == null ? "" : String.valueOf(field.get(vo)).trim();
                                    if (!"".equals(cellValue)) {
                                        if (isNumericEx(cellValue)) {
                                            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                                            try {
                                                cell.setCellValue(Double.parseDouble(cellValue));
                                            } catch (Exception e) {
                                                cell.setCellValue(cellValue);
                                                logger.debug(e.getMessage(),e);
                                            }
                                        } else {
                                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                            cell.setCellValue(cellValue);
                                        }
                                    } else {
                                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                        cell.setCellValue("");
                                    }
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            logger.debug(e.getMessage(),e);
                        } catch (IllegalAccessException e) {
                            logger.debug(e.getMessage(),e);
                        }
                    }
                }
            }
            try {
                output.flush();
                workbook.write(output);
                output.close();
                return true;
            } catch (IOException e) {
                logger.debug(e.getMessage(),e);
                logger.debug("Output is closed ");
                return false;
            }
        }
        finally {
            try {
                workbook.close();
            } catch (IOException e) {
               logger.debug(e.getMessage(),e);
            }
        }
    }

    /**
     * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
     * 
     * @param col
     */
    private static int getExcelCol(String col) {
        String newCol = col.toUpperCase();
        // 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
        int count = -1;
        char[] cs = newCol.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            count += (cs[i] - 64) * Math.pow(26, (double)(cs.length - 1 - i));
        }
        return count;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     * 
     * @param sheet
     *            要设置的sheet.
     * @param textlist
     *            下拉框显示的内容
     * @param firstRow
     *            开始行
     * @param endRow
     *            结束行
     * @param firstCol
     *            开始列
     * @param endCol
     *            结束列
     * @return 设置好的sheet.
     */
    private static XSSFSheet setHSSFValidation(XSSFSheet sheet,
            String[] textlist, int firstRow, int endRow, int firstCol,
            int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint
                .createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation dataValidationList = new HSSFDataValidation(
                regions, constraint);
        sheet.addValidationData(dataValidationList);
        return sheet;
    }

    public void exportExcel(List<List<Object>> list, String sheetName,
            List<String> columnHeaderList, int sheetSize, OutputStream output) {
        HSSFWorkbook workbook=null;
        try{
            workbook= new HSSFWorkbook();// 产生工作薄对象
            // excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
            int size = sheetSize;
            if (sheetSize > 65536 || sheetSize < 1) {
                size = 65536;
            }
            double sheetNo = (double)(list.size() / size);// 取出一共有多少个sheet.
            for (int index = 0; index <= sheetNo; index++) {
                HSSFSheet sheet = workbook.createSheet();// 产生工作表对象
                workbook.setSheetName(index, sheetName);// 设置工作表的名称.
                HSSFRow row;
                HSSFCell cell;// 产生单元格
                row = sheet.createRow(0);// 产生一行
                // 写入各个字段的列头名称
                for (int i = 0; i < columnHeaderList.size(); i++) {
                    int col = i;// 获得列号
                    cell = row.createCell(col);// 创建列
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型
                    String cellName = columnHeaderList.get(i);
                    if (StringUtils.isNotEmpty(cellName)) {
                        int width = (cellName.getBytes().length > 18 ? cellName.getBytes().length : 18) * 180;
                        sheet.setColumnWidth(i, width);
                    } else {
                        sheet.autoSizeColumn(i);
                    }
                    cell.setCellValue(cellName);// 写入列名
                }
                int startNo = index * size;
                int endNo = Math.min(startNo + size, list.size());
                // 写入各条记录,每条记录对应excel表中的一行
                for (int i = startNo; i < endNo; i++) {
                    row = sheet.createRow(i + 1 - startNo);
                    for (int j = 0; j < columnHeaderList.size(); j++) {
                        try {
                            cell = row.createCell(j);// 创建cell
                            if (list.get(i).get(j) != null) {
                                if (isNumericEx(list.get(i).get(j).toString())) {
                                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                                    try {
                                        cell.setCellValue(Double.parseDouble(list.get(i).get(j).toString().trim()));
                                    } catch (Exception e) {
                                        cell.setCellValue(list.get(i).get(j).toString());
                                        logger.debug(e.getMessage(),e);
                                    }
                                } else {
                                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                    cell.setCellValue(list.get(i).get(j).toString());
                                }
                            } else {
                                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                cell.setCellValue("");
                            }
                        } catch (IllegalArgumentException e) {
                            logger.debug(e.getMessage(),e);
                        }
                    }
                }
            }
            try {
                output.flush();
                workbook.write(output);
                output.close();
            } catch (IOException e) {
                logger.debug("Output is closed ");
                logger.debug(e.getMessage(),e);
            }
        }
        finally{
            try {
                workbook.close();
            } catch (IOException e) {
                logger.debug(e.getMessage(),e);
            }
           
        }
    }

    private boolean isNumericEx(String str) {
        try {
            if (StringUtils.isNotEmpty(str)) {
                Double.parseDouble(str);
            }
            return true;
        } catch (Exception e) {
            logger.debug(e.getMessage(),e);
            return false;
        }
    }
}
