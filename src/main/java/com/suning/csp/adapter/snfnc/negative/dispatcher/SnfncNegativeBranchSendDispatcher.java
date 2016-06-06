package com.suning.csp.adapter.snfnc.negative.dispatcher;

import com.gs.csp.framework.dispatcher.AbstractSendDispatcher;
import com.gs.csp.framework.dispatcher.AcceptDispatchable;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.gs.csp.framework.toolkit.Toolkit;

/**
 * 反向分支发送调度器
 *
 * @author zhangpengcheng
 */
public class SnfncNegativeBranchSendDispatcher extends AbstractSendDispatcher<HostResponseMessage, HostRequestMessage> {

    protected AcceptDispatchable<HostRequestMessage, HostResponseMessage> trunkAcceptDispatcher;

    @Override
    protected HostRequestMessage link(HostResponseMessage hostResponseMessage) throws Exception {
        return this.processor.getSendConnector(hostResponseMessage.getNegativeClientCode(), Toolkit.MODEL_NONSYNCHRONOUS).send(hostResponseMessage);
    }

    protected HostResponseMessage interceptOutputMessage(HostResponseMessage outputMessage) throws Exception {
        return this.trunkAcceptDispatcher.interceptOutputMessage(outputMessage);
    }

    public void setTrunkAcceptDispatcher(AcceptDispatchable<HostRequestMessage, HostResponseMessage> trunkAcceptDispatcher) {
        this.trunkAcceptDispatcher = trunkAcceptDispatcher;
    }
}
