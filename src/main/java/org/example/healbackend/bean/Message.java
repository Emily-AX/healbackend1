package org.example.healbackend.bean;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    private int id;
    private int userId;
    private int receiveId;
    private int status;
    private String createTime;
    private String content;
    public Message() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(int receiveId) {
        this.receiveId = receiveId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonCreator
    public Message(@JsonProperty("userId") int userId,
                   @JsonProperty("receiveId") int receiveId,
                   @JsonProperty("content") String content,
                   @JsonProperty("createTime") String createTime) {
        this.userId = userId;
        this.receiveId = receiveId;
        this.content = content;
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"id\": " + id + ",\n" +
                "  \"userId\": " + userId + ",\n" +
                "  \"receiveId\": " + receiveId + ",\n" +
                "  \"status\": " + status + ",\n" +
                "  \"createTime\": \"" + createTime + "\",\n" +
                "  \"content\": \"" + content + "\"\n" +
                "}";
    }
}
