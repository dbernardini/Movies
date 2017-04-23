package com.bernardini.danilo.movies.entries;

import android.graphics.drawable.Drawable;

public class MovieEntry extends Entry {

    private String year;
    private Drawable poster;

    public MovieEntry(String title, String year, String posterPath, String id){
        super(title, year, posterPath, id, "movie");
        this.year = year;
        this.poster = null;
    }

    public String getYear(){
        return year;
    }

    public Drawable getPoster() {
        return poster;
    }
}
