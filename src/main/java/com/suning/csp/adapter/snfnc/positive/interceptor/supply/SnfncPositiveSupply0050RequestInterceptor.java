package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;

/**
 * 单笔公对私付款转0003拦截
 * 
 * @author 11070727
 */
public class SnfncPositiveSupply0050RequestInterceptor implements Interceptable<SnfncPositiveRequestMessage> {
    /**
     * 0050转换的付款交易代码
     */
    private String transBusinessCode = "0003";
    
    /**
     * 交易代码
     */
    private String businessCode = "0050";

    /**
     * 付款标识,1代表单笔公对私
     */
    private String payType = "1";

    public SnfncPositiveRequestMessage intercept(SnfncPositiveRequestMessage message) throws Exception {
        if (businessCode.equals(message.getBusinessCode())) {
            Document requestDocument = DocumentHelper.parseText(message.getContent());
            requestDocument.selectSingleNode("/positive-request/head/business-code").setText(transBusinessCode);
            // 付款信息字段调整
            Element accountNode = (Element) requestDocument.selectSingleNode("/positive-request/body/account");
            accountNode.addElement("bank-name");
            accountNode.addElement("area-code");
            // 区分单笔公对私以及对公付款交易标识
            accountNode.addElement("pay-type").setText(payType);
            // 收款信息字段调整
            Element oppAccountNode = (Element) requestDocument.selectSingleNode("/positive-request/body/opp-account");
            oppAccountNode.addElement("bank-name");
            oppAccountNode.addElement("bank-address");
            oppAccountNode.addElement("bank-no");
            oppAccountNode.addElement("inner-bank-no");
            oppAccountNode.addElement("swift-code");
            // body信息字段调整
            Element bodyNode = (Element) requestDocument.selectSingleNode("/positive-request/body");
            bodyNode.addElement("currency");
            // 同行
            bodyNode.addElement("same-bank").setText("0");
            bodyNode.addElement("same-city");
            message.setContent(requestDocument.asXML());
            message.setBusinessCode(transBusinessCode);
        }
        return message;
    }

    public void setTransBusinessCode(String transBusinessCode) {
        this.transBusinessCode = transBusinessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
