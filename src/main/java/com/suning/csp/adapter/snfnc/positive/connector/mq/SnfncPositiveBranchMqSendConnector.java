/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: SnfncPositiveMqSendConnector.java
 * Author:   11070727
 * Date:     2014-1-9 上午10:17:01
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.connector.mq;

import com.gs.csp.framework.connector.AbstractSendConnector;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 前端正向MQ发送器
 */
public class SnfncPositiveBranchMqSendConnector extends AbstractSendConnector<SnfncPositiveResponseMessage, SnfncPositiveRequestMessage> {
    /**
     * 消息管理
     */
    private JmsTemplate jmsTemplate;

    /**
     * 消息队列
     */
    private Queue queue;

    /**
     * 文件发送器
     */
    private AbstractSendConnector<SnfncPositiveResponseMessage, SnfncPositiveRequestMessage> fileSendConnector;

    private SnfncPositivePersistence snfncPositivePersistence;

    /**
     * 发送
     *
     * @param responseMessage 正向响应对象
     * @return null 无返回
     * @throws Exception 异常
     */
    public SnfncPositiveRequestMessage link(SnfncPositiveResponseMessage responseMessage) throws Exception {
        String filePath = responseMessage.getFilePath();
        if (this.fileSendConnector != null && (filePath != null && filePath.trim().length() > 0) ) this.fileSendConnector.send(responseMessage);
        String responseString = SnfncPositiveToolkit.transferResponseMessage(responseMessage.getContent(), this.getNegativeCode(responseMessage.getContent()));
        // 加密响应消息
        final byte[] responseBytes = this.encrypt(responseString);
        this.jmsTemplate.send(queue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(responseBytes);
                return objectMessage;
            }
        });
        return null;
    }

    private byte[] encrypt(String responseText) throws Exception {
        byte[] bytes = responseText.getBytes(SnfncPositiveToolkit.POSITIVE_ENCODING);
        if (this.cryptography != null) bytes = this.cryptography.encrypt(bytes);
        return bytes;
    }

    /**
	 *
	 * 查询反向客户代码
	 *
	 * @param newResponseString 反向响应消息
	 * @return  反向客户代码
	 * @throws org.dom4j.DocumentException 异常
	 */
	private String getNegativeCode(String newResponseString) throws DocumentException {
		Node headNode= DocumentHelper.parseText(newResponseString).selectSingleNode("/positive-response/head");
		String serialNumber = headNode.selectSingleNode("serial-number").getText();
        String positiveCode = headNode.selectSingleNode("client-code").getText();
        String businessCode = headNode.selectSingleNode("business-code").getText();
		//查询 反向客户代码
		String nagativeCode = snfncPositivePersistence.queryNegativeCode(serialNumber,positiveCode, businessCode);
		//查完删除数据
		snfncPositivePersistence.deletePositiveFormerMessage(serialNumber, positiveCode, businessCode);
		return nagativeCode;
	}

    public String getModel() {
        return Toolkit.MODEL_NONSYNCHRONOUS;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public void setFileSendConnector(AbstractSendConnector<SnfncPositiveResponseMessage, SnfncPositiveRequestMessage> fileSendConnector) {
        this.fileSendConnector = fileSendConnector;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }
}
