package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;

/**
 * 
 * 增加区分付款标识
 * 
 * @author zhangpengcheng
 */
public class SnfncPositiveSupply0003RequestInterceptor implements Interceptable<SnfncPositiveRequestMessage> {
    /**
     * 付款标识
     */
    private String payType = "0";

    private String businessCode = "0003";

    public SnfncPositiveRequestMessage intercept(SnfncPositiveRequestMessage message) throws Exception {
        if (businessCode.equals(message.getBusinessCode())) {
            Document document = DocumentHelper.parseText(message.getContent());
            Element accountNode = (Element) document.selectSingleNode("/positive-request/body/account");
            // 区分单笔公对私以及对公付款交易标识,0代表单笔对公付款
            accountNode.addElement("pay-type").setText(payType);
            message.setContent(document.asXML());
        }
        return message;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }
}