package com.suning.csp.adapter.snfnc.negative.interceptor.supply;

import com.gs.csp.framework.interceptor.Interceptable;
import com.gs.csp.framework.message.HostResponseMessage;
import com.suning.csp.adapter.snfnc.negative.persistence.SnfncNegativePersistence;

/**
 * 
 * 补充内部响应对象
 * 
 * @author zhangpengcheng
 */
public class SnfncNegativeSupplyHostResponseMessageInterceptor implements Interceptable<HostResponseMessage> {
    /**
     *  持久化
     */
    private SnfncNegativePersistence snfncNegativePersistence;

    public HostResponseMessage intercept(HostResponseMessage message) throws Exception {
        // 设置内部对象的agentPositiveClientCode属性
        message.setAgentPositiveClientCode(this.snfncNegativePersistence.queryAgentPositiveCode(message.getMessageId()));
        return message;
    }

    public void setSnfncNegativePersistence(SnfncNegativePersistence snfncNegativePersistence) {
        this.snfncNegativePersistence = snfncNegativePersistence;
    }
}