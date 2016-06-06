package com.suning.csp.adapter.snfnc.negative.dispatcher.proxy;

import com.gs.csp.framework.dispatcher.AbstractAcceptDispatcher;
import com.gs.csp.framework.dispatcher.proxy.AbstractAcceptDispatcherProxy;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.gs.csp.framework.persistence.AdapterNegativePersistent;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.negative.toolkit.SnfncNegativeToolkit;

import org.apache.log4j.Logger;

/**
 * 接收调度器异常代理
 *
 * @author zhangpengcheng
 */
public class SnfncNegativeTrunkAcceptDispatcherExceptionProxy extends AbstractAcceptDispatcherProxy<HostRequestMessage, HostResponseMessage, AbstractAcceptDispatcher<HostRequestMessage, HostResponseMessage>> {
    /**
     * 日志操作
     */
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * 持久化组件
     */
    private AdapterNegativePersistent negativePersistence;

    /**
     * 异常结果描述
     */
    private String exceptionResultText;

    public HostResponseMessage dispatch(HostRequestMessage hostRequestMessage) throws Exception {
        HostResponseMessage hostResponseMessage = null;
        try {
            hostResponseMessage = this.target.dispatch(hostRequestMessage);
        } catch (Exception e) {
            logger.error("exception while execute message: " + hostRequestMessage, e);
            this.negativePersistence.saveStackTrace(hostRequestMessage, e);
            int site = this.negativePersistence.queryMessageSite(hostRequestMessage);
            if (site == Toolkit.SITE_NEGATIVE_ADAPTER_REQUEST_ACCEPTED || site == Toolkit.SITE_NEGATIVE_ADAPTER_RESPONSE_ACCEPTED) {
                hostResponseMessage = SnfncNegativeToolkit.createExceptionHostResponseMessage(hostRequestMessage,exceptionResultText);
                hostResponseMessage.setException(e);
            }
        }
        return hostResponseMessage;
    }

    public void setNegativePersistence(AdapterNegativePersistent negativePersistence) {
        this.negativePersistence = negativePersistence;
    }

    public void setExceptionResultText(String exceptionResultText) {
        this.exceptionResultText = exceptionResultText;
    }
}
