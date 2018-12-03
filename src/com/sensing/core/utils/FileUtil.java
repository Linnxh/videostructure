package com.sensing.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

	/**
	 * @Description: 遍历删除文件夹下的所有内容
	 * @param dir
	 * @return
	 * @return boolean
	 * @author dongsl
	 * @Date 2017年9月8日上午11:02:38
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static boolean isSameFile(String fileName1, String fileName2) {
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		try {
			fis1 = new FileInputStream(fileName1);
			fis2 = new FileInputStream(fileName2);

			int len1 = fis1.available();// 返回总的字节数
			int len2 = fis2.available();

			if (len1 == len2) {// 长度相同，则比较具体内容
				// 建立两个字节缓冲区
				byte[] data1 = new byte[len1];
				byte[] data2 = new byte[len2];

				// 分别将两个文件的内容读入缓冲区
				fis1.read(data1);
				fis2.read(data2);

				// 依次比较文件中的每一个字节
				for (int i = 0; i < len1; i++) {
					// 只要有一个字节不同，两个文件就不一样
					if (data1[i] != data2[i]) {
						// System.out.println("文件内容不一样");
						return false;
					}
				}
				// System.out.println("两个文件完全相同");
				return true;
			} else {
				// 长度不一样，文件肯定不同
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {// 关闭文件流，防止内存泄漏
			if (fis1 != null) {
				try {
					fis1.close();
				} catch (IOException e) {
					// 忽略
					e.printStackTrace();
				}
			}
			if (fis2 != null) {
				try {
					fis2.close();
				} catch (IOException e) {
					// 忽略
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 删除目录下所有的文件;
	 * 
	 * @param path
	 */
	public static boolean deleteExcelPath(File file) {
		String[] files = null;
		if (file != null) {
			files = file.list();
		}

		if (file.isDirectory()) {
			for (int i = 0; i < files.length; i++) {
				boolean bol = deleteExcelPath(new File(file, files[i]));
				if (bol) {
					System.out.println("删除成功!");
				} else {
					System.out.println("删除失败!");
				}
			}
		}
		return file.delete();
	}
}