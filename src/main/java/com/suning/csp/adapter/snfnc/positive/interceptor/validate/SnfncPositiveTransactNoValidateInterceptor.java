package com.suning.csp.adapter.snfnc.positive.interceptor.validate;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.gs.csp.framework.exception.ValidateException;
import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;

/**
 * 付款单号校验拦截
 * 
 * @author 11070727
 */
public class SnfncPositiveTransactNoValidateInterceptor implements Interceptable<SnfncPositiveRequestMessage> {
    /**
     * 持久化组件
     */
    private SnfncPositivePersistence snfncPositivePersistence;

    /**
     * 付款单号检验重复描述
     */
    private String validatePIDResultText = "transact no validate conflict";

    public SnfncPositiveRequestMessage intercept(SnfncPositiveRequestMessage message) throws Exception {
        Document document = DocumentHelper.parseText(message.getContent());
        Node bodyNode = document.selectSingleNode("/positive-request/body");
        Node transactNoNode = bodyNode.selectSingleNode("transact-no");
        String transactNo = transactNoNode.getText();
        // 删除transact-no节点
        ((Element) bodyNode).remove(transactNoNode);
        message.setContent(document.asXML());
        // 查询非此笔交易相同的付款单号数据
        List<Map<String, Object>> transactList = this.snfncPositivePersistence.queryPositiveTransactMessage(message,
                transactNo);
        if (transactList.size() == 0) { // 校验通过
            return message;
        } else if (transactList.size() < 0) {
            throw new RuntimeException("query same transact-no in exception,records size =  " + transactList.size());
        }
        String businessCode = message.getBusinessCode();
        // 将0050转换成0003
        if ("0050".equals(businessCode)) {
            businessCode = "0003";
        }
        for (Map<String, Object> map : transactList) {
            if (!this.snfncPositivePersistence.validatePayStatus(businessCode, map.get("ID").toString())) {
                throw new ValidateException(validatePIDResultText);
            }
        }
        return message;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }

    public void setValidatePIDResultText(String validatePIDResultText) {
        this.validatePIDResultText = validatePIDResultText;
    }
}
