package com.suning.csp.adapter.snfnc.negative.executor;

import com.gs.csp.framework.executor.AbstractSingleExecutor;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeRequestMessage;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;
import com.suning.csp.adapter.snfnc.negative.toolkit.SnfncNegativeToolkit;
import java.io.File;

/**
 * 反向主干处理器
 *
 * @author zhangpengcheng
 */
public class SnfncNegativeTrunkExecutor
        extends
        AbstractSingleExecutor<HostRequestMessage, SnfncNegativeRequestMessage, SnfncNegativeResponseMessage, HostResponseMessage> {
    /**
     * 外部请求文件目录
     */
    private File requestClientDirectory;

    /**
     * 内部请求文件目录
     */
    private File requestHostDirectory;

    /**
     * 外部响应文件目录
     */
    private File responseClientDirectory;

    /**
     * 内部响应文件目录
     */
    private File responseHostDirectory;

    protected SnfncNegativeRequestMessage accept2send(HostRequestMessage hostRequestMessage) throws Exception {
        return SnfncNegativeToolkit.createSnfncNegativeRequestMessage(hostRequestMessage,this.requestHostDirectory,this.requestClientDirectory);
    }

    protected SnfncNegativeResponseMessage link(SnfncNegativeRequestMessage negativeRequestMessage) throws Exception {
        return this.processor.getSendDispatcher().dispatch(negativeRequestMessage);
    }

    public HostResponseMessage send2accept(SnfncNegativeResponseMessage negativeResponseMessage) throws Exception {
        return SnfncNegativeToolkit.createHostResponseMessage(negativeResponseMessage,this.responseClientDirectory,this.responseHostDirectory);
    }

    public void setRequestClientDirectory(File requestClientDirectory) {
        this.requestClientDirectory = requestClientDirectory;
    }

    public void setRequestHostDirectory(File requestHostDirectory) {
        this.requestHostDirectory = requestHostDirectory;
    }

    public void setResponseClientDirectory(File responseClientDirectory) {
        this.responseClientDirectory = responseClientDirectory;
    }

    public void setResponseHostDirectory(File responseHostDirectory) {
        this.responseHostDirectory = responseHostDirectory;
    }
}
