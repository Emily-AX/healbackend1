package org.example.healbackend.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class Comment implements Serializable {
    private int comment_id;
    private int user_id;
    private String content;
    private Timestamp created_at;

    public Comment(int comment_id, int user_id, String content, Timestamp created_at) {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.content = content;
        this.created_at = created_at;
    }

    // Getters and Setters
    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
