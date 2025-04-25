package org.example.healbackend.bean;

public enum MessageStatus {
    READ(1),   // 已读
    UNREAD(0), // 未读
    HIDDEN(-1); // 隐藏

    private final int status;

    MessageStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
