/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * FileName: SupplyMessageInterceptorProxy.java
 * Author:   11070727
 * Date:     2014-1-21 下午06:00:56
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import com.gs.csp.framework.interceptor.Interceptable;
import com.gs.utility.converter.StringConverter;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Date;

/**
 * 补充消息头及content内容
 * 
 * @author 11070727
 */
public class SnfncPositiveSupplyMessageInterceptor implements Interceptable<SnfncPositiveResponseMessage> {
    /**
     * 拦截器
     */
    private Interceptable<SnfncPositiveResponseMessage> interceptor;

    public SnfncPositiveResponseMessage intercept(SnfncPositiveResponseMessage message) throws Exception {
        message = this.interceptor.intercept(message);
        // 补充消息content的值
        Document document = DocumentHelper.parseText(message.getContent());
        Element headElement = (Element) document.selectSingleNode("/positive-response/head");
        headElement.addElement("client-code").setText(StringConverter.null2blank(message.getClientCode()));
        headElement.addElement("serial-number").setText(StringConverter.null2blank(message.getSerialNumber()));
        headElement.addElement("business-code").setText(StringConverter.null2blank(message.getBusinessCode()));
        headElement.addElement("priority").setText(StringConverter.integer2string(message.getPriority()));
        headElement.addElement("version").setText(StringConverter.null2blank(message.getVersion()));
        headElement.addElement("result-text").setText(StringConverter.null2blank(message.getResultText()));
        headElement.addElement("response-time").setText(SnfncPositiveToolkit.POSITIVE_DATE_TIME_FORMAT.format(new Date()));
        message.setContent(document.asXML());
        return message;
    }

    public void setInterceptor(Interceptable<SnfncPositiveResponseMessage> interceptor) {
        this.interceptor = interceptor;
    }
}