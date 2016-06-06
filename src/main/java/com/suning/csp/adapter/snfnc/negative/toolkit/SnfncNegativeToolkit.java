package com.suning.csp.adapter.snfnc.negative.toolkit;

import com.gs.csp.framework.message.HostRequestMessage;
import com.gs.csp.framework.message.HostResponseMessage;
import com.gs.csp.framework.toolkit.Toolkit;
import com.gs.utility.converter.StringConverter;
import com.gs.utility.format.DateFormat;
import com.gs.utility.io.Transfer;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeRequestMessage;
import com.suning.csp.adapter.snfnc.negative.message.SnfncNegativeResponseMessage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 反向工具类
 * 
 * @author zhangpengcheng
 */
public final class SnfncNegativeToolkit {

    /**
     * 交易成功
     */
    public static final String NEGATIVE_TRANSACT_STATUS_SUCCESS = Toolkit.HOST_TRANSACT_STATUS_SUCCESS;

    /**
     * 交易失败
     */
    public static final String NEGATIVE_TRANSACT_STATUS_FAILURE = Toolkit.HOST_TRANSACT_STATUS_FAILURE;

    /**
     * 交易不明
     */
    public static final String NEGATIVE_TRANSACT_STATUS_UNKNOWN = Toolkit.HOST_TRANSACT_STATUS_UNKNOWN;

    /**
     * 交易等待
     */
    public static final String NEGATIVE_TRANSACT_STATUS_WAITING = Toolkit.HOST_TRANSACT_STATUS_WAITING;

    /**
     * H-P状态映射
     */
    public static final Map<String, String> N_H_TRANSACT_STATUS_MAP = new HashMap<String, String>();

    /**
     * 内部与反向状态映射
     */
    public static final Map<String, String> H_N_TRANSACT_STATUS_MAP = new HashMap<String, String>();

    /**
     * 付款和付款查询映射关系
     */
    public static final Map<String, String> P_R_BUSINESS_MAP = new HashMap<String, String>();

    static {
        N_H_TRANSACT_STATUS_MAP.put(NEGATIVE_TRANSACT_STATUS_SUCCESS, Toolkit.HOST_TRANSACT_STATUS_SUCCESS);
        N_H_TRANSACT_STATUS_MAP.put(NEGATIVE_TRANSACT_STATUS_FAILURE, Toolkit.HOST_TRANSACT_STATUS_FAILURE);
        N_H_TRANSACT_STATUS_MAP.put(NEGATIVE_TRANSACT_STATUS_UNKNOWN, Toolkit.HOST_TRANSACT_STATUS_UNKNOWN);

        H_N_TRANSACT_STATUS_MAP.put(Toolkit.HOST_TRANSACT_STATUS_SUCCESS, NEGATIVE_TRANSACT_STATUS_SUCCESS);
        H_N_TRANSACT_STATUS_MAP.put(Toolkit.HOST_TRANSACT_STATUS_WAITING, NEGATIVE_TRANSACT_STATUS_WAITING);
        H_N_TRANSACT_STATUS_MAP.put(Toolkit.HOST_TRANSACT_STATUS_FAILURE, NEGATIVE_TRANSACT_STATUS_FAILURE);

        P_R_BUSINESS_MAP.put("0003", "0004");
        P_R_BUSINESS_MAP.put("0005", "0006");
        P_R_BUSINESS_MAP.put("0007", "0008");
        P_R_BUSINESS_MAP.put("0050", "0008");
        P_R_BUSINESS_MAP.put("0057", "0058");
        P_R_BUSINESS_MAP.put("0060", "0061");
    }

    /**
     * 日期格式化对象
     */
    public static final DateFormat NEGATIVE_DATE_FORMAT = Toolkit.HOST_DATE_FORMAT;

    /**
     * 时间格式化对象
     */
    public static final DateFormat NEGATIVE_TIME_FORMAT = Toolkit.HOST_TIME_FORMAT;

    /**
     * 日期时间格式化对象
     */
    public static final DateFormat NEGATIVE_DATE_TIME_FORMAT = Toolkit.HOST_DATE_TIME_FORMAT;

    /**
     * 字符编码
     */
    public static final String NEGATIVE_ENCODING = Toolkit.HOST_ENCODING;

    /**
     * 请求附件扩展名
     */
    public static final String NEGATIVE_FILE_EXTENSION_REQUEST = Toolkit.HOST_FILE_EXTENSION_REQUEST;

    /**
     * 响应附件扩展名
     */
    public static final String NEGATIVE_FILE_EXTENSION_RESPONSE = Toolkit.HOST_FILE_EXTENSION_RESPONSE;

    /**
     * 创建反向请求对象
     * 
     * @param hostRequestMessage 内部请求对象
     * @param requestHostDirectory 内部请求文件目录
     * @param requestClientDirectory 反向请求文件目录
     * @return SnfncNegativeRequestMessage 反向请求对象
     * @throws Exception 异常
     */
    public static SnfncNegativeRequestMessage createSnfncNegativeRequestMessage(HostRequestMessage hostRequestMessage,
            File requestHostDirectory, File requestClientDirectory) throws Exception {
        SnfncNegativeRequestMessage negativeRequestMessage = new SnfncNegativeRequestMessage();
        negativeRequestMessage.setSerialNumber(hostRequestMessage.getMessageId());
        negativeRequestMessage.setClientCode(hostRequestMessage.getNegativeClientCode());
        negativeRequestMessage.setBusinessCode(hostRequestMessage.getBusinessCode());
        negativeRequestMessage.setPriority(hostRequestMessage.getPriority());
        negativeRequestMessage.setModel(hostRequestMessage.getModel());
        negativeRequestMessage.setHostMessageId(hostRequestMessage.getMessageId());
        negativeRequestMessage.setVersion(hostRequestMessage.getVersion());
        String hostFilePath = hostRequestMessage.getFilePath();
        if (null != hostFilePath) {
            String clientFilePath = negativeRequestMessage.getSerialNumber() + NEGATIVE_FILE_EXTENSION_REQUEST;
            // 拷贝文件
//            Transfer.copy(new File(requestHostDirectory, hostFilePath),new File(requestClientDirectory, clientFilePath));
            negativeRequestMessage.setFilePath(clientFilePath);
        }
        // 创建反向对象content内容
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("negative-request");
        Element headElement = root.addElement("head");
        headElement.addElement("client-code").setText(negativeRequestMessage.getClientCode());
        headElement.addElement("serial-number").setText(negativeRequestMessage.getSerialNumber());
        headElement.addElement("business-code").setText(negativeRequestMessage.getBusinessCode());
        headElement.addElement("priority").setText(StringConverter.integer2blank(negativeRequestMessage.getPriority()));
        headElement.addElement("file-path").setText(StringConverter.null2blank(negativeRequestMessage.getFilePath()));
        headElement.addElement("version").setText(negativeRequestMessage.getVersion());
        String hostContent = hostRequestMessage.getContent();
        if (hostContent != null) {
            root.add(DocumentHelper.parseText(hostContent).selectSingleNode("/body"));
        }
        negativeRequestMessage.setContent(document.asXML());
        return negativeRequestMessage;
    }

    /**
     * 创建反向响应对象
     * 
     * @param responseString 反向响应消息
     * @return SnfncNegativeResponseMessage 反向响应对象
     * @throws DocumentException 异常
     */
    public static SnfncNegativeResponseMessage createNegativeResponseMessage(String responseString) throws Exception {
        SnfncNegativeResponseMessage negativeResponseMessage = new SnfncNegativeResponseMessage();
        Node headNode = DocumentHelper.parseText(responseString).selectSingleNode("/negative-response/head");
        negativeResponseMessage.setSerialNumber(headNode.selectSingleNode("serial-number").getText());
        negativeResponseMessage.setFilePath(StringConverter
                .blank2null(headNode.selectSingleNode("file-path").getText()));
        negativeResponseMessage.setResultCode(headNode.selectSingleNode("result-code").getText());
        negativeResponseMessage.setResultText(StringConverter.blank2null(headNode.selectSingleNode("result-text")
                .getText()));
        negativeResponseMessage.setContent(responseString);
        negativeResponseMessage.setVersion(headNode.selectSingleNode("version").getText());
        negativeResponseMessage.setBusinessCode(headNode.selectSingleNode("business-code").getText());
        return negativeResponseMessage;
    }

    /**
     * 创建内部响应对象
     * 
     * @param negativeResponseMessage 反向响应对象
     * @param responseClientDirectory 反向响应文件目录
     * @param responseHostDirectory 内部响应文件目录
     * @return HostResponseMessage 内部响应对象
     * @throws Exception 异常
     */
    public static HostResponseMessage createHostResponseMessage(SnfncNegativeResponseMessage negativeResponseMessage,
            File responseClientDirectory, File responseHostDirectory) throws Exception {
        HostResponseMessage hostResponseMessage = new HostResponseMessage();
        hostResponseMessage.setMessageId(negativeResponseMessage.getHostMessageId());
        String clientFilePath = negativeResponseMessage.getFilePath();
        if (null != clientFilePath) {
            String hostFilePath = hostResponseMessage.getMessageId() + Toolkit.HOST_FILE_EXTENSION_RESPONSE;
            // 拷贝文件
            /*Transfer.copy(new File(responseClientDirectory, clientFilePath), new File(responseHostDirectory,
                    hostFilePath));*/
            hostResponseMessage.setFilePath(hostFilePath);
        }
        // 外部交易结果代码转为对应的内部交易结果代码
        hostResponseMessage.setResultCode(N_H_TRANSACT_STATUS_MAP.get(negativeResponseMessage.getResultCode()));
        hostResponseMessage.setResultText(negativeResponseMessage.getResultText());
        Node body = DocumentHelper.parseText(negativeResponseMessage.getContent()).selectSingleNode("/negative-response/body");
        if (body != null) {
            hostResponseMessage.setContent(body.asXML());
        }
        return hostResponseMessage;
    }

    /**
     * 创建异常内部响应对象
     * 
     * @param hostRequestMessage 内部请求对象
     * @param exceptionResultText 异常描述
     * @return HostResponseMessage 内部响应对象
     */
    public static HostResponseMessage createExceptionHostResponseMessage(HostRequestMessage hostRequestMessage,
            String exceptionResultText) {
        HostResponseMessage hostResponseMessage = new HostResponseMessage();
        hostResponseMessage.setMessageId(hostRequestMessage.getMessageId());
        hostResponseMessage.setBusinessCode(hostRequestMessage.getBusinessCode());
        hostResponseMessage.setNegativeClientCode(hostRequestMessage.getNegativeClientCode());
        hostResponseMessage.setPositiveClientCode(hostRequestMessage.getPositiveClientCode());
        hostResponseMessage.setPriority(hostRequestMessage.getPriority());
        hostResponseMessage.setVersion(hostRequestMessage.getVersion());
        hostResponseMessage.setResultCode(Toolkit.HOST_TRANSACT_STATUS_UNKNOWN);
        hostResponseMessage.setResultText(exceptionResultText);
        return hostResponseMessage;
    }
}