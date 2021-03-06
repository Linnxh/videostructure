package com.sensing.core.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.utils.UuidUtil;

/**
 * 服务端向web端推送抓拍数据
 * <p>Title: CapDataPush</p>
 * <p>Description:</p>
 * <p>Company: www.sensingtech.com</p> 
 * @author	mingxingyu
 * @date	2018年7月23日
 * @version 1.0
 */
@ServerEndpoint("/capDataPush")
public class CapDataPush {
    private Session session;
    public String deviceId;//通道的uuid
    public static CopyOnWriteArraySet<CapDataPush> capWbSockets = new CopyOnWriteArraySet<CapDataPush>(); //此处定义静态变量，以在其他方法中获取到所有连接
//    public static boolean flag = false;
    private Log log = LogFactory.getLog(CapDataPush.class); 
    
    /**
     * 客户端创建websocket连接
     * @param session
     * @author mingxingyu
     * @date   2018年7月23日 上午11:17:22
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config){
        this.session = session;
        capWbSockets.add(this); //将此对象存入集合中以在之后广播用，如果要实现一对一订阅，则类型对应为Map。由于这里广播就可以了随意用Set
        log.info("capDataPush收到链接,sessionId:"+session.getId());
//        if ( !flag ) {
//        	produceMsg();
//        	flag = true;
//		}
    }
    /**
     * 关闭连接
     * 
     * @author mingxingyu
     * @date   2018年7月23日 上午11:18:04
     */
    @OnClose
    public void onClose(){
        capWbSockets.remove(this);//将socket对象从集合中移除，以便广播时不发送次连接。如果不移除会报错(需要测试)
        log.info("capDataPush删除session,sessionid:"+session.getId());
    }
    /**
     * 接收前端传过来的数据。
     * @param message
     * @param session
     * @author mingxingyu
     * @date   2018年7月23日 上午11:18:20
     */
    @OnMessage
    public void onMessage(String message ,Session session){
        log.info("capDataPush接收到信息,sessionid:"+session.getId()+",message:"+message);
        this.deviceId = message;
    }
    
    /**
     * 服务端向客户端推送数据
     * @param message
     * @throws IOException
     * @throws EncodeException
     * @author mingxingyu
     * @date   2018年7月23日 上午11:19:17
     */
    public void sendMessage(String message) throws IOException, EncodeException {
    	try {
    		this.session.getBasicRemote().sendText(message);
    		log.info("capDataPush推送信息,sessionid:"+session.getId()+",messageLength:"+message.length());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("服务端向客户端推送数据."+e.getMessage());
		}
    }
    
    
    public void produceMsg(){
    	ExecutorService executor = Executors.newSingleThreadExecutor();
    	executor.submit(new Runnable() {
			public void run() {
				try {
					int i = 0 ;
					while ( true ) {
						Map<String,String> mapMsg = new HashMap<String, String>();
						log.info("当前capWbSockets链接的数量是:"+capWbSockets.size());
						if ( capWbSockets != null && capWbSockets.size() > 0  ) {
							for (CapDataPush dataPush : capWbSockets) {

								mapMsg.put("uuid",UuidUtil.getUuid());
								
								if ( i % 3 == 0 ) {
									mapMsg.put("imgUrl","http://192.168.0.153:8500/image/group1/M00/00/00/wKgAmVt9M--IaQ4jAAAOYLAuYNoAAAAAwHHQT4AAA54825.jpg");
									mapMsg.put("upperClothesColor","红色");
									mapMsg.put("lowerClothesColor","黑色");
									mapMsg.put("carryThingsBag","双肩包");
									mapMsg.put("glass","有");
									mapMsg.put("hat","头盔");
									mapMsg.put("capType","1");
								}
								if ( i % 3 == 1 ) {
									mapMsg.put("plateLicence","1");
									mapMsg.put("carColor","银灰色");
									mapMsg.put("brandMain","大众");
									mapMsg.put("carType","中型客车");
									mapMsg.put("plateColor","蓝色");
									mapMsg.put("imgUrl","http://192.168.0.153:8500/image/group1/M00/00/00/wKgAmVt9M--IaQ4jAAAOYLAuYNoAAAAAwHHQT4AAA54825.jpg");
									mapMsg.put("capType","3");
								}
								if ( i % 3 == 2 ) {
									mapMsg.put("imgUrl","http://192.168.0.153:8500/image/group1/M00/00/00/wKgAmVt9M--IaQ4jAAAOYLAuYNoAAAAAwHHQT4AAA54825.jpg");
									mapMsg.put("upperClothesColor","红色");
									mapMsg.put("lowerClothesColor","黑色");
									mapMsg.put("carryThingsBag","双肩包");
									mapMsg.put("glass","有");
									mapMsg.put("hat","头盔");
									mapMsg.put("capType","4");
								}
								
								dataPush.sendMessage(JSONObject.toJSONString(mapMsg));
							}
						}
						Thread.sleep(5000);
						i++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("定时任务推送消息时发生异常。"+e.getMessage());
				}
			}
		});
    }
}