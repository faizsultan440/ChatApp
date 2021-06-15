package com.example.rbchat.Model;

import java.io.Serializable;

public class Calls implements Serializable {


    private String callId;
    private String senderId;
    private String receiverId;
    private long timestamp;
    private String type;
    private long duration;

    public Calls(String callId, String senderId, String receiverId, long timestamp, String type, long duration ){
        this.callId = callId;

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.type = type;
        this.duration = duration;
    }


    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
