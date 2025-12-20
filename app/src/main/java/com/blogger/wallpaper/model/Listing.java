package com.blogger.wallpaper.model;

import java.util.List;

public class Listing {
    public String id;
    public String type;
    public String title;
    public String published;
    public String updated;
    public String content;
    public String link;
    public List<String> category;
    public String nextPageToken;
//    public Author author;


    public Listing() {
    }

    public Listing(String id, String type, String title, String published, String updated, String content, String link, List<String> category,String nextPageToken) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.published = published;
        this.updated = updated;
        this.content = content;
        this.link = link;
        this.category = category;
        this.nextPageToken = nextPageToken;
    }
}