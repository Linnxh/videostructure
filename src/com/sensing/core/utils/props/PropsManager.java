package com.sensing.core.utils.props;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;  
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;  
import ch.ethz.ssh2.StreamGobbler;  

public class PropsManager {
	
	private static final Log log = LogFactory.getLog(PropsManager.class);
	private static final String tempDir = "D:/temp/";
	
	public static void main(String[] args) throws Exception{
		PropsManager manager = new PropsManager();
		String ipAddr = "192.168.0.158";
		String userName = "root";
		String password = "sensingtech-123";
		String path = "/sensing/rlsb_deploy_2.8/captureserversx/sub01/sstcomp/FacialRelated/";
		String file = "FacialRelated.ini";
		
		
		Map<String,String> kv = new HashMap<String,String>();
		kv.put("Threshold", "60");
		kv.put("CheckTimeout", "30");
		
		//获取当前配置
		Map<String,String> before = manager.getRemoteProps(ipAddr,userName,password,path,file);
//		for (String key : before.keySet()) {
//            System.out.println(key + " = " + before.get(key));
//        }
		
//		System.out.println("-------------------------------------------------");
		
		//修改配置文件的
		manager.modifyProps(ipAddr,userName,password,path,file,kv);
		
		Map<String,String> after = manager.getRemoteProps(ipAddr,userName,password,path,file);
//		for (String key : after.keySet()) {
//            System.out.println(key + " = " + after.get(key));
//        }

	}

	/**
	 * 修改远程linux服务器的配置文件
	 * @param ipAddr
	 * @param userName
	 * @param password
	 * @param path
	 * @param file
	 * @param kv
	 * @return
	 * @throws Exception
	 */
	public String modifyProps(String ipAddr,String userName,String password,String path,String file,Map kv) throws Exception{
		//第一步：创建连接对象
		Connection conn = null;
		try {
			//第二步：验证网络连通状态
			boolean status = InetAddress.getByName(ipAddr).isReachable(1500);
			if (status) {
				//第三步：建立连接
				conn = new Connection(ipAddr);
				conn.connect();
				//第四步：验证帐号密码
				boolean isAuthed = conn.authenticateWithPassword(userName, password);
				if (isAuthed) {
					//第五步：从远程服务器上面获取文件并保存到本地
					Session session = conn.openSession();
					SCPClient scpClient = conn.createSCPClient();
					scpClient.get(path + file, tempDir);
					session.close();//关闭连接
					
					//第六步：通过一系列文件操作修改配置文件
					//备份修改前的配置文件
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH24mmss");
					String dateStr = sdf.format(date);
					this.copyFile(new File(tempDir + file), new File(tempDir + ipAddr + "_" + dateStr + "_" + file));
					this.updateParam(tempDir + file, kv);
					
					//第七步：删掉旧版本文件
					session = conn.openSession();
					String cmd = "cd "+path+" &&rm -rf "+file;
					session.execCommand(cmd);
					session.close();
		
					//第八步：将修改后的本地文件放入远程服务器原路径中
					scpClient.put(tempDir + file, path);
					
					//抓拍有多个节点的情况
					if(path.indexOf("sub01")>0){
						try{
							scpClient.put(tempDir + file, path.replaceFirst("sub01", "sub02"));
							scpClient.put(tempDir + file, path.replaceFirst("sub01", "sub03"));
							scpClient.put(tempDir + file, path.replaceFirst("sub01", "sub04"));
							scpClient.put(tempDir + file, path.replaceFirst("sub01", "sub05"));
							scpClient.put(tempDir + file, path.replaceFirst("sub01", "sub06"));
							scpClient.put(tempDir + file, path.replaceFirst("sub01", "sub07"));
							scpClient.put(tempDir + file, path.replaceFirst("sub01", "sub08"));
						}catch(Exception e){
							log.error("保存抓拍节点的配置文件，因节点不存在，保存失败");
							log.error(e);
						}
					}
					
					return "success";
				} else {
					return "login failed";
				}
			}else{
				return "connet failed";
			}
		}catch (IOException e) {
			log.error(e);
			throw e;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
	}
	
	
	
	/**
	 * 修改本地配置文件
	 * @param localPath 本地目录的配置文件
	 * @param kv	配置项KV对,如xxx=xxxxxx
	 * @throws Exception
	 */
	public void updateParam(String localPath,Map<String,String> kv) throws Exception{
		String temp = "";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			//读取从服务器上get的临时文件
			File file = new File(localPath);
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
			//遍历每一行，写入StringBuffer
			for (int j = 1; (temp = br.readLine()) != null ; j++) {
				//修改配置文件，标准格式
				temp = temp.trim();
				String k = temp.split("=")[0].toString();
				if(kv.get(k)!=null){
					String temp1 = temp.replaceAll(temp.split("=")[1].toString(),kv.get(k));
					buf = buf.append(temp1);
				//修改配置文件，标准格式
				}else{
					buf = buf.append(temp);
				}
				buf = buf.append(System.getProperty("line.separator"));
			}
			br.close();
			//输出更新后的文件
			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();
		} catch (IOException e) {
			log.error(e);
			throw e;
		}finally{
			if(fis!=null) fis.close();
			if(isr!=null) isr.close();
			if(br!=null) br.close();
			if(fos!=null) fos.close();
			if(pw!=null) pw.close();
		}
	}
	
	
	/**
	 * 拷贝文件,用于修改前备份
	 * @param source
	 * @param copy
	 * @throws IOException
	 */
	private void copyFile(File source, File copy) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(copy);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			if(input!=null) input.close();
			if(output!=null) output.close();
		}
	}
	
	/**
	 * 获取配置文件KV
	 * @param ipAddr
	 * @param userName
	 * @param password
	 * @param path
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> getRemoteProps(String ipAddr,String userName,String password,String path,String file) throws Exception{
		Map<String,String> result = new HashMap<String,String>();
		//第一步：创建连接对象
		Connection conn = null;
		try {
			//第二步：验证网络连通状态
			boolean status = InetAddress.getByName(ipAddr).isReachable(1500);
			if (status) {
				//第三步：建立连接
				conn = new Connection(ipAddr);
				conn.connect();
				//第四步：验证帐号密码
				boolean isAuthed = conn.authenticateWithPassword(userName, password);
				if (isAuthed) {
					//第五步：从远程服务器上面获取文件并保存到本地
					Session session = conn.openSession();
					SCPClient scpClient = conn.createSCPClient();
					scpClient.get(path + file, tempDir);
					session.close();//关闭连接
					//读取配置文件
					result = this.getKVFromLocalPropFile(tempDir + file);
				} else {
					log.error("连接" + ipAddr + "时登录失败");
				}
			}else{
				log.error("连接" + ipAddr + "失败");
			}
		}catch (IOException e) {
			log.error(e);
			throw e;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}finally{
			if(conn!=null){
				conn.close();
			}
		}
		return result;
	}
	
	/**
	 * 读取本地配置文件中的数据
	 * @param localPath
	 * @throws Exception
	 */
	public Map<String,String> getKVFromLocalPropFile(String localPath) throws Exception{
		Map<String,String> result = new HashMap<String,String>();
		String temp = "";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			//读取从服务器上get的临时文件
			File file = new File(localPath);
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			//遍历每一行，写入StringBuffer
			for (int j = 1; (temp = br.readLine()) != null ; j++) {
				//修改配置文件，标准格式  
				if(temp!=null && !temp.trim().equals("") && !temp.startsWith("#") && !temp.startsWith(";") && temp.indexOf("=")>0){
					temp = temp.trim();
					String[] kv = temp.split("=");
					result.put(kv[0], kv[1]);
				}
			}
		} catch (IOException e) {
			log.error(e);
			throw e;
		}finally{
			if(fis!=null) fis.close();
			if(isr!=null) isr.close();
			if(br!=null) br.close();
		}
		return result;
	}
	
	
	
	
}
