package com.suning.csp.adapter.snfnc.negative.connector.http;

import com.gs.csp.framework.connector.AbstractSendConnector;
import com.gs.csp.framework.toolkit.Toolkit;
import com.gs.utility.net.HttpSendConnector;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeRequestMessage;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;
import com.suning.csp.adapter.snfnc.negative.toolkit.SnfncNegativeToolkit;

/**
 * http发送器
 *
 * @author zhangpengcheng
 */
public class SnfncNegativeTrunkHttpSendConnector extends AbstractSendConnector<SnfncNegativeRequestMessage, SnfncNegativeResponseMessage> {

    private HttpSendConnector httpSendConnector;

    protected SnfncNegativeResponseMessage link(SnfncNegativeRequestMessage requestMessage) throws Exception {
        String responseString =this.decrypt(this.httpSendConnector.send(this.encrypt(requestMessage.getContent())));
        //创建反向响应对象 
        return SnfncNegativeToolkit.createNegativeResponseMessage(responseString);
    }
    
    private byte[] encrypt(String requestString) throws Exception {
        byte[] bytes = requestString.getBytes(SnfncNegativeToolkit.NEGATIVE_ENCODING);
        if (this.cryptography != null) {
            bytes = this.cryptography.encrypt(bytes);
        }
        return bytes;
    }
    
    private String decrypt(byte[] bytes) throws Exception {
        if (this.cryptography != null) {
            bytes = this.cryptography.decrypt(bytes);
        }
        return new String(bytes, SnfncNegativeToolkit.NEGATIVE_ENCODING);
    }

    public String getModel() {
        return Toolkit.MODEL_SYNCHRONOUS;
    }

    public void setHttpSendConnector(HttpSendConnector httpSendConnector) {
        this.httpSendConnector = httpSendConnector;
    }
}
