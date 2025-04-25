package org.example.healbackend.bean;


public class Questions {
    private int questionId;
    private int userId;
    private String title;
    private String content;
    private int isAnonymous;
    private int viewCount;
    private int likeCount;
    private String createdAt;

    public Questions() {}

    public Questions(int userId, String title, String content, int isAnonymous, int viewCount, int likeCount, String createdAt) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(int anonymous) {
        isAnonymous = anonymous;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}