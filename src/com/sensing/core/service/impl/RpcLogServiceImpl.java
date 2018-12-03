package com.sensing.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.sensing.core.bean.RpcLog;
import com.sensing.core.bean.RpcLogLogin;
import com.sensing.core.bean.RpcLogRun;
import com.sensing.core.dao.IRpcLogDAO;
import com.sensing.core.service.IRpcLogService;
import com.sensing.core.utils.BussinessException;
import com.sensing.core.utils.Pager;

/**
 *@author wenbo
 */
@Service
public class RpcLogServiceImpl implements IRpcLogService{


	private static final Log log = LogFactory.getLog(IRpcLogService.class);

	@Resource
	public IRpcLogDAO rpcLogDAO;
	/**
	 * @Description: 保存RpcLog日志 
	 * @author dongsl
	 * @Date 2018年6月13日下午1:42:05
	 */
	@Override
	public RpcLog saveNewRpcLog(RpcLog rpcLog){
		try {
			//日志长度限制
			if(rpcLog.getMemo().length()>1000){
				rpcLog.setMemo(rpcLog.getMemo().substring(0,1000));
			}
			rpcLogDAO.saveRpcLog(rpcLog);
		} catch (Exception e) {
			log.error("保存rpcLog失败！异常信息：" + e.getLocalizedMessage());
		}
		return rpcLog;
	}

	/**
	 * @Description: 删除rpcLog日志   
	 * @author dongsl
	 * @Date 2018年6月13日下午1:42:55
	 */
	@Override
	public void removeRpcLog(Integer id) throws Exception{
		try {
			rpcLogDAO.removeRpcLog(id);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
	}

	/**
	 * @Description: 查询rpcLog日志列表       
	 * @author dongsl
	 * @Date 2018年6月13日下午1:43:14
	 */
	@Override
	public Pager queryPage(Pager pager) throws Exception{
		try {
			List<RpcLog> list = rpcLogDAO.queryList(pager);
			int totalCount = rpcLogDAO.selectCount(pager);
			pager.setTotalCount(totalCount);
			pager.setResultList(list);
		} catch (Exception e) {
			log.error(e);
			throw new BussinessException(e);
		}
		return pager;
	}

	@Override
	public List<RpcLog> queryRpcLog(RpcLog rpcLog) throws Exception {
		List<RpcLog> list = rpcLogDAO.queryRpcLog(rpcLog);
		return list;
	}

	@Override
	public List<RpcLogLogin> queryRpcLoginLog(RpcLog rpcLog) {
		return rpcLogDAO.queryRpcLoginLog(rpcLog);
	}

	@Override
	public List<RpcLogRun> queryRpcRunLog(RpcLog rpcLog) {
		return rpcLogDAO.queryRpcRunLog(rpcLog);
	}

	@Override
	public List<RpcLog> getModle() {
		return rpcLogDAO.getModle();
	}

}