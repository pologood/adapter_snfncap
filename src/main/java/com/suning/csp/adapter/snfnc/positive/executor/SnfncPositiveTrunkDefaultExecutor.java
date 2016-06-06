package com.suning.csp.adapter.snfnc.positive.executor;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.gs.csp.framework.exception.ValidateException;
import com.gs.csp.framework.executor.AbstractSingleExecutor;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;

/**
 * 正向异常处理器
 * 
 * @author 11070727
 */
public class SnfncPositiveTrunkDefaultExecutor
        extends
        AbstractSingleExecutor<SnfncPositiveRequestMessage, HostRequestMessage, HostResponseMessage, SnfncPositiveResponseMessage> {
    /**
     * 日志
     */
    private final Logger logger = Logger.getLogger(this.getClass());
    
    /**
     * 异常返回消息
     */
    private String defaultExceptionResultText="exception in default executor";
    
    public SnfncPositiveResponseMessage execute(SnfncPositiveRequestMessage acceptInputMessage) throws Exception {
        SnfncPositiveResponseMessage acceptOutputMessage = new SnfncPositiveResponseMessage();
        try {
            // 拦截接收端请求消息
            acceptInputMessage = this.interceptAcceptInputMessage(acceptInputMessage);
            // 执行请求消息业务
            HostRequestMessage sendOutputMessage = this.accept2send(acceptInputMessage);
            // 发送
            HostResponseMessage sendInputMessage = this.link(sendOutputMessage);
            if (sendInputMessage == null) return null;
            // 执行响应消息业务
            acceptOutputMessage = this.send2accept(sendInputMessage);
        } catch (Exception e) {
            logger.error("exception in default executor: " +acceptInputMessage, e);
            acceptOutputMessage = SnfncPositiveToolkit.createExceptionResponseMessage(acceptInputMessage, SnfncPositiveToolkit.POSITIVE_TRANSACT_STATUS_UNKNOWN, defaultExceptionResultText);
        }
        return acceptOutputMessage;
    }

    protected HostRequestMessage accept2send(SnfncPositiveRequestMessage acceptInputMessage) throws Exception {
        HostRequestMessage hostRequestMessage = SnfncPositiveToolkit.createHostRequestMessage(acceptInputMessage, null, null);
        Document document = DocumentHelper.parseText(acceptInputMessage.getContent());
        hostRequestMessage.setContent(document.selectSingleNode("/positive-request/body").asXML());
        // 设置异常信息
        hostRequestMessage.setException(acceptInputMessage.getException());
        // 设置特殊异常标识
        if(acceptInputMessage.getException() instanceof ValidateException){
            hostRequestMessage.setSpecialExceptionFlag(true);
        }
        hostRequestMessage.setFilePath(null);
        return hostRequestMessage;
    }

    public SnfncPositiveResponseMessage send2accept(HostResponseMessage sendInputMessage) throws Exception {
        SnfncPositiveResponseMessage positiveResponseMessage = new SnfncPositiveResponseMessage();
        return SnfncPositiveToolkit.createPositiveResponseMessage(sendInputMessage, null, positiveResponseMessage,null,null);
    }

    protected HostResponseMessage link(HostRequestMessage sendOutputMessage) throws Exception {
        return this.processor.getSendDispatcher().dispatch(sendOutputMessage);
    }

    public void setDefaultExceptionResultText(String defaultExceptionResultText) {
        this.defaultExceptionResultText = defaultExceptionResultText;
    }
}
