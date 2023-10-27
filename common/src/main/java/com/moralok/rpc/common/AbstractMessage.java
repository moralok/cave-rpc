package com.moralok.rpc.common;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract class of message.
 */
public abstract class AbstractMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 147447865497252395L;

    /**
     * requestId
     */
    private Long requestId;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "AbstractMessage{" +
                "requestId=" + requestId +
                '}';
    }

    public abstract byte getMessageType();
}
