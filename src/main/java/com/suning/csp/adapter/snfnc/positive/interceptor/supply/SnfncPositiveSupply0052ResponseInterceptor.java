package com.suning.csp.adapter.snfnc.positive.interceptor.supply;

import com.gs.csp.framework.interceptor.Interceptable;
import com.gs.csp.framework.toolkit.Toolkit;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;
import com.suning.csp.adapter.snfnc.positive.persistence.SnfncPositivePersistence;
import com.suning.csp.adapter.snfnc.positive.toolkit.SnfncPositiveToolkit;

import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 
 * 0051 币种余额查询拦截器 
 *
 * @author 14050188
 * 2014-8-1上午11:10:04
 */
public class SnfncPositiveSupply0052ResponseInterceptor implements Interceptable<SnfncPositiveResponseMessage> {

    private SnfncPositivePersistence snfncPositivePersistence;
    
    @Override
    public SnfncPositiveResponseMessage intercept(SnfncPositiveResponseMessage message) throws Exception {       
        //响应报文添加币种表示
        if (message != null) {
            String currency = this.snfncPositivePersistence.query0052Currency(message);
            String accountNo = this.snfncPositivePersistence.query0052AccountNo(message);
            String queryDate = this.snfncPositivePersistence.query0052QueryDate(message);  //yyyy-MM-dd HH:mm:ss
            if (!SnfncPositiveToolkit.POSITIVE_TRANSACT_STATUS_SUCCESS.equals(message.getResultCode())) {
                Document responseDocument = DocumentHelper.parseText(message.getContent());
                Element rootElement = (Element) responseDocument.selectSingleNode("/positive-response");
                Element bodyElement = (Element) responseDocument.selectSingleNode("/positive-response/body");
                if (bodyElement == null) {
                    bodyElement = rootElement.addElement("body");
                }
                bodyElement.addElement("currency").setText(currency);
                bodyElement.addElement("account-no").setText(accountNo);
                if (queryDate != null && queryDate.length() > 10) {
                    queryDate = queryDate.substring(0,10);
                }
                String todayStr = Toolkit.HOST_DATE_FORMAT.format(new Date());
                if (queryDate.equals(todayStr)) {
                    bodyElement.addElement("time").setText(Toolkit.HOST_DATE_TIME_FORMAT.format(new Date()));
                } else {
                    bodyElement.addElement("time").setText(queryDate + " 23:59:59");
                }
                message.setContent(responseDocument.asXML());
            } else if (message.getContent() != null) {
                    Document responseDocument = DocumentHelper.parseText(message.getContent());
                    Element bodyElement = (Element) responseDocument.selectSingleNode("/positive-response/body");
                    bodyElement.addElement("currency").setText(currency);
                    bodyElement.addElement("account-no").setText(accountNo);
                    message.setContent(responseDocument.asXML()); 
                } 
        }
        return message;
    }

    public void setSnfncPositivePersistence(SnfncPositivePersistence snfncPositivePersistence) {
        this.snfncPositivePersistence = snfncPositivePersistence;
    } 
}