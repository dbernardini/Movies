package com.bernardini.danilo.movies.entries;

/**
 * Created by danilo on 24/04/17.
 */

public class Movie {

    private String id;
    private String title;
    private String director;
    private int runtime;
    private String path;
    private String cast;
    private String genre;
    private String showtimes;

    public Movie(){
        // Default constructor required for calls to DataSnapshot.getValue(Movie.class)
    }

    public Movie(String id, String title, String director, int runtime, String path, String cast) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.runtime = runtime;
        this.path = path;
        this.cast = cast;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(String showtimes) {
        this.showtimes = showtimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return id.equals(movie.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", runtime=" + runtime +
                ", path='" + path + '\'' +
                ", cast='" + cast + '\'' +
                ", genre='" + genre + '\'' +
                ", showtimes='" + showtimes + '\'' +
                '}';
    }
}
