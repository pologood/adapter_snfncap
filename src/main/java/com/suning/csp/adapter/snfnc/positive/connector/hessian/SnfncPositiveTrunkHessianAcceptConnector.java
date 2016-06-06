/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: HessianServicImpl.java
 * Author:   11070727
 * Date:     2014-1-9 上午10:23:17
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.connector.hessian;

import com.gs.csp.framework.connector.AbstractAcceptConnector;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;

/**
 * hessian实现类
 *
 * @author 11070727
 */
public class SnfncPositiveTrunkHessianAcceptConnector extends AbstractAcceptConnector<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage> implements SnfncPositiveTrunkHessianAcceptConnectable {

    public void start() {
    }
    @Override
    public void stop() {

    }


    public byte[] acceptHandle(byte[] requestBytes) throws Exception {
        // 解密
        String requestText = this.decrypt(requestBytes);
        requestText = SnfncPositiveToolkit.transferRequestMessage(requestText);
        // 创建正向请求对象
        SnfncPositiveRequestMessage requestMessage = SnfncPositiveToolkit.createPositiveRequestMessage(new SnfncPositiveRequestMessage(), requestText, this.getModel());
        SnfncPositiveResponseMessage responseMessage = this.accept(requestMessage);
        String responseString = SnfncPositiveToolkit.transferResponseMessage(responseMessage.getContent(), "snfncap");
        // 加密响应消息返回
        return this.encrypt(responseString);
    }

    protected SnfncPositiveResponseMessage link(SnfncPositiveRequestMessage requestMessage) throws Exception {
        return this.processor.getAcceptDispatcher().dispatch(requestMessage);
    }

    private String decrypt(byte[] bytes) throws Exception {
        if (this.cryptography != null) bytes = this.cryptography.decrypt(bytes);
        return new String(bytes, SnfncPositiveToolkit.POSITIVE_ENCODING);
    }

    private byte[] encrypt(String responseText) throws Exception {
        byte[] bytes = responseText.getBytes(SnfncPositiveToolkit.POSITIVE_ENCODING);
        if (this.cryptography != null) bytes = this.cryptography.encrypt(bytes);
        return bytes;
    }

    public String getModel() {
        return Toolkit.MODEL_SYNCHRONOUS;
    }
}
