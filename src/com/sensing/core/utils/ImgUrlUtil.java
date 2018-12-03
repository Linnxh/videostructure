package com.sensing.core.utils;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



import sun.misc.BASE64Encoder;

import com.sensing.core.cacahes.ImgUrlCache;

import sun.net.www.protocol.https.Handler;

public class ImgUrlUtil {
	
	public static String getChannelUrl(String uuid){
		String url = ImgUrlCache.channelData.get(uuid);
		if(StringUtils.isEmptyOrNull(url) || url.equals("*")){
			url = Constants.SERVER_URL_PHOTO;
		}
		return url;
	}
	
	public static String getFtUrl(Integer uuid){
		String url = ImgUrlCache.ftData.get(uuid);
		if(StringUtils.isEmptyOrNull(url) || url.equals("*")){
			url = Constants.SERVER_URL_PHOTO;
		}
		return url;
	}
	
	public static void getImgFromUrl(String url){
		try{
			 URL urls = new URL(url);    
	         HttpURLConnection conn = (HttpURLConnection)urls.openConnection();    
	         conn.setRequestMethod("GET");    
	         conn.setConnectTimeout(5 * 1000);    
	         InputStream inStream = conn.getInputStream();//通过输入流获取图片数据    
	         byte[] btImg = readInputStream(inStream);//得到图片的二进制数据                  
	         BASE64Encoder encoder = new BASE64Encoder();  
	         String encode = encoder.encode(btImg);
	         String ss="data:image/jpeg;base64,";
	         String sss=ss+encode;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getImgStrFromUrl(String url){
		String str = "";
		try{
			//new一个URL对象  
	        URL urll = new URL(url);  
	        //打开链接  
	        HttpURLConnection conn = (HttpURLConnection)urll.openConnection();  
	        //设置请求方式为"GET"  
	        conn.setRequestMethod("GET");  
	        //超时响应时间为5秒  
	        conn.setConnectTimeout(5 * 1000);  
	        //通过输入流获取图片数据  
	        InputStream inStream = conn.getInputStream();//通过输入流获取图片数据    
	        byte[] btImg = readInputStream(inStream);//得到图片的二进制数据                  
	        BASE64Encoder encoder = new BASE64Encoder();  
	        String encode = encoder.encode(btImg);
	        String ss="data:image/jpeg;base64,";
	        str = ss+encode;
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	
	 public static byte[] readInputStream(InputStream inStream) throws Exception{    
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        while( (len=inStream.read(buffer)) != -1 ){    
            outStream.write(buffer, 0, len);    
        }    
        inStream.close();    
        return outStream.toByteArray();    
    }
	 
	 public static void main(String[] args) throws Exception {
		 getImgStrFromUrl("http://192.168.0.158:8500/image/group1/M00/00/06/wKgAnlta0aSIChlXAAAYEvq3AmUAAAEAQJxz8kAABgq413.jpg");
	}
	
	
}
