package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;

/**
 * 付款查询请求付款凭证转换拦截器
 * 
 * @author 11070727
 */
public class SnfncPositiveSupplyPayResultRequestInterceptor implements Interceptable<SnfncPositiveRequestMessage> {
    /**
     * 持久化组件
     */
    private SnfncPositivePersistence snfncPositivePersistence;

    /**
     * 转换的付款交易代码
     */
    private String transBusinessCode = "0004";

    /**
     * 交易代码集合
     */
    private Set<String> businessCodeSet;

    public SnfncPositiveRequestMessage intercept(SnfncPositiveRequestMessage message) throws Exception {
        if (this.businessCodeSet.contains(message.getBusinessCode())) {
            Document requestDocument = DocumentHelper.parseText(message.getContent());
            Node tvNode = requestDocument.selectSingleNode("/positive-request/body/transact-voucher");
            // 保存transact-voucher
            this.snfncPositivePersistence.updateTransactVoucher(message, tvNode.getText());
            // 将应用端发来的付款凭证转成内部付款凭证(值跟付款ID一样)
            tvNode.setText(this.snfncPositivePersistence.queryPayIDByTV(message, tvNode.getText()));
            // 若查询交易代码为0008，则需将此次查询改成0004
            if ("0008".equals(message.getBusinessCode())) {
                message.setBusinessCode(transBusinessCode);
                requestDocument.selectSingleNode("/positive-request/head/business-code").setText(transBusinessCode);
            }
            message.setContent(requestDocument.asXML());
        }
        return message;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }

    public void setTransBusinessCode(String transBusinessCode) {
        this.transBusinessCode = transBusinessCode;
    }

    public void setBusinessCodeSet(String businessCodeSet) {
        this.businessCodeSet = new HashSet<String>();
        String[] businessCodes = businessCodeSet.split(",");
        for (String businessCode : businessCodes) {
            if (this.businessCodeSet.contains(businessCode)) {
                throw new RuntimeException(" bankCode repeat");
            } else {
                this.businessCodeSet.add(businessCode);
            }
        }
    }
}
