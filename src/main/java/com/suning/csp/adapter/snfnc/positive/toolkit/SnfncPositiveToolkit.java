package com.suning.csp.adapter.snfnc.positive.toolkit;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;

import com.gs.csp.framework.message.ClientPositiveMessage;
import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.gs.csp.framework.message.Request;
import com.gs.csp.framework.message.Response;
import com.gs.csp.framework.toolkit.Toolkit;
import com.gs.utility.converter.StringConverter;
import com.gs.utility.format.DateFormat;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveRequestMessage;
import com.suning.csp.adapter.snfnc.positive.message.SnfncPositiveResponseMessage;

/**
 * @author Guozheng
 * @version 0.0.1, 14-2-19, 上午11:07
 */
public final class SnfncPositiveToolkit {

    /**
     * 交易成功
     */
    public static final String POSITIVE_TRANSACT_STATUS_SUCCESS = Toolkit.HOST_TRANSACT_STATUS_SUCCESS;

    /**
     * 交易失败
     */
    public static final String POSITIVE_TRANSACT_STATUS_FAILURE = Toolkit.HOST_TRANSACT_STATUS_FAILURE;

    /**
     * 交易不明
     */
    public static final String POSITIVE_TRANSACT_STATUS_UNKNOWN = Toolkit.HOST_TRANSACT_STATUS_UNKNOWN;

    /**
     * 交易等待
     */
    public static final String POSITIVE_TRANSACT_STATUS_WAITING = Toolkit.HOST_TRANSACT_STATUS_WAITING;

    /**
     * H-P状态映射
     */
    public static final Map<String, String> H_P_TRANSACT_STATUS_MAP = new HashMap<String, String>();

    static {
        H_P_TRANSACT_STATUS_MAP.put(Toolkit.HOST_TRANSACT_STATUS_SUCCESS, POSITIVE_TRANSACT_STATUS_SUCCESS);
        H_P_TRANSACT_STATUS_MAP.put(Toolkit.HOST_TRANSACT_STATUS_FAILURE, POSITIVE_TRANSACT_STATUS_FAILURE);
        H_P_TRANSACT_STATUS_MAP.put(Toolkit.HOST_TRANSACT_STATUS_UNKNOWN, POSITIVE_TRANSACT_STATUS_UNKNOWN);
    }

    /**
     * 日期格式化对象
     */
    public static final DateFormat POSITIVE_DATE_FORMAT = Toolkit.HOST_DATE_FORMAT;

    /**
     * 时间格式化对象
     */
    public static final DateFormat POSITIVE_TIME_FORMAT = Toolkit.HOST_TIME_FORMAT;

    /**
     * 日期时间格式化对象
     */
    public static final DateFormat POSITIVE_DATE_TIME_FORMAT = Toolkit.HOST_DATE_TIME_FORMAT;

    /**
     * 字符编码
     */
    public static final String POSITIVE_ENCODING = Toolkit.HOST_ENCODING;

    /**
     * 请求附件扩展名
     */
    public static final String POSITIVE_FILE_EXTENSION_REQUEST = Toolkit.HOST_FILE_EXTENSION_REQUEST;

    /**
     * 响应附件扩展名
     */
    public static final String POSITIVE_FILE_EXTENSION_RESPONSE = Toolkit.HOST_FILE_EXTENSION_RESPONSE;
    /**
     * 非付款类业务代码
     */
    private static Set<String> queryBusinessCodeSet = new HashSet<String>();

    /**
     * 付款类业务代码
     */
    private static Set<String> payBusinessCodeSet = new HashSet<String>();
    
    /**
     * 重发时付款单号重复的付款类业务代码
     */
    private static Set<String> repeatPayBusinessCodeSet = new HashSet<String>();

    /**
     * 日期格式
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 付款类交易标识
     */
    public static final String PAYMENT_BUSINESS_FLAG = "payment";    
    
    /**
     * 非付款类交易标识
     */
    public static final String NOT_PAYMENT_BUSINESS_FLAG = "other";
    static {
        // 下面业务代码bank-code标签增加在body下
        queryBusinessCodeSet.add("0001");
        queryBusinessCodeSet.add("0002");
        queryBusinessCodeSet.add("0011");
        queryBusinessCodeSet.add("0013");
        queryBusinessCodeSet.add("0014");
        queryBusinessCodeSet.add("0016");
        queryBusinessCodeSet.add("0017");
        queryBusinessCodeSet.add("0018");
        queryBusinessCodeSet.add("0019");
        queryBusinessCodeSet.add("0020");
        queryBusinessCodeSet.add("0021");
        queryBusinessCodeSet.add("0022");
        queryBusinessCodeSet.add("0023");
        queryBusinessCodeSet.add("0040");
        queryBusinessCodeSet.add("0051");
        queryBusinessCodeSet.add("0052");
        queryBusinessCodeSet.add("0053");
        queryBusinessCodeSet.add("0054");
        queryBusinessCodeSet.add("0055");
        queryBusinessCodeSet.add("0056");
        queryBusinessCodeSet.add("0058");
        queryBusinessCodeSet.add("0059");
        queryBusinessCodeSet.add("0061");
        queryBusinessCodeSet.add("0062");
        queryBusinessCodeSet.add("0063");
        queryBusinessCodeSet.add("0064");
        queryBusinessCodeSet.add("0065");
        queryBusinessCodeSet.add("0066");
        queryBusinessCodeSet.add("0067");
        queryBusinessCodeSet.add("0069");
		queryBusinessCodeSet.add("0074");
        queryBusinessCodeSet.add("0082");
        // 下面业务代码bank-code标签增加在body/account下(付款单号不重复)
        payBusinessCodeSet.add("0057");
        payBusinessCodeSet.add("0060");
        payBusinessCodeSet.add("0073");
        payBusinessCodeSet.add("0075");
        payBusinessCodeSet.add("0077");
        payBusinessCodeSet.add("0079");
        payBusinessCodeSet.add("0080");
        // 重发时付款单号会重复的交易
        repeatPayBusinessCodeSet.add("0003");
        repeatPayBusinessCodeSet.add("0005");
        repeatPayBusinessCodeSet.add("0007");
        repeatPayBusinessCodeSet.add("0050");
        repeatPayBusinessCodeSet.add("0068");
    }
    /**
     * 创建正向请求对象
     * 
     * @param requestMessage 请求消息
     * @param content 请求内容
     * @param model 通讯模式
     * @return SnfncPositiveRequestMessage 请求消息
     * @throws org.dom4j.DocumentException 异常
     */
    public static <QM extends ClientPositiveMessage & Request> QM createPositiveRequestMessage(QM requestMessage,
            String content, String model) throws DocumentException {
        Node head = DocumentHelper.parseText(content).selectSingleNode("/positive-request/head");
        requestMessage.setSerialNumber(StringConverter.blank2null(head.selectSingleNode("serial-number").getText()));
        requestMessage.setClientCode(StringConverter.blank2null(head.selectSingleNode("client-code").getText()));
        requestMessage.setBusinessCode(StringConverter.blank2null(head.selectSingleNode("business-code").getText()));
        requestMessage.setPriority(StringConverter.string2integer(StringConverter.blank2null(head.selectSingleNode("priority").getText())));
        requestMessage.setFilePath(StringConverter.blank2null(head.selectSingleNode("file-path").getText()));
        requestMessage.setVersion(StringConverter.blank2null(head.selectSingleNode("version").getText()));
        requestMessage.setModel(model);
        requestMessage.setContent(content);
        return requestMessage;
    }

    /**
     * 
     * 将正向请求转换成内部请求数据
     *
     * @param <QM>
     * @param requestMessage 正向请求消息
     * @param positiveDirectory 正向文件目录
     * @param hostDirectory 内部文件目录
     * @return 内部请求消息
     * @throws DocumentException
     * @throws IOException
     */
    public static <QM extends ClientPositiveMessage & Request> HostRequestMessage createHostRequestMessage(
            QM requestMessage, File positiveDirectory, File hostDirectory) throws DocumentException, IOException {
        HostRequestMessage hostRequest = new HostRequestMessage();
        hostRequest.setMessageId(requestMessage.getMessageId());
        hostRequest.setPositiveClientCode(requestMessage.getClientCode());
        hostRequest.setBusinessCode(requestMessage.getBusinessCode());
        hostRequest.setPriority(requestMessage.getPriority());
        hostRequest.setFilePath(requestMessage.getFilePath());
        hostRequest.setVersion(requestMessage.getVersion());
        hostRequest.setModel(requestMessage.getModel());
        Node body = DocumentHelper.parseText(requestMessage.getContent()).selectSingleNode("/positive-request/body");
        if (body != null) {
            hostRequest.setContent(body.asXML());
        }
        return hostRequest;
    }

    /**
     * 创建正向返回对象
     *
     * @param hostResponseMessage     内部响应消息
     * @param hostDirectory
     * @param positiveResponseMessage 正向响应消息
     * @param positiveDirectory
     * @param positiveFilePath
     * @return SnfncPositiveResponseMessage 正向响应消息
     * @throws java.io.IOException
     * @throws org.dom4j.DocumentException
     */
    public static <RM extends ClientPositiveMessage & Response> RM createPositiveResponseMessage(
            HostResponseMessage hostResponseMessage, File hostDirectory, RM positiveResponseMessage,
            File positiveDirectory, String positiveFilePath) throws IOException, DocumentException {
        positiveResponseMessage.setMessageId(hostResponseMessage.getMessageId());
        String filePath = hostResponseMessage.getFilePath();
        if (filePath != null && hostDirectory != null) {
            //            Transfer.copy(new File(hostDirectory, filePath), new File(positiveDirectory, positiveFilePath));
            positiveResponseMessage.setFilePath(positiveFilePath);
        }
        positiveResponseMessage.setResultCode(H_P_TRANSACT_STATUS_MAP.get(hostResponseMessage.getResultCode()));
        positiveResponseMessage.setResultText(hostResponseMessage.getResultText());
        // content
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("positive-response");
        Element head = root.addElement("head");
        head.addElement("file-path").setText(StringConverter.null2blank(positiveResponseMessage.getFilePath()));
        head.addElement("result-code").setText(StringConverter.null2blank(positiveResponseMessage.getResultCode()));
        if (hostResponseMessage.getContent() != null)
            root.add((Node) DocumentHelper.parseText(hostResponseMessage.getContent()).selectSingleNode("/body").clone());
        positiveResponseMessage.setContent(document.asXML());
        return positiveResponseMessage;
    }

    /**
     * 拼装异常消息返回对象 
     *
     * @param requestMessage            正向请求对象
     * @param defaultUnknownResultText  异常信息描述
     * @return SnfncPositiveResponseMessage 正向响应对象
     */
    public static SnfncPositiveResponseMessage createExceptionResponseMessage(
            SnfncPositiveRequestMessage requestMessage, String resultCode, String defaultUnknownResultText) {
        Document document = DocumentHelper.createDocument();
        Element responseElement = document.addElement("positive-response");
        Element headElement = responseElement.addElement("head");
        headElement.addElement("file-path");
        headElement.addElement("result-code").setText(resultCode);
        headElement.addElement("result-text").setText(defaultUnknownResultText);
        // 创建正向响应对象
        SnfncPositiveResponseMessage responseMessage = new SnfncPositiveResponseMessage();
        responseMessage.setMessageId(requestMessage.getMessageId());
        responseMessage.setBusinessCode(requestMessage.getBusinessCode());
        responseMessage.setClientCode(requestMessage.getClientCode());
        responseMessage.setPriority(requestMessage.getPriority());
        responseMessage.setSerialNumber(requestMessage.getSerialNumber());
        responseMessage.setVersion(requestMessage.getVersion());
        responseMessage.setResultCode(resultCode);
        responseMessage.setResultText(defaultUnknownResultText);
        responseMessage.setContent(document.asXML());
        return responseMessage;
    }


    /**
     *
     * 请求老报文转换为新报文
     *
     * @param requestMessage 老报文
     * @return String 新报文
     * @throws DocumentException 解析异常
     */
    public static String transferRequestMessage(String requestMessage) throws DocumentException {
        Document document = DocumentHelper.parseText(requestMessage);
        // 修改head
        Element headElement = (Element) document.selectSingleNode("/positive-request/head");
        Node businessCodeNode = document.selectSingleNode("/positive-request/head/business-code");
        String businessCode = businessCodeNode.getText();
        // 流水号
        Node serialNumberNode = document.selectSingleNode("/positive-request/head/serial-number");
        Node positiveCodeNode = document.selectSingleNode("/positive-request/head/positive-code");
        Node negativeCodeNode = document.selectSingleNode("/positive-request/head/negative-code");
        String negativeCode = negativeCodeNode.getText();
        // head节点删除negative-code节点
        headElement.remove(negativeCodeNode);
        // head节点增加client-code节点
        headElement.addElement("client-code").setText(positiveCodeNode.getText().toLowerCase());
        // head节点删除节点
        headElement.remove(positiveCodeNode);
        // 修改body
        Node bodyNode = document.selectSingleNode("/positive-request/body");
        if (bodyNode != null) {
            // 判断body节点增加bank-code节点
            if (queryBusinessCodeSet.contains(businessCode)) {
                // 增加的bank-code标签写在body下
                ((Element) bodyNode).addElement("bank-code").setText(negativeCode);
            } else if (payBusinessCodeSet.contains(businessCode) || repeatPayBusinessCodeSet.contains(businessCode)) {
                // 付款类增加的bank-code标签写在account下
                Node accountNode = bodyNode.selectSingleNode("account");
                if("0077".equals(businessCode) && null==accountNode){//0077没有account节点
                    accountNode = new DefaultElement("account");
                    ((Element) bodyNode).add(accountNode);
                }
                ((Element) accountNode).addElement("bank-code").setText(negativeCode);
                // 付款类的增加付款单号,新交易资金传transact-no,老交易取serialNumber
                if (bodyNode.selectSingleNode("transact-no") == null) {//资金付款单号
                    ((Element) bodyNode).addElement("transact-no").setText(serialNumberNode.getText());
                }
                // 修改新流水号为时间戳+老流水（防止重新付款时校验失败）
                if(repeatPayBusinessCodeSet.contains(businessCode)){
                    serialNumberNode.setText(DATE_FORMAT.format(new Date()) + serialNumberNode.getText());
                }
            }
        } 
        return document.asXML();
    }

    /**
     *
     * 响应新报文转换为老报文
     *
     * @param responseString 接收到新报文
     * @return String 转换后的老报文
     * @throws DocumentException 异常
     */
    public static String transferResponseMessage(String responseString,String negativeCode) throws DocumentException {
        Document document = DocumentHelper.parseText(responseString);
        // 修改business-code
        Node businessCodeNode = document.selectSingleNode("/positive-response/head/business-code");
        String businessCode = businessCodeNode.getText();
        Node clientCodeNode = document.selectSingleNode("/positive-response/head/client-code");
        String positiveCode = clientCodeNode.getText();
        Element headElement = (Element) document.selectSingleNode("/positive-response/head");
        // 付款交易的新老流水号转换
        if (repeatPayBusinessCodeSet.contains(businessCode)) {
            Node serialNumberNode = document.selectSingleNode("/positive-response/head/serial-number");
            // 新流水号转换为老流水号去掉时间戳
            serialNumberNode.setText(serialNumberNode.getText().substring(14));
        }
        // head节点增加positive-code字段
        headElement.addElement("positive-code").setText(positiveCode);
        // head节点增加negative-code字段
        headElement.addElement("negative-code").setText(negativeCode);
        // head节点删除client-code节点
        headElement.remove(clientCodeNode);
        return document.asXML();
    }
    
    /**
     * 判断交易是否是付款类交易
     * @param businessCode
     * @return
     */
    public static String getBusinessFlag(String businessCode){
        if(repeatPayBusinessCodeSet.contains(businessCode) || payBusinessCodeSet.contains(businessCode)){
            return PAYMENT_BUSINESS_FLAG;
        }else{
            return NOT_PAYMENT_BUSINESS_FLAG;
        }
    }

}

