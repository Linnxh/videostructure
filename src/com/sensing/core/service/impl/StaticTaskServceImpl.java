package com.sensing.core.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.RpcLog;
import com.sensing.core.bean.StaticTask;
import com.sensing.core.dao.IStaticTaskDAO;
import com.sensing.core.service.CaptureThriftService;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.service.IStaticTaskServce;
import com.sensing.core.service.ITaskService;
import com.sensing.core.thrift.cap.bean.CapChannelConfig;
import com.sensing.core.thrift.cap.bean.CapReturn;
import com.sensing.core.utils.BussinessException;
import com.sensing.core.utils.Constants;
import com.sensing.core.utils.Pager;
@Service
public class StaticTaskServceImpl implements IStaticTaskServce {
	private static final Log log = LogFactory.getLog(StaticTaskServceImpl.class);

    @Resource
    IStaticTaskDAO statictaskDAO;
    @Autowired 
	CaptureThriftService capThriftService;
    @Resource
    ITaskService taskService;
    @Resource
    IRpcLogService logService;
	@Override
	public Pager queryPage(Pager pager) throws Exception {
		try {
            List<StaticTask> list = statictaskDAO.queryList(pager);
            int totalCount = statictaskDAO.selectCount(pager);
            pager.setTotalCount(totalCount);
            pager.setResultList(list);
        } catch (Exception e) {
            log.error(e);
            throw new BussinessException(e);
        }
        return pager;
	}
	@Override
	public StaticTask getVedioByJobId(String id) throws Exception{
		StaticTask task=new StaticTask();
		try {
			task=statictaskDAO.getVedioByJobId(id);
		 } catch (Exception e) {
            log.error(e);
            throw new BussinessException(e);
	     }
		return task;
	}
	@Override
	public void runCompare(String id) {
		RpcLog rpcLog=new RpcLog();
		try {
			StaticTask task=this.getVedioByJobId(id);
			String channel_id=task.getChannelId();
			String videoPath=task.getChannelAddr();//获取根路径
			CapChannelConfig cfg=new CapChannelConfig();
			cfg.setUuid(channel_id);
			cfg.setProtocol(5);
			cfg.setFace_merge(true);
			cfg.setMerge_all(true);
			cfg.setMerge_threshold(75);
			cfg.setZoom(960);
			cfg.setMax_face_count(100);
			cfg.setMin_confidence(0.6);
			cfg.setMin_face_width(40);
			cfg.setMin_cap_distance(8);
			cfg.setMax_yaw(30);
			cfg.setMax_roll(30);
			cfg.setChannel_port(80);
			cfg.setMax_pitch(30);
			cfg.setChannel_direct(1);
			cfg.setChannel_threshold(1);
			cfg.setStrReserve("5");
			cfg.setCap_stat(1);
			File file=new File(videoPath);
			if("".equals(file.getCanonicalPath()));
			{
				log.info("++++++++离线视频状分析+++++++"+cfg.toString());
				cfg.setChannel_addr(file.getCanonicalPath());  //拼装视频本地路径
				CapReturn result=capThriftService.AddChannel(cfg);
				log.info("++++++++离线视频状分析+++++++"+result.toString());
				if(result==null||result.getRes()<0)
				{
					task.setState(Constants.TASK_STAT_FAILEE);
					taskService.updateState(task);
					rpcLog.setCallTime(new Date());
					rpcLog.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
					rpcLog.setResult("失败");;
					rpcLog.setModule(Constants.SEVICE_MODEL_STATICVIDEO);
					rpcLog.setTodo("分析视频");
					rpcLog.setRpcType("thrift");
					rpcLog.setErrorMsg(result.getMsg());
				}
				else {
					rpcLog.setCallTime(new Date());
					rpcLog.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
					rpcLog.setResult("成功");;
					rpcLog.setModule(Constants.SEVICE_MODEL_STATICVIDEO);
					rpcLog.setTodo("分析视频");
					rpcLog.setRpcType("thrift");
					rpcLog.setMemo(cfg.toString());;
				}
			}
		} catch (Exception e) {
			rpcLog.setCallTime(new Date());
			rpcLog.setMode(Constants.INTERFACE_CALL_TYPE_INIT);
			rpcLog.setResult("异常");;
			rpcLog.setModule(Constants.SEVICE_MODEL_STATICVIDEO);
			rpcLog.setTodo("分析视频");
			rpcLog.setRpcType("thrift");
			rpcLog.setErrorMsg(e.getMessage());
			e.printStackTrace();
		}
		finally {
			logService.saveNewRpcLog(rpcLog);
		}
		
	}

}
