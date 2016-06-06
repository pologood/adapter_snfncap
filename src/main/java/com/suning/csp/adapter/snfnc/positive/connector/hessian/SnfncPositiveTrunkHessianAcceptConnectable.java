/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: HessianServiceConnector.java
 * Author:   11070727
 * Date:     2014-1-9 上午10:20:43
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.connector.hessian;

/**
 * hessian接口
 *
 * @author 11070727
 */
public interface SnfncPositiveTrunkHessianAcceptConnectable {
    /**
     * 接口方法
     *
     * @param requestBytes 请求消息
     * @return 响应消息
     * @throws Exception 异常
     */
    public byte[] acceptHandle(byte[] requestBytes) throws Exception;

}
