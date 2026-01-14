package com.easytpa.data;

import java.util.UUID;

public class TpaRequest {

    public enum Type {
        TPA,
        TPAHERE
    }

    private final UUID senderId;
    private final String senderName;
    private final UUID targetId;
    private final String targetName;
    private final Type type;
    private final long createdAt;

    public TpaRequest(UUID senderId, String senderName, UUID targetId, String targetName, Type type) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.targetId = targetId;
        this.targetName = targetName;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public Type getType() {
        return type;
    }

    public boolean isExpired(int timeoutSeconds) {
        return System.currentTimeMillis() - createdAt > timeoutSeconds * 1000L;
    }
}
