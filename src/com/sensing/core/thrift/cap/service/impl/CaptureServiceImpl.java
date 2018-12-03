package com.sensing.core.thrift.cap.service.impl;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.thrift.TException;

import com.sensing.core.thrift.cap.bean._capChannelConfig;
import com.sensing.core.thrift.cap.bean._capFacesSet;
import com.sensing.core.thrift.cap.bean._capReturn;
import com.sensing.core.thrift.cap.service.CaptureService.Iface;

public class CaptureServiceImpl implements Iface {

	public CaptureServiceImpl(){}

	@Override
	public _capReturn AddChannel(_capChannelConfig cfg) throws TException {
		return null;
	}

	@Override
	public _capReturn DelChannel(String strChannelID) throws TException {
		return null;
	}

	@Override
	public _capReturn DelChannelByRegionID(int iRegionID) throws TException {
		return null;
	}

	@Override
	public _capReturn ModifyChannel(_capChannelConfig cfg) throws TException {
		return null;
	}

	@Override
	public List<_capFacesSet> DetectFaces(List<ByteBuffer> lstImgs) throws TException {
		return null;
	}

	@Override
	public _capReturn OpenCloseChannels(List<String> lstChnls, int iOpenFlag, int iAllFlag) throws TException {
		return null;
	}

	
	
}