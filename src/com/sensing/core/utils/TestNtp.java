package com.sensing.core.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;
//ntp.sjtu.edu.cn 上海交大
//ntp.sjtu.edu.cn

public class TestNtp {
	public static void main(String[] args) throws IOException {
		try {
			NTPUDPClient timeClient = new NTPUDPClient();
			String timeServerUrl = "ntp.sjtu.edu.cn";
			int port = 123;
			InetAddress timeServerAddress = InetAddress
					.getByName(timeServerUrl);
			TimeInfo timeInfo = timeClient.getTime(timeServerAddress, port);
			TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
			Date date = timeStamp.getDate();
			timeStamp.getFraction();
			// System.out.println(date);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			// System.out.println(dateFormat.format(date));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
