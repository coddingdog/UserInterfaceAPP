package com.echo.echofarm.Activity;

public class PostInfo {

    private final String title;
    private final String tags;
    private final int imageUri;

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
