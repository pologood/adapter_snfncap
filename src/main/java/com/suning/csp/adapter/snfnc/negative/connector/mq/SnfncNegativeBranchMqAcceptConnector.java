package com.suning.csp.adapter.snfnc.negative.connector.mq;

import com.gs.csp.framework.connector.AbstractAcceptConnector;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeRequestMessage;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;
import com.suning.csp.adapter.snfnc.negative.toolkit.SnfncNegativeToolkit;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * 前端反向接收器
 * 
 * @author zhangpengcheng
 */
public class SnfncNegativeBranchMqAcceptConnector extends
        AbstractAcceptConnector<SnfncNegativeResponseMessage, SnfncNegativeRequestMessage> {

    private DefaultMessageListenerContainer listenerContainer;

    public void start() {
        this.listenerContainer.start();
    }

    public void stop() {
        this.listenerContainer.setAcceptMessagesWhileStopping(true);
        this.listenerContainer.stop();
    }
    /**
	 *
	 * 转换为新报文
	 *
	 * @param responseString 响应报文
	 * @return 新报文
	 * @throws DocumentException 异常
	 */
	private String transferResponseString(String responseString) throws DocumentException {
		Document document= DocumentHelper.parseText(responseString);
		Element headElement=(Element)document.selectSingleNode("/negative-response/head");
		headElement.addElement("client-code").setText(headElement.selectSingleNode("negative-code").getText());
		headElement.remove((headElement).selectSingleNode("positive-code"));
		headElement.remove((headElement).selectSingleNode("negative-code"));
		return document.asXML();
	}

    public void acceptHandle(byte[] cipherText) throws Exception {
        // 解密消息
        String responseString = this.decrypt(cipherText);
        // 创建响应对象
        SnfncNegativeResponseMessage negativeResponseMessage = SnfncNegativeToolkit.createNegativeResponseMessage(transferResponseString(responseString));
        this.accept(negativeResponseMessage);
    }

    @Override
    protected SnfncNegativeRequestMessage link(SnfncNegativeResponseMessage negativeResponseMessage) throws Exception {
        return this.processor.getAcceptDispatcher().dispatch(negativeResponseMessage);
    }

    private String decrypt(byte[] bytes) throws Exception {
        if (this.cryptography != null) {
            bytes = this.cryptography.decrypt(bytes);
        }
        return new String(bytes, SnfncNegativeToolkit.NEGATIVE_ENCODING);
    }

    public String getModel() {
        return Toolkit.MODEL_NONSYNCHRONOUS;
    }

    public void setListenerContainer(DefaultMessageListenerContainer listenerContainer) {
        this.listenerContainer = listenerContainer;
    }
}