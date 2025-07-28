package com.petchat.android.models;

public class Moment {
    private String userName;
    private String time;
    private String content;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;

    public Moment(String userName, String time, String content, int likeCount, int commentCount, boolean isLiked) {
        this.userName = userName;
        this.time = time;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}