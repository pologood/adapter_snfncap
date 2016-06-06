package com.suning.csp.adapter.snfnc.positive.dispatcher;

import com.gs.csp.framework.dispatcher.AbstractSendDispatcher;
import com.gs.csp.framework.dispatcher.AcceptDispatchable;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;

/**
 * 前端正向分支调度器
 *
 * @author zhangpengcheng
 */
public class SnfncPositiveBranchSendDispatcher extends AbstractSendDispatcher<SnfncPositiveResponseMessage, SnfncPositiveRequestMessage> {

    protected AcceptDispatchable<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage> trunkAcceptDispatcher;

    @Override
    protected SnfncPositiveRequestMessage link(SnfncPositiveResponseMessage positiveResponseMessage) throws Exception {
        return this.processor.getSendConnector(positiveResponseMessage.getClientCode(), Toolkit.MODEL_NONSYNCHRONOUS).send(positiveResponseMessage);
    }

    protected SnfncPositiveResponseMessage interceptOutputMessage(SnfncPositiveResponseMessage outputMessage) throws Exception {
        return this.trunkAcceptDispatcher.interceptOutputMessage(outputMessage);
    }

    public void setTrunkAcceptDispatcher(AcceptDispatchable<SnfncPositiveRequestMessage, SnfncPositiveResponseMessage> trunkAcceptDispatcher) {
        this.trunkAcceptDispatcher = trunkAcceptDispatcher;
    }
}
