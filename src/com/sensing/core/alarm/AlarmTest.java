package com.sensing.core.alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.sensing.core.bean.Jobs;

public class AlarmTest {
	
	public static void main(String[] args) {
		long l1 = System.currentTimeMillis();
		boolean b = isWork(str2Date("2018-11-30 09:59:59"));
		long l2 = System.currentTimeMillis();
		System.out.println((l2-l1)+":"+b);
	}
	
	/**
	 * 是否在工作时间的判断
	 * @param currDate
	 * @return
	 * @author mingxingyu
	 * @date   2018年10月30日 下午1:54:02
	 */
	public static boolean isWork(Date currDate){
		
		Jobs jobs = new Jobs();
//		jobs.setBeginDate(str2Date("2018-10-10"));
//		jobs.setEndDate(str2Date("2018-11-10"));
		jobs.setBeginTime("10:00:00");
		jobs.setEndTime("13:59:59");
		
		Date sDate = null;
		Date eDate = null;
		if ( jobs.getBeginDate() != null && jobs.getEndDate() != null ) {
			sDate = jobs.getBeginDate();
			eDate = jobs.getEndDate();
		}else{
			String dstr = date2Str(currDate).substring(0,10)+" 00:00:00";
			Date currDate0 = str2Date(dstr);
			sDate = currDate0;
			eDate = currDate0;
		}
		
		long startTime = 0;
		long endTime = 0;
		String begin = jobs.getBeginTime();
		String end = jobs.getEndTime();
		String[] beginArr = begin.split(":");
		String[] endArr = end.split(":");
		
		startTime = (Integer.parseInt(beginArr[0])*3600+Integer.parseInt(beginArr[1])*60+Integer.parseInt(beginArr[2]))*1000 + sDate.getTime();
		endTime = (Integer.parseInt(endArr[0])*3600+Integer.parseInt(endArr[1])*60+Integer.parseInt(endArr[2]))*1000 + eDate.getTime();
		
		if ( currDate.getTime() >= startTime && currDate.getTime() <= endTime ) {
			return true;
		}else{
			return false;
		}
	}
	
	public static String date2Str(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	public static Date str2Date(String dateStr) {
		Date date = null;
		
		try {
			if ( dateStr.length() == 19  ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date= sdf.parse(dateStr);
			}
			
			if ( dateStr.length() == 10  ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date= sdf.parse(dateStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
}
