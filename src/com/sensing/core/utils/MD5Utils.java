/**
 * <p>Title: MD5Utils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: www.sensingtech.com</p>
 * @author mingxingyu
 * @date 2017年9月5日下午5:55:33
 * @version 1.0
 */
package com.sensing.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

/**
 * <p>Title: MD5Utils</p>
 * <p>Description: 获取md5值</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2017年9月5日下午5:55:33
 * @version 1.0
 */
public class MD5Utils {
	
	/**
	 * 获取图片数组的md5值
	 * @param imagebyte
	 * @return
	 * @author mingxingyu
	 * @date   2017年9月5日下午5:56:02
	 */
	public static String getImageMD5(byte[] imagebyte) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(imagebyte, 0, imagebyte.length);
			BigInteger bigInt = new BigInteger(1, md.digest());
			return bigInt.toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getFileMD5(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			fis.close();
			BigInteger bigInt = new BigInteger(1, md.digest());
			return bigInt.toString(16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String EncoderByMd5(String str) throws NoSuchAlgorithmException,UnsupportedEncodingException{
		MessageDigest md5=MessageDigest.getInstance("MD5");   
		BASE64Encoder base64en = new BASE64Encoder();    
        String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));    
        return newstr;  
    }     
	
	
	/**判断用户密码是否正确   
	 *  newpasswd 用户输入的密码  
	 *  *oldpasswd 正确密码*/  
	public static boolean checkpassword(String newpasswd,String oldpasswd) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		if(EncoderByMd5(newpasswd).equals(oldpasswd))      
		{	return true;    
		}else{      
			return false;  
		}
	}
	
	public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
	/**
	   *  静态工具类：MD5加密（32位加密）
	   * @param plainText
	   * @return （小写）的MD5加密后的字符串。
	   */
	  public static String getMd5(String plainText) {
	    try {
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      md.update(plainText.getBytes());
	      byte b[] = md.digest();
	      int i;
	      StringBuffer buf = new StringBuffer("");
	      for (int offset = 0; offset < b.length; offset++) {
	        i = b[offset];
	        if (i < 0)
	          i += 256;
	        if (i < 16)
	          buf.append("0");
	        buf.append(Integer.toHexString(i));
	      }
	      // 32位加密
	      return buf.toString();
	      // 16位的加密
//	       return buf.toString().substring(8, 24);
	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	      return null;
	    }

	  }  
	public static void main(String[] args) {
//		System.out.println(MD5Utils.MD5("111111"));
//		System.out.println(MD5("111111"));
	}
}
