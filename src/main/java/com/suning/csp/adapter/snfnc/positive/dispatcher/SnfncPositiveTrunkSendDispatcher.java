/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: GeneralSendDispatcher.java
 * Author:   11070727
 * Date:     2014-1-9 上午10:25:16
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.dispatcher;

import com.gs.csp.framework.dispatcher.AbstractSendDispatcher;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;

/**
 * 前端正向发送调度器
 *
 * @author 11070727
 */
public class SnfncPositiveTrunkSendDispatcher extends AbstractSendDispatcher<HostRequestMessage, HostResponseMessage> {

    @Override
    protected HostResponseMessage link(HostRequestMessage hostRequestMessage) throws Exception {
        return this.processor.getSendConnector(hostRequestMessage.getPositiveClientCode(), hostRequestMessage.getModel()).send(hostRequestMessage);
    }
}
