package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;

/**
 * 付款返回凭证转换代理
 * 
 * @author 11070727
 */
public class SnfncPositiveSupplyPayResponseInterceptor implements Interceptable<SnfncPositiveResponseMessage> {
    /**
     * 持久化组件
     */
    private SnfncPositivePersistence snfncPositivePersistence;

    public SnfncPositiveResponseMessage intercept(SnfncPositiveResponseMessage message) throws Exception {
        Document responseDocument = DocumentHelper.parseText(message.getContent());
        // 得到付款凭证节点
        Node tvNode = responseDocument.selectSingleNode("/positive-response/body/transact-voucher");
        if (tvNode != null) { // 若节点不为空，则进行凭证转换
            // 得到对应付款的流水号
            String paySn = this.snfncPositivePersistence.querySerialNumber(message);
            tvNode.setText(paySn);
            message.setContent(responseDocument.asXML());
            // 更新付款凭证
            this.snfncPositivePersistence.updateTransactVoucher(message, paySn);
        } else {
            if( SnfncPositiveToolkit.POSITIVE_TRANSACT_STATUS_UNKNOWN.equals(message.getResultCode())){
                Node bodyNode = responseDocument.selectSingleNode("/positive-response/body");
                if(bodyNode==null){
                    Element rootElement = responseDocument.getRootElement();
                    rootElement.addElement("body").addElement("transact-voucher");
                }else{
                    ((Element)bodyNode).addElement("transact-voucher");
                }
                message.setContent(responseDocument.asXML());
            }
        }
        return message;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }
}
