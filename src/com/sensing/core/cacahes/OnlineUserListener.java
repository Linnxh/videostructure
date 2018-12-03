package com.sensing.core.cacahes;

import com.sensing.core.aop.RequestInfoLog;
import com.sensing.core.utils.props.PropUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class OnlineUserListener implements HttpSessionListener {

    private static int count = 0;
    //日志记录
    private final static Logger log = LoggerFactory.getLogger(OnlineUserListener.class);
    private static int sessionTimeOut = 1800;

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        //单位为秒
        session.setMaxInactiveInterval(sessionTimeOut);
        count++;
        log.info("-------新增session" + session.getId() + "-------session数量为" + count);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String id = session.getId() + session.getCreationTime();
        synchronized (this) {
            count--;
            log.info("-------减少session" + session.getId() + "-------session数量为" + count);
        }
    }

    public  void setSessionTimeOut(Integer sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }
}
