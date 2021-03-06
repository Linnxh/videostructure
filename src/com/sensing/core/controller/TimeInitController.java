package com.sensing.core.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sensing.core.bean.SysParam;
import com.sensing.core.bean.SysTypecode;
import com.sensing.core.service.ISysTypecodeService;
import com.sensing.core.service.ISysParamService;
import com.sensing.core.utils.BaseController;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.ResponseBean;
import com.sensing.core.utils.WebUtil;

@Controller
@RequestMapping("/timeInit")
public class TimeInitController extends BaseController{
	private static final Log log = LogFactory.getLog(TimeInitController.class);
	@Autowired
	ISysParamService timeInitService;
	@Autowired
	public ISysTypecodeService sysTypeCodeService;
	@ResponseBody
    @RequestMapping("/initByHand")
    public ResponseBean initByHand(@RequestBody JSONObject p) {
		 ResponseBean result = new ResponseBean();
		 String date=p.getString("date");
		 String time=p.getString("time");
		 String zone=p.getString("zone");
		 String cmd = "";
		 try {
			 if(WebUtil.isWindowsOS())
			 {
				 result.setError(100);
				 result.setMessage("windows暂不支持时间设置");
			 }else {
			   cmd="sed -i '/ntpdate/d' /etc/crontab";//去除掉原来的定时任务设置
			   Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
			   cmd="timedatectl set-ntp no";
			   Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
			   cmd = "timedatectl set-timezone \""+zone+"\"";
			   Process process =Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
			   int exitValue = process.waitFor();  
		       if (0 == exitValue) { 
				   cmd = "timedatectl set-time "+date;
				   Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
				   cmd = "timedatectl set-time "+time;
				   Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
				   saveTime(p);
				   result.setError(0);
				   result.setMessage("successful");
		       }
			 }
		 } catch (IOException e) {
			 result.setError(-1);
			 result.setMessage(e.getMessage());
			 log.error(e);
		 } catch (Exception e) {
			 result.setError(-1);
			 result.setMessage(e.getMessage());
			 log.error(e);
		}
		 return result;
	}
	@ResponseBody
    @RequestMapping("/initByNTP")
    public ResponseBean initByNTP(@RequestBody JSONObject p) {
		 ResponseBean result = new ResponseBean();
		 String zone=p.getString("zone");
		 String ip=p.getString("ip");
		 String port=p.getString("port");
		 String rate=p.getString("rate");
		 String cmd = "";
		 try {
			 if(WebUtil.isWindowsOS())
			 {
				 result.setError(100);
				 result.setMessage("windows暂不支持时间同步设置");
			 }else {
				cmd = "timedatectl set-timezone \""+zone+"\"";
				Process processZone =Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
				int exitValueZone = processZone.waitFor();  
			    if (0 == exitValueZone) { 
			    	cmd="ntpdate "+ip;
			    	Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
			    	cmd="sed -i '/ntpdate/d' /etc/crontab";//去除掉原来的定时任务设置
					Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
					cmd="echo \"00 */"+rate+" * * * root /usr/sbin/ntpdate "+ip+"\" >>/etc/crontab";//新增定时任务
					Process process =Runtime.getRuntime().exec(new String[] { "/bin/sh","-c",cmd});
					int exitValue = process.waitFor();  
			        if (0 != exitValue) {  
			        	log.error("同步时间失败");  
			        	result.setError(-1);
						result.setMessage("同步时间失败");
			        }
			        else {
			        	 saveTime(p);
			        	 result.setError(0);
						 result.setMessage("successful");
			        }
			    }
			 }
		} catch (IOException e) {
			 result.setError(-1);
			 result.setMessage(e.getMessage());
			 log.error(e);
		} catch (InterruptedException e) {
			result.setError(-1);
			result.setMessage(e.getMessage());
			log.error(e);
		} catch (Exception e) {
			result.setError(-1);
			result.setMessage(e.getMessage());
			log.error(e);
		}
		 return result;
	}
	@ResponseBody
    @RequestMapping("/queryTimeSyn")
    public ResponseBean queryTimeSyn() {
		ResponseBean result = new ResponseBean();
		try {
			List<SysParam> param=timeInitService.queryTimeSyn(Constants.SYS_TIMESYN);
			result.setError(0);
			result.setMessage("successful");
			result.getMap().put("dates", param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.setError(-1);
		    result.setMessage(e.getMessage());
		    log.error(e);
		}
		return result;
	}
	@ResponseBody
    @RequestMapping("/queryTimeZones")
    public ResponseBean queryTimeZones() {
		ResponseBean result = new ResponseBean();
		try {
			List<SysTypecode>SysTypecode=sysTypeCodeService
					.querySysTypeCodeByTypeCodeAndItemId(Constants.SYS_TYPECODE_TIMEZONES, null);
			result.setError(0);
			result.setMessage("successful");
			result.getMap().put("dates", SysTypecode);
		} catch (Exception e) {
			result.setError(-1);
			result.setMessage(e.getMessage());
			log.error(e);
		}
		return result;
	}
    private  void saveTime(@RequestBody JSONObject p) throws Exception {
		 String zone=p.getString("zone");
		 String ip=p.getString("ip");
		 String port=p.getString("port");
		 String rate=p.getString("rate");
		List<SysParam> params=new ArrayList<SysParam>();
		 params.add(setparam("type","2"));
    	 params.add(setparam("time_zone", zone));
    	 params.add(setparam("ip", ip));
    	 params.add(setparam("port", port));
    	 params.add(setparam("rate", rate));
		 timeInitService.updateParam(params);
	}
	private SysParam setparam(String key,String value)
	{
		SysParam param=new SysParam();
		param.setParam(key);
		param.setTypeCode(Constants.SYS_TIMESYN);
		param.setValue(value);
		return param;
	}
}
