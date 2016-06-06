/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: ExceptionPositiveTrunkAcceptDispatcherProxy.java
 * Author:   11070727
 * Date:     2014-1-9 上午09:47:35
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.dispatcher.proxy;

import org.apache.log4j.Logger;
import com.gs.csp.framework.dispatcher.AbstractAcceptDispatcher;
import com.gs.csp.framework.dispatcher.proxy.AbstractAcceptDispatcherProxy;
import com.gs.csp.framework.persistence.AdapterPositivePersistent;
import com.gs.csp.framework.toolkit.Toolkit;
import com.gs.csp.plugins.connector.proxy.messagefilter.exception.NotTradinghoursException;
import com.suning.csp.adapter.snfnc.positive.executor.SnfncPositiveTrunkDefaultExecutor;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;

/**
 * 调度器异常处理代理
 * 
 * @author 11070727
 */
public class SnfncPositiveTrunkAcceptDispatcherExceptionProxy
        extends
        AbstractAcceptDispatcherProxy<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage, AbstractAcceptDispatcher<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage>> {
    /**
     * 日志操作
     */
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * 持久化
     */
    private AdapterPositivePersistent<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage> persistence;

    /**
     * 异常处理器
     */
    private SnfncPositiveTrunkDefaultExecutor defaultExecutor;

    public SnfncPositiveResponseMessage dispatch(SnfncPositiveRequestMessage requestMessage) throws Exception {
        SnfncPositiveResponseMessage responseMessage = null;
        // 复制请求对象
        SnfncPositiveRequestMessage originRequestMessage = this.copyRequestMessage(requestMessage);
        try {
            responseMessage = this.target.dispatch(requestMessage);
            if (responseMessage != null) {
                // 针对特殊异常消息做处理
                if (responseMessage.getException() instanceof NotTradinghoursException)
                    return null;
            }
        } catch (Exception e) { // TODO 可调异常处理方式
            logger.error("exception while execute message: " + originRequestMessage, e);
            originRequestMessage.setException(e);
            int site = this.persistence.queryMessageSite(originRequestMessage);
            // 处理为发送至路由以及发送返回的异常
            if (site < Toolkit.SITE_POSITIVE_ADAPTER_REQUEST_SENT
                    || site >= Toolkit.SITE_POSITIVE_ADAPTER_RESPONSE_ACCEPTED) {
                // 调用异常处理器
                responseMessage = this.defaultExecutor.execute(originRequestMessage);
                if (responseMessage != null) {
                    // 同步异常响应拦截
                    this.interceptOutputMessage(responseMessage);
                }
            }
        }
        return responseMessage;
    }
    
    /**
     * 
     * 复制请求对象
     *
     * @param requestMessage 请求对象
     * @return 复制对象
     */
    public SnfncPositiveRequestMessage copyRequestMessage(SnfncPositiveRequestMessage requestMessage){
        SnfncPositiveRequestMessage originMessage = new SnfncPositiveRequestMessage();
        originMessage.setMessageId(requestMessage.getMessageId());
        originMessage.setClientCode(requestMessage.getClientCode());
        originMessage.setBusinessCode(requestMessage.getBusinessCode());
        originMessage.setContent(requestMessage.getContent());
        originMessage.setException(requestMessage.getException());
        originMessage.setFilePath(requestMessage.getFilePath());
        originMessage.setSerialNumber(requestMessage.getSerialNumber());
        originMessage.setModel(requestMessage.getModel());
        originMessage.setPriority(requestMessage.getPriority());
        originMessage.setVersion(requestMessage.getVersion());
        return originMessage;
    }

    public void setDefaultExecutor(SnfncPositiveTrunkDefaultExecutor defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
    }

    public void setPersistence(AdapterPositivePersistent<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage> persistence) {
        this.persistence = persistence;
    }
}