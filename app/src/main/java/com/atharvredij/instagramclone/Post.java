package com.atharvredij.instagramclone;

// POJO to store posts on Firebase Realtime Database
public class Post {

    public String title;
    public String description;
    public String imageUrl;

    public Post() {
    }

    public Post(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
