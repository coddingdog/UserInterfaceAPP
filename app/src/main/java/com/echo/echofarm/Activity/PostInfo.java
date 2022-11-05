package com.echo.echofarm.Activity;

public class PostInfo {

    private String title;
    private String tags;
    private int imageUri;

    public PostInfo(String title, String tags, int imageUri) {
        this.title = title;
        this.tags = tags;
        this.imageUri = imageUri;
    }
    public String getTitle() {
        return title;
    }
    public String getTags() {
        return tags;
    }
    public int getImageUri() {
        return imageUri;
    }
}
