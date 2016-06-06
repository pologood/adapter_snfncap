package com.suning.csp.adapter.snfnc.negative.dispatcher;

import com.gs.csp.framework.dispatcher.AbstractSendDispatcher;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeRequestMessage;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;

/**
 * 反向主干发送调度器
 *
 * @author zhangpengcheng
 */
public class SnfncNegativeTrunkSendDispatcher extends AbstractSendDispatcher<SnfncNegativeRequestMessage, SnfncNegativeResponseMessage> {

    @Override
    protected SnfncNegativeResponseMessage link(SnfncNegativeRequestMessage negativeRequestMessage) throws Exception {
        return this.processor.getSendConnector(negativeRequestMessage.getClientCode(), negativeRequestMessage.getModel()).send(negativeRequestMessage);
    }
}
