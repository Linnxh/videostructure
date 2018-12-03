package com.sensing.core.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * 
 * com.sensing.core.utils
 * 调试类
 * @author haowenfeng
 * @date 2018年4月27日 下午8:08:10
 */
public class TestRemote {

	public static void main(String[] args) throws Exception {
		while ( true ) {
			System.out.println(isHostConnectable("192.168.3.246",7700));
			Thread.sleep(3000);
		}
	}

	public static boolean isHostConnectable(String host, int port) {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(host, port), 3000);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
