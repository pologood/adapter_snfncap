package com.suning.csp.adapter.snfnc.negative.interceptor.supply;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.gs.csp.framework.interceptor.Interceptable;
import com.gs.csp.framework.message.HostRequestMessage;

/**
 * 主动推送请求消息body内容补充拦截
 * 
 * @author 11070727
 */
public class Snfnc0072NegativeSupplyPushPayResultRequestMessageInterceptor implements Interceptable<HostRequestMessage> {

    public HostRequestMessage intercept(HostRequestMessage message) throws Exception {
        Element body = (Element) DocumentHelper.parseText(message.getContent()).selectSingleNode("/body");
        //转化建行重客、网银、跨境银行编码
        if("ccbvip".equals(message.getPositiveClientCode()) || "ccbebs".equals(message.getPositiveClientCode())
        		|| "ccbcbs".equals(message.getPositiveClientCode())){
        	message.setPositiveClientCode("ccb");
        }
        if("citiccbs".equals(message.getPositiveClientCode())){
        	message.setPositiveClientCode("citic");
        }
        body.addElement("bank-code").setText(message.getPositiveClientCode());
        // 删除app-code和system-code
        body.remove(((Node)body).selectSingleNode("app-code"));
        body.remove(((Node)body).selectSingleNode("system-code"));
        message.setContent(body.asXML());
        return message;
    }
}
