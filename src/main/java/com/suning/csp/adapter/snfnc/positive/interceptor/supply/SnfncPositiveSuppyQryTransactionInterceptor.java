/*
 * Copyright (C), 2002-2016, 苏宁易购电子商务有限公司
 * FileName: SnfncPositiveSuppyQryTransactionInterceptor.java
 * Author:   12061777
 * Date:     2016-3-23 上午11:38:23
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.gs.csp.framework.interceptor.Interceptable;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;

/**
 * 查询响应消息添加请求的凭证
 * 〈功能详细描述〉
 *
 * @author 12061777
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SnfncPositiveSuppyQryTransactionInterceptor implements Interceptable<SnfncPositiveResponseMessage> {
   
    /**
     * 持久化组件
     */
    private SnfncPositivePersistence snfncPositivePersistence;
    
    @Override
    public SnfncPositiveResponseMessage intercept(SnfncPositiveResponseMessage message) throws Exception {
        Document responseDocument = DocumentHelper.parseText(message.getContent());
        //获取body节点
        Node bodyNode = responseDocument.selectSingleNode("/positive-response/body");
        if(bodyNode==null){
            Element rootElement = responseDocument.getRootElement();
            rootElement.addElement("body").addElement("transact-voucher").setText(snfncPositivePersistence.queryTransactVoucher(message));
        }else{
            ((Element)bodyNode).addElement("transact-voucher").setText(snfncPositivePersistence.queryTransactVoucher(message));
        }
        
        message.setContent(responseDocument.asXML());
        return message;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    }
    
}


