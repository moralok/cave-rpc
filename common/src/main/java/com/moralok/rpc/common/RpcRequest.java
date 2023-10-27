package com.moralok.rpc.common;

import java.io.Serial;

/**
 * RpcRequest
 *
 * @author moralok
 */
public class RpcRequest extends AbstractMessage {

    @Serial
    private static final long serialVersionUID = 5358852904078929307L;

    /**
     * serviceName
     */
    private String serviceName;

    /**
     * methodName
     */
    private String methodName;

    /**
     * parameterTypes
     */
    private Class<?>[] parameterTypes;

    /**
     * parameterValues
     */
    private Object[] parameterValues;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Object[] parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public byte getMessageType() {
        return 0;
    }
}
