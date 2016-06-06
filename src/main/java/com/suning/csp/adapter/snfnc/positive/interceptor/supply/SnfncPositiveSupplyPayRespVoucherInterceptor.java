package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;

/**
 * 付款响应拦截,未知状态处理
 * 
 * @author 14111532
 */
public class SnfncPositiveSupplyPayRespVoucherInterceptor implements Interceptable<SnfncPositiveResponseMessage> {

    public SnfncPositiveResponseMessage intercept(SnfncPositiveResponseMessage message) throws Exception {
        Document responseDocument = DocumentHelper.parseText(message.getContent());
        // 得到付款凭证节点
        Node tvNode = responseDocument.selectSingleNode("/positive-response/body/transact-voucher");
        if (tvNode == null) {
            if (SnfncPositiveToolkit.POSITIVE_TRANSACT_STATUS_UNKNOWN.equals(message.getResultCode())) {
                Node bodyNode = responseDocument.selectSingleNode("/positive-response/body");
                if (bodyNode == null) {
                    Element rootElement = responseDocument.getRootElement();
                    rootElement.addElement("body").addElement("transact-voucher").setText(message.getMessageId());
                } else {
                    ((Element) bodyNode).addElement("transact-voucher").setText(message.getMessageId());
                }
                message.setContent(responseDocument.asXML());
            }
        }
        return message;
    }
}
