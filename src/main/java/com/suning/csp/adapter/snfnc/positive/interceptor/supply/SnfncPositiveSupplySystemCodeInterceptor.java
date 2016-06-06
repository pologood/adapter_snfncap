package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;

/**
 * 补充请求消息系统标识拦截器
 * 
 * @author 11070727
 */
public class SnfncPositiveSupplySystemCodeInterceptor implements Interceptable<SnfncPositiveRequestMessage> {
    /**
     * 需加系统标识的银行
     */
    private Set<String> bankCodeSet;

    /**
     * 系统标识转换关系map
     */
    private Map<String, String> systemCodeMap;

    public SnfncPositiveRequestMessage intercept(SnfncPositiveRequestMessage message) throws Exception {
        // 交易代码
        String businessCode = message.getBusinessCode();
        // 解析消息
        Document requestDocument = DocumentHelper.parseText(message.getContent());
        // 查询类交易bank-code标签在body下
        Node bankCodeNode = requestDocument.selectSingleNode("/positive-request/body/bank-code");
        if (bankCodeNode != null) {
            Element bodyElement = (Element) requestDocument.selectSingleNode("/positive-request/body");
            bodyElement.addElement("system-code").setText(this.selectSystemCode(bankCodeNode.getText(), businessCode));
        } else {
            // 付款类交易bank-code标签在body/account下
            bankCodeNode = requestDocument.selectSingleNode("/positive-request/body/account/bank-code");
            if (bankCodeNode != null) {
                Element accountElement = (Element) requestDocument.selectSingleNode("/positive-request/body/account");
                accountElement.addElement("system-code").setText(this.selectSystemCode(bankCodeNode.getText(), businessCode));
            }
        }
        message.setContent(requestDocument.asXML());
        return message;
    }
    
    /**
     * 
     * 选项系统标识
     *
     * @param bankCode     银行代码
     * @param businessCode 交易代码
     * @return 系统标识
     */
    private String selectSystemCode(String bankCode, String businessCode) {
        String systemCode = "";
        if (bankCodeSet.contains(bankCode)) {
            // 根据bankCode+businessCode在map查找systemCode
            systemCode = systemCodeMap.get(bankCode + businessCode);
        }
        return systemCode;
    }

    public void setBankCodeSet(Set<String> bankCodeSet) {
        this.bankCodeSet = bankCodeSet;
    }

    public void setBankCodes(String bankCodes) {
        String[] bankCodeArray = bankCodes.split(",");
        this.bankCodeSet = new HashSet<String>();
        for (String bankCode : bankCodeArray) {
            if (this.bankCodeSet.contains(bankCode)) {
                throw new RuntimeException(" bankCode repeat");
            } else {
                this.bankCodeSet.add(bankCode);
            }
        }
    }

    public void setSystemCodeMap(Map<String, String> systemCodeMap) {
        this.systemCodeMap = systemCodeMap;
    }
}
