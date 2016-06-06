package com.suning.csp.adapter.snfnc.positive.interceptor.save;

import java.util.HashSet;
import java.util.Set;

import org.dom4j.DocumentHelper;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;

/**
 * 付款单号初始化拦截器
 * 
 * @author 11070727
 */
public class SnfncPositiveSavePayMessageInterceptor implements Interceptable<SnfncPositiveRequestMessage> {
    /**
     * 持久化组件
     */
    private SnfncPositivePersistence snfncPositivePersistence;

    /**
     * 付款类交易代码set
     */
    private Set<String> payBusinessCodesSet;

    public SnfncPositiveRequestMessage intercept(SnfncPositiveRequestMessage message) throws Exception {
        if (payBusinessCodesSet.contains(message.getBusinessCode())) {
            // 付款单号
            String transactNo = DocumentHelper.parseText(message.getContent()).selectSingleNode("/positive-request/body/transact-no").getText();
            this.snfncPositivePersistence.initializePayMessage(message,transactNo);
        }
        return message;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }

    public void setPayBusinessCodeSet(Set<String> payBusinessCodesSet) {
        this.payBusinessCodesSet = payBusinessCodesSet;
    }
    
    /**
     * 
     * 设置付款交易代码set
     *
     * @param payBusienssCodes
     */
    public void setPayBusinessCodes(String payBusienssCodes){
        String[] payBusienssCodesArray = payBusienssCodes.split(",");
        this.payBusinessCodesSet=new HashSet<String>();
        for (String payBusinessCode : payBusienssCodesArray) {
            if (this.payBusinessCodesSet.contains(payBusinessCode)) {
                throw new RuntimeException(" businessCode repeat");
            } else {
                this.payBusinessCodesSet.add(payBusinessCode);
            }
        }
    
    }
}
