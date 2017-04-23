package com.bernardini.danilo.movies.entries;

import android.graphics.drawable.Drawable;

public class PersonEntry extends Entry{

    private String details;

    public PersonEntry(String name, String details, String profilePath, String id){
        super(name, details, profilePath, id, "person");
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

}
