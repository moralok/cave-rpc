package com.moralok.rpc.common;

import java.io.Serial;

/**
 * RpcResponse
 *
 * @author moralok
 */
public class RpcResponse extends AbstractMessage {

    @Serial
    private static final long serialVersionUID = 2464840746894212201L;

    /**
     * result
     */
    private Object result;

    /**
     * error
     */
    private Exception error;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "result=" + result +
                ", error=" + error +
                "} " + super.toString();
    }

    @Override
    public byte getMessageType() {
        return 1;
    }
}
