/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: SupplyResponseMessageInterceptor.java
 * Author:   11070727
 * Date:     2014-1-21 下午04:08:18
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;

/**
 * 响应消息的处理拦截
 * 
 * @author 11070727
 */
public class SnfncPositiveSupplyResultTextInterceptor implements Interceptable<SnfncPositiveResponseMessage> {
    
    /**
     * map<交易类型标识, map<交易状态, 交易状态描述>>
     */
    private Map<String, Map<String, String>> resultParamMap;

    public SnfncPositiveResponseMessage intercept(SnfncPositiveResponseMessage message) throws Exception {
        String resultCode = message.getResultCode();
        String resultText = message.getResultText();
        String businessCode = message.getBusinessCode();

        // 判断是否填充resultText的值
        if (StringUtils.isBlank(resultText) && resultParamMap != null) {
            message.setResultText(resultParamMap.get(SnfncPositiveToolkit.getBusinessFlag(businessCode)).get(
                    resultCode));
        }
        return message;
    }

    public void setResultParamMap(Map<String, Map<String, String>> resultParamMap) {
        this.resultParamMap = resultParamMap;
    }

}
