package com.suning.csp.adapter.snfnc.negative.interceptor.supply;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;
import com.suning.csp.adapter.snfnc.negative.persistence.SnfncNegativePersistence;

/**
 * 
 * 补充反向响应消息
 * 
 * @author zhangpengcheng
 */
public class SnfncNegativeSupplyMessageInterceptor implements Interceptable<SnfncNegativeResponseMessage> {

    private SnfncNegativePersistence snfncNegativePersistence;

    public SnfncNegativeResponseMessage intercept(SnfncNegativeResponseMessage negativeResponseMessage)
            throws Exception {
        // 设置反向消息ID
        negativeResponseMessage.setMessageId(this.snfncNegativePersistence.queryNegativeMessageId(negativeResponseMessage));
        // 设置hostmessageid
        negativeResponseMessage.setHostMessageId(negativeResponseMessage.getSerialNumber());
        return negativeResponseMessage;
    }

    public void setSnfncNegativePersistence(SnfncNegativePersistence snfncNegativePersistence) {
        this.snfncNegativePersistence = snfncNegativePersistence;
    }
}
