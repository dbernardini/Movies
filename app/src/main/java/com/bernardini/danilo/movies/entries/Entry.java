package com.bernardini.danilo.movies.entries;

import android.graphics.drawable.Drawable;

public class Entry {

    private String name;
    private String details;
    private String imagePath;
    private String id;
    private Drawable image;
    private String type;

    public Entry(String name, String details, String imagePath, String id, String type){
        this.name = name;
        this.details = details;
        this.imagePath = imagePath;
        this.id = id;
        this.type = type;
        this.image = null;
    }

    public Entry(String name, String path, String id, String type){
        this.name = name;
        this.details = null;
        this.imagePath = path;
        this.id = id;
        this.type = type;
        this.image = null;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getId() {
        return id;
    }

    public String getDetails() {
        return details;
    }

    public Drawable getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
