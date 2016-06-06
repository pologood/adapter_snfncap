/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: SnfncPositiveTrunkChainExecutor.java
 * Author:   11070727
 * Date:     2014-1-9 上午09:59:34
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.executor;

import com.gs.csp.framework.executor.AbstractSingleExecutor;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;

import java.io.File;

/**
 * 前端正向主干处理器
 *
 * @author 11070727
 */
public class SnfncPositiveTrunkExecutor
        extends
        AbstractSingleExecutor<SnfncPositiveRequestMessage, HostRequestMessage, HostResponseMessage, SnfncPositiveResponseMessage> {
    /**
     * 外部请求文件目录
     */
    private File clientRequestDirectory;

    /**
     * 外部响应文件目录
     */
    private File clientResponseDirectory;

    /**
     * 内部请求文件目录
     */
    private File hostRequestDirectory;

    /**
     * 内部响应文件目录
     */
    private File hostResponseDirectory;

    /**
     * 持久化组件
     */
    private SnfncPositivePersistence snfncPositivePersistence;

    /**
     * 正向请求对象转换为内部请求对象
     *
     * @param positiveRequestMessage 正向请求对象
     * @return HostRequestMessage 内部请求对象
     * @throws Exception 异常
     */
    protected HostRequestMessage accept2send(SnfncPositiveRequestMessage positiveRequestMessage) throws Exception {
        return SnfncPositiveToolkit.createHostRequestMessage(positiveRequestMessage, this.clientRequestDirectory, this.hostRequestDirectory);
    }

    /**
     * 发送
     *
     * @param requestMessage 内部请求对象
     * @return HostResponseMessage 内部响应对象
     * @throws Exception 异常
     */
    protected HostResponseMessage link(HostRequestMessage requestMessage) throws Exception {
        return this.processor.getSendDispatcher().dispatch(requestMessage);
    }

    /**
     * 内部响应对象转换为正向响应对象
     *
     * @param hostResponseMessage 内部响应对象
     * @return SnfncPositiveResponseMessage 正向响应对象
     * @throws Exception 异常
     */
    public SnfncPositiveResponseMessage send2accept(HostResponseMessage hostResponseMessage) throws Exception {
        // 流水号
       //String serialNumber = this.snfncPositivePersistence.querySerialNumber(hostResponseMessage);
        //String filePath = serialNumber + SnfncPositiveToolkit.POSITIVE_FILE_EXTENSION_RESPONSE;
        // 创建正向响应对象
        return SnfncPositiveToolkit.createPositiveResponseMessage(hostResponseMessage, this.hostResponseDirectory,
                new SnfncPositiveResponseMessage(), this.clientResponseDirectory, hostResponseMessage.getFilePath());
    }

    public void setClientRequestDirectory(File clientRequestDirectory) {
        this.clientRequestDirectory = clientRequestDirectory;
    }

    public void setHostRequestDirectory(File hostRequestDirectory) {
        this.hostRequestDirectory = hostRequestDirectory;
    }

    public void setClientResponseDirectory(File clientResponseDirectory) {
        this.clientResponseDirectory = clientResponseDirectory;
    }

    public void setHostResponseDirectory(File hostResponseDirectory) {
        this.hostResponseDirectory = hostResponseDirectory;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }
}
