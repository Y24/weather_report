/*
 * Copyright (c) 2019.
 *  Author: Y24
 *  All rights reserved.
 */

package cn.org.y24.ui.framework;

public class Deliverer {
    public static int broadcastFlag = 0;
    private int senderHashCode;
    private int receiverHahCode;
    private Object message;

    public Deliverer(int senderHashCode,
                     int receiverHahCode,
                     Object message) {
        this.senderHashCode = senderHashCode;
        this.receiverHahCode = receiverHahCode;
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public int getReceiverHahCode() {
        return receiverHahCode;
    }

    public void setReceiverHahCode(int receiverHahCode) {
        this.receiverHahCode = receiverHahCode;
    }

    public int getSenderHashCode() {
        return senderHashCode;
    }

    public void setSenderHashCode(int senderHashCode) {
        this.senderHashCode = senderHashCode;
    }
}
