package com.e.bisatau.model;

public class NewsModel {

    String id;
    String title;
    String description;
    String image;

    public NewsModel(String id, String title, String description, String image ) {
        this.id=id;
        this.title=title;
        this.description=description;
        this.image=image;
    }

    public  String getId() {
        return  id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

}