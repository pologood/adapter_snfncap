package com.suning.csp.adapter.snfnc.positive.interceptor.validate;

import com.gs.csp.framework.exception.ValidateException;
import com.gs.csp.framework.interceptor.Interceptable;
import com.gs.csp.framework.message.ClientPositiveMessage;
import com.gs.csp.framework.message.Request;
import com.gs.csp.framework.persistence.AdapterPositivePersistent;

/**
 * 流水号校验拦截器
 *
 * @author zhangpengcheng
 */
public class SnfncPositiveSerialNumberValidateInterceptor<M extends ClientPositiveMessage & Request> implements Interceptable<M> {
    /**
     * 持久化组件
     */
    private AdapterPositivePersistent<M, ?> persistence;

    /**
     * 校验异常描述
     */
    private String exceptionText = "serial number in conflict.";

    public M intercept(M message) throws ValidateException {
        if (!this.persistence.validate(message)) throw new ValidateException(this.exceptionText);
        return message;
    }

    public void setPersistence(AdapterPositivePersistent<M, ?> persistence) {
        this.persistence = persistence;
    }

    public void setExceptionText(String exceptionText) {
        this.exceptionText = exceptionText;
    }
}
