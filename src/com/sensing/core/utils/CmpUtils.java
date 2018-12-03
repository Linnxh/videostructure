package com.sensing.core.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.ImageFile;
import com.sensing.core.thrift.cap.bean._capFaceDetectResult;
import com.sensing.core.thrift.cap.bean._capFacesSet;
import com.sensing.core.thrift.cap.service.CaptureService.Client;
import com.sensing.core.utils.httputils.HttpDeal;
import com.sensing.core.utils.props.PropUtils;
import com.sensing.core.utils.props.RemoteInfoUtil;
import com.sensing.core.utils.time.DateUtil;

/**
 * cmp业务公共方法集合
 * */
@SuppressWarnings("all")
public class CmpUtils {
	private static final Log log = LogFactory.getLog(CmpUtils.class);
	/**
	 *  调用抓拍返回特征值,多个特征值返回最大的
	 *  @param imageBytes
	 *  @return feature
	 *  @author liuwei
	 * @throws BussinessException 
	 * */
	public static ByteBuffer getFaceFeature(byte[] imageBytes) throws BussinessException{
		List<ByteBuffer> lstImgs = new ArrayList<ByteBuffer>();
		List<_capFacesSet> detectFaces = new ArrayList<_capFacesSet>();
		ByteBuffer feature = null;
		ByteBuffer byteBuffer = ByteBuffer.wrap(imageBytes);
		if (byteBuffer != null) {
			lstImgs.add(byteBuffer);
		} else {
			throw new BussinessException("图片传递有误，请确认");
		}		
		TTransport transport =  new TSocket(RemoteInfoUtil.CAP_SERVER_IP, RemoteInfoUtil.CAP_SERVER_PORT, RemoteInfoUtil.CAP_TIMEOUT);
		Client client = null;
		if (!transport.isOpen()) {
			try {
				transport.open();
			} catch (TTransportException e) {
				log.error("抓拍服务连接异常！",e.fillInStackTrace());
				throw new BussinessException("抓拍服务连接异常!");
			}
		}
		
		TProtocol protocol = new TBinaryProtocol(transport);
		client = new Client(protocol);
		try {
			detectFaces = client.DetectFaces(lstImgs);//调用抓拍
			if(transport.isOpen()){
				transport.close();
			}
		} catch (Exception e) {
			log.error("抓拍信息处理发生异常",e);
			throw new BussinessException("抓拍信息处理发生异常!");
		}
		
		
		// 赋特征值：如果有两个特征值，则获取最大的一个；计算方法：人脸特征的 脸宽度 X人脸高度 作为比较依据
		if (detectFaces != null && detectFaces.size() > 0) {
			// 判断是否有返回值
			_capFacesSet facesInfo = detectFaces.get(0);
			if (facesInfo != null && facesInfo.getLstFaces().size() > 0) {
				feature = getMaxFaceByte(facesInfo);
			}else {
				log.error("抓拍服务没有找到特征信息!");
				throw new BussinessException("抓拍服务没有找到特征信息!");
			}
		}else {
			log.error("抓拍服务没有找到特征信息!");
			throw new BussinessException("抓拍服务没有找到特征信息!");
		}
		return feature;
	}
	/**
	 * 获取map中key值最大的对象
	 * @param map
	 * @return
	 * @author liuwei
	 * @date   2018年3月29日上午16:30:03
	 */
	private static Object getMaxKey(Map<Integer, Object> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		int maxValue = Integer.MIN_VALUE;
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			if (maxValue < key) {
				maxValue = key;
			}
		}
		return map.get(maxValue);
	}

	public static ByteBuffer getMaxFaceByte(_capFacesSet facesInfo){
		ByteBuffer feature = null;
		/*
		 * 需要调用抓拍服务的接口；获取ft_fea(模板特征)、face_x(模版人脸X坐标)、face_y(模版人脸Y坐标)、
		 * face_cx(模版人脸宽度)、face_cy(模版人脸高度)、ft_quality(模版质量)。
		 */
		// 判断返回的特征值有几个
		if (facesInfo.getLstFaces().size() == 1) {
			feature =  ByteBuffer.wrap(facesInfo.getLstFaces().get(0).getBinFea());
			
		} else {
			// 特征值大于1个，需要计算获取人脸面积最大的一个特征。
			List<_capFaceDetectResult> list = facesInfo.getLstFaces();// 查询出的特征值
			Map<Integer, Object> facesMap = new HashMap<Integer, Object>();// 过滤多个特征值

			for (_capFaceDetectResult _capFaceDetectResult : list) {
				int right = _capFaceDetectResult.getRcFace().getRight();
				int left = _capFaceDetectResult.getRcFace().getLeft();
				int bottom = _capFaceDetectResult.getRcFace().getBottom();
				int top = _capFaceDetectResult.getRcFace().getTop();
				int Integer = (right - left) * (bottom - top);
				facesMap.put(Integer, _capFaceDetectResult);
			}
			// 选择出最大的一个对象。
			_capFaceDetectResult maxCapFaceDetect = (_capFaceDetectResult) getMaxKey(facesMap);
			if (maxCapFaceDetect == null) {
				// 处理失败
				log.error("获取在多个特征值中最大特征值失败！");
				throw new BussinessException("获取在多个特征值中最大特征值失败!");
			}
			feature = ByteBuffer.wrap(maxCapFaceDetect.getBinFea());
		}
		return feature;
	}
	public static ByteBuffer reGetFaceFeature(byte[] imageBytes) throws BussinessException{
		ByteBuffer feature = null;
		try{
			feature = getFaceFeature(imageBytes);
		}catch(BussinessException e){
			if(e.getErrorCode().equals("抓拍服务没有找到特征信息!")){
				//放大3倍重新提前特征值
				feature = getFaceFeature(ImgUtil.ShowImgByByte(imageBytes));
			}
		}
		return feature;
	}
	/**
	 * @Description: 提取图片特征值 并 上传图片服务器
	 * @param lstImgs
	 * @return
	 * @throws Exception
	 * @return List<byte[]> 特征值数组
	 * @author liuwei
	 * @Date 2018年07月27日
	 */
	public static List<Map<String,byte[]>> getCapResult(List<byte[]> imgList)
			throws Exception {
		List<Map<String,byte[]>> list = new ArrayList<Map<String,byte[]>>();
		for (byte[] imgByte : imgList) {
			List<_capFacesSet> detectFaces = new ArrayList<_capFacesSet>(); // 特征列表
			List<ByteBuffer> lstImgs = new ArrayList<ByteBuffer>();// 抓拍所需图片参数
			ByteBuffer byteBuffer = ByteBuffer.wrap(imgByte);
			// 为lstImgs赋值
			if (byteBuffer != null) {
				lstImgs.add(byteBuffer);
			} else {
				log.info(DateUtil.DateToString(new Date()) + ":图片传递有误，请确认！");
				throw new Exception("图片传递有误，请确认！");
			}
			// 开启抓拍
			TTransport transport = new TSocket(RemoteInfoUtil.CAP_SERVER_IP,
					RemoteInfoUtil.CAP_SERVER_PORT, RemoteInfoUtil.CAP_TIMEOUT);
			com.sensing.core.thrift.cap.service.CaptureService.Client client = null;
			if (!transport.isOpen()) {
				try {
					transport.open();
				} catch (TTransportException e) {
					log.error("抓拍服务连接异常！", e.fillInStackTrace());
					e.printStackTrace();
					if (transport.isOpen()) {
						transport.close();
					}
					throw new Exception("抓拍服务连接异常！");
				}
			}
			// 调用抓拍 start
			TProtocol protocol = new TBinaryProtocol(transport);
			client = new com.sensing.core.thrift.cap.service.CaptureService.Client(
					protocol);
			try {
				detectFaces = client.DetectFaces(lstImgs);// 调用抓拍,返回特征值列表
				//-------原图抓不到特征，放大三倍后再提取特征--------
				_capFacesSet facesInfo = detectFaces.get(0);
				if (facesInfo == null || facesInfo.getLstFaces().size() <= 0) {
					ByteBuffer newbyteBuffer = ByteBuffer.wrap(ImgUtil.ShowImgByByte(imgByte));
					if (newbyteBuffer != null) {
						lstImgs.clear();
						lstImgs.add(newbyteBuffer);
					}
					detectFaces = client.DetectFaces(lstImgs);// 调用抓拍,返回特征值列表
				}
				//-------二次提取特征 end--------
				if (transport.isOpen()) {
					transport.close();
				}
			} catch (Exception e) {
				log.error("抓拍信息处理发生异常", e);
				e.getStackTrace();
				throw new Exception("抓拍信息处理发生异常！");
			}
			// -------------调用抓拍 end----------

			if (detectFaces != null && detectFaces.size() > 0) {
				_capFacesSet facesInfo = detectFaces.get(0);
				if (facesInfo != null && facesInfo.getLstFaces().size() > 0) {
					// 查询出的特征值
					_capFaceDetectResult capres = facesInfo.getLstFaces().get(0);// 查询出的特征值列表
					//上传人像图片到服务器 start
					String seceneURI = UuidUtil.getUuid() + ".jpg";
					ImageFile seceneImageFile = new ImageFile();
					String secenePut = HttpDeal.doPut(seceneURI, capres.getBinImg());
					String imgURL = null;
					if(StringUtils.isNotEmpty(secenePut)){
						seceneImageFile = JSONObject.toJavaObject(JSONObject.parseObject(secenePut), ImageFile.class);
						imgURL = PropUtils.getString("remote.img.url")+ seceneImageFile.getMessage();
						Map<String,byte[]> map = new HashMap<String,byte[]>();
						map.put(imgURL, capres.getBinFea());
						list.add(map);
					}else{
						log.error("抓拍获取返回值中人像图片为空！");
					}
					//上传人像图片到服务器 end
				} else {
					log.error("抓拍服务没有找到特征信息!");
					throw new Exception("抓拍服务没有找到特征信息！");
				}
			} else {
				log.error("抓拍服务没有找到特征信息!");
				throw new Exception("抓拍服务没有找到特征信息！");
			}
			
		}
		return list;
	}
}
