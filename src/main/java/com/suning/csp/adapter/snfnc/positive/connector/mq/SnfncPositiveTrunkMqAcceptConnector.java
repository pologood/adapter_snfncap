/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: SnfncPositiveMqAcceptConnector.java
 * Author:   11070727
 * Date:     2014-1-9 上午10:14:09
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.connector.mq;

import com.gs.csp.framework.connector.AbstractAcceptConnector;
import com.gs.csp.framework.connector.SendConnectable;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * 前端正向MQ接收器
 *
 * @author 11070727
 */
public class SnfncPositiveTrunkMqAcceptConnector extends AbstractAcceptConnector<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage> {

    private DefaultMessageListenerContainer listenerContainer;

    private SendConnectable<SnfncPositiveResponseMessage, SnfncPositiveRequestMessage> sendConnector;

    private final Logger logger = Logger.getLogger(this.getClass());

    private SnfncPositivePersistence snfncPositivePersistence;

    public void start() {
        this.listenerContainer.start();
    }

    public void stop() {
        this.listenerContainer.setAcceptMessagesWhileStopping(true);
        this.listenerContainer.stop();
    }

    /**
     * 接收mq消息并进行处理
     *
     * @param requestBytes 加密内容
     * @throws Exception 异常
     */
	public void acceptHandle(byte[] requestBytes) throws Exception {
		String requestString = null;
    	try{
        // 解密
            requestString = this.decrypt(requestBytes);
            this.logger.info("accept from fund message successful ,@ message:" + requestString);
            String negativeCode = DocumentHelper.parseText(requestString).selectSingleNode(
            "/positive-request/head/negative-code").getText();
            requestString = SnfncPositiveToolkit.transferRequestMessage(requestString);
            saveMessage(negativeCode, requestString);
            this.logger.info("transfer message successful:" + requestString);
            // 创建正向请求对象
            SnfncPositiveRequestMessage requestMessage = SnfncPositiveToolkit.createPositiveRequestMessage(new SnfncPositiveRequestMessage(), requestString, this.getModel());
            SnfncPositiveResponseMessage responseMessage = this.accept(requestMessage);
            // 响应对象不为null，调用发送器
            if (responseMessage != null) {
                if (this.sendConnector != null) this.sendConnector.send(responseMessage);
                else this.logger.warn(this.code + ", no send connector for message. " + responseMessage);
            }
    	}catch (Exception e){
			this.logger.error("acceptHandle method throw exception,@message:" + requestString, e);
    	}
    }

	/**
	 * 解密请求数据
	 * @param requestBytes 加密数据
	 * @return 解密数据
	 * @throws Exception 异常
	 */
    private String decrypt(byte[] requestBytes) throws Exception {
        if (this.cryptography != null) requestBytes = this.cryptography.decrypt(requestBytes);
        return new String(requestBytes, SnfncPositiveToolkit.POSITIVE_ENCODING);
    }

/**
     *
     * 保存消息部分信息，用于响应添加
     *
     * @param requestMessage
     * @throws DocumentException
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    private void saveMessage(String negativeCode, String requestMessage) throws DocumentException {
        String serialNumber = DocumentHelper.parseText(requestMessage).selectSingleNode(
        "/positive-request/head/serial-number").getText();
        String clientCode = DocumentHelper.parseText(requestMessage).selectSingleNode(
        "/positive-request/head/client-code").getText();
        String businessCode = DocumentHelper.parseText(requestMessage).selectSingleNode(
        "/positive-request/head/business-code").getText();

        // 保存消息
        this.snfncPositivePersistence.saveMessage(serialNumber,
                clientCode, negativeCode, businessCode);
    }


    protected SnfncPositiveResponseMessage link(SnfncPositiveRequestMessage requestMessage) throws Exception {
        return this.processor.getAcceptDispatcher().dispatch(requestMessage);
    }

    public void setListenerContainer(DefaultMessageListenerContainer listenerContainer) {
        this.listenerContainer = listenerContainer;
    }

    public void setSendConnector(SendConnectable<SnfncPositiveResponseMessage, SnfncPositiveRequestMessage> sendConnector) {
        this.sendConnector = sendConnector;
    }

    public String getModel() {
        return Toolkit.MODEL_NONSYNCHRONOUS;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }
}
