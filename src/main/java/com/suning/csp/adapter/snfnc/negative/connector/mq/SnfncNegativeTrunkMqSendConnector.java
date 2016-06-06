package com.suning.csp.adapter.snfnc.negative.connector.mq;

import com.gs.csp.framework.connector.AbstractSendConnector;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeRequestMessage;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;
import com.suning.csp.adapter.snfnc.negative.toolkit.SnfncNegativeToolkit;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * MQ发送器
 * 
 * @author zhangpengcheng
 */
public class SnfncNegativeTrunkMqSendConnector extends
        AbstractSendConnector<SnfncNegativeRequestMessage, SnfncNegativeResponseMessage> {
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
    private AbstractSendConnector<SnfncNegativeRequestMessage, SnfncNegativeResponseMessage> fileSendConnector;

    public SnfncNegativeResponseMessage link(SnfncNegativeRequestMessage negativeRequestMessage) throws Exception {
        String filePath=negativeRequestMessage.getFilePath();
        if (fileSendConnector != null && (filePath!=null && filePath.trim().length()>0))
            this.fileSendConnector.send(negativeRequestMessage);
        // 加密
        final byte[] responseBytes = this.encrypt(transferRequestString(negativeRequestMessage.getContent()));
        this.jmsTemplate.send(queue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(responseBytes);
                return objectMessage;
            }
        });
        return null;
    }

    private byte[] encrypt(String responseString) throws Exception {
        byte[] bytes = responseString.getBytes(SnfncNegativeToolkit.NEGATIVE_ENCODING);
        if (this.cryptography != null) {
            bytes = this.cryptography.encrypt(bytes);
        }
        return bytes;
    }

    /**
    *
    * 转换为老报文
    *
    * @param requestString 请求新报文消息
    * @return  老报文消息
    * @throws org.dom4j.DocumentException 异常
    */
   private String transferRequestString(String requestString) throws DocumentException {
       Document document= DocumentHelper.parseText(requestString);
       Element headElement=(Element)document.selectSingleNode("/negative-request/head");
       headElement.addElement("positive-code").setText("bps");
       String clientCode=document.selectSingleNode("/negative-request/head/client-code").getText();
       headElement.addElement("negative-code").setText(clientCode);
       headElement.remove(document.selectSingleNode("/negative-request/head/client-code"));
       return document.asXML();
   }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public void setFileSendConnector(
            AbstractSendConnector<SnfncNegativeRequestMessage, SnfncNegativeResponseMessage> fileSendConnector) {
        this.fileSendConnector = fileSendConnector;
    }

    public String getModel() {
        return Toolkit.MODEL_NONSYNCHRONOUS;
    }
}
