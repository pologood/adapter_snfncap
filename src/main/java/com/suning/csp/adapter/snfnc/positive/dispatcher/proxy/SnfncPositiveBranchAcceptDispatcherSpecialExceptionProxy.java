package com.suning.csp.adapter.snfnc.positive.dispatcher.proxy;

import org.apache.log4j.Logger;

import com.gs.csp.framework.dispatcher.AbstractAcceptDispatcher;
import com.gs.csp.framework.dispatcher.proxy.AbstractAcceptDispatcherProxy;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.gs.csp.framework.persistence.Persistent;
import com.gs.csp.plugins.connector.proxy.messagefilter.exception.NotTradinghoursException;

public class SnfncPositiveBranchAcceptDispatcherSpecialExceptionProxy
        extends AbstractAcceptDispatcherProxy<HostResponseMessage, HostRequestMessage, AbstractAcceptDispatcher<HostResponseMessage, HostRequestMessage>> {

    /**
     * 持久化
     */
    private Persistent<?, HostRequestMessage, HostResponseMessage, ?> persistence;

    /**
     * 日志记录器
     */
    private Logger logger = Logger.getLogger(this.getClass());

    public HostRequestMessage dispatch(HostResponseMessage inputMessage) throws Exception {
        try {
            // 针对特殊异常消息做处理 
            if (inputMessage.getException() instanceof NotTradinghoursException)
                return null;
            this.target.dispatch(inputMessage);
        } catch (Exception e) {
            logger.error("exception while execute message: " + inputMessage, e);
            // 记录异常信息
            this.persistence.saveStackTrace(inputMessage, e);
        }
        return null;
    }

    public void setPersistence(Persistent<?, HostRequestMessage, HostResponseMessage, ?> persistence) {
        this.persistence = persistence;
    }
}
