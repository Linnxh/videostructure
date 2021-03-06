package com.sensing.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("all")
public class FileUnZip {

	/**
	 * @Description: 将压缩文件解压到指定目录，如果指定目录已存在，先删除再解压
	 * @param sourceFile	原压缩文件
	 * @param toFolder	
	 * @throws Exception    
	 * @return void    
	 * @author dongsl
	 * @Date 2017年9月12日上午9:36:59
	 */
	public static void zipToFile(String sourceFile, String toFolder) throws Exception {
		//判断指定目录是否存在，如果存在，先删除
		File file = new File(toFolder);
		if(file.exists()){
			FileUtil.deleteDir(file);
		}
		
        String toDisk = toFolder;// 接收解压后的存放路径
        Charset gbk = Charset.forName("GBK");
        ZipFile zfile = new  ZipFile(sourceFile, gbk);
        //ZipFile zfile = new ZipFile(sourceFile);// 连接待解压文件
        Enumeration zList = zfile.entries();// 得到zip包里的所有元素
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                continue;
            }
            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = new BufferedOutputStream(new FileOutputStream(getRealFileName(toDisk, ze.getName())));
                inputStream = new BufferedInputStream(zfile.getInputStream(ze));
                int readLen = 0;
                while ((readLen = inputStream.read(buf, 0, 1024)) != -1) {
                    outputStream.write(buf, 0, readLen);
                }
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                throw new IOException("解压失败：" + e.toString());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {

                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                inputStream = null;
                outputStream = null;
            }

        }
        zfile.close();
    }
	
	private static File getRealFileName(String zippath, String absFileName) {
        // log.info("文件名："+absFileName);
        String[] dirs = absFileName.split("/", absFileName.length());
        File ret = new File(zippath);// 创建文件对象
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {// 检测文件是否存在
            ret.mkdirs();// 创建此抽象路径名指定的目录
        }
        ret = new File(ret, dirs[dirs.length - 1]);// 根据 ret 抽象路径名和 child
                                                    // 路径名字符串创建一个新 File 实例
        return ret;
    }
}
