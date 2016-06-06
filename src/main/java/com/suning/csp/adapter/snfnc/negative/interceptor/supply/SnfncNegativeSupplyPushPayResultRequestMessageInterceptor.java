package com.suning.csp.adapter.snfnc.negative.interceptor.supply;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.gs.csp.framework.interceptor.Interceptable;
import com.gs.csp.framework.message.HostRequestMessage;
import com.suning.csp.adapter.snfnc.negative.persistence.SnfncNegativePersistence;
import com.suning.csp.adapter.snfnc.negative.toolkit.SnfncNegativeToolkit;

/**
 * 主动推送请求消息body内容补充拦截
 * 
 * @author 11070727
 */
public class SnfncNegativeSupplyPushPayResultRequestMessageInterceptor implements Interceptable<HostRequestMessage> {
    /**
     * 持久化组件
     */
    private SnfncNegativePersistence snfncNegativePersistence;

    /**
     * 重发时付款单号重复的付款类业务代码
     */
    private static Set<String> repeatPayBusinessCodeSet = new HashSet<String>();

    static {
        // 重发时付款单号会重复的交易
        repeatPayBusinessCodeSet.add("0003");
        repeatPayBusinessCodeSet.add("0005");
        repeatPayBusinessCodeSet.add("0007");
        repeatPayBusinessCodeSet.add("0050");
    }

    public HostRequestMessage intercept(HostRequestMessage message) throws Exception {
        Element body = (Element) DocumentHelper.parseText(message.getContent()).selectSingleNode("/body");
        String hostTV = body.selectSingleNode("transact-voucher").getText();
        String hostTS = body.selectSingleNode("transact-status").getText();
        Map<String, Object> positiveMap = this.snfncNegativePersistence.queryPayMessageByID(hostTV);
        String payBusinessCode = positiveMap.get("BUSINESS_CODE").toString();
        // 转化建行重客、网银、跨境银行编码
        if ("ccbvip".equals(message.getPositiveClientCode()) || "ccbebs".equals(message.getPositiveClientCode())
                || "ccbcbs".equals(message.getPositiveClientCode())) {
            message.setPositiveClientCode("ccb");
        }
        if ("citiccbs".equals(message.getPositiveClientCode())) {
            message.setPositiveClientCode("citic");
        }
        body.addElement("bank-code").setText(message.getPositiveClientCode());
        // 设置对应的付款查询交易代码
        body.addElement("result-business").setText(SnfncNegativeToolkit.P_R_BUSINESS_MAP.get(payBusinessCode));
        // 转换付款凭证
        if (repeatPayBusinessCodeSet.contains(payBusinessCode)) {
            body.selectSingleNode("transact-voucher").setText(positiveMap.get("SERIAL_NUMBER").toString());
        }
        // 转换交易状态
        body.selectSingleNode("transact-status").setText(SnfncNegativeToolkit.H_N_TRANSACT_STATUS_MAP.get(hostTS));
        // 删除app-code和system-code
        body.remove(((Node) body).selectSingleNode("app-code"));
        body.remove(((Node) body).selectSingleNode("system-code"));
        message.setContent(body.asXML());
        return message;
    }

    public void setSnfncNegativePersistence(SnfncNegativePersistence snfncNegativePersistence) {
        this.snfncNegativePersistence = snfncNegativePersistence;
    }
}
