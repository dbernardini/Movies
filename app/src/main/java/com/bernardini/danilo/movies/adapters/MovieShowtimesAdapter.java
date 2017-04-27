package com.bernardini.danilo.movies.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.entries.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieShowtimesAdapter extends ArrayAdapter {

    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private List<Movie> moviesList;
    private Context context;
    private Activity activity;

    public MovieShowtimesAdapter(Activity activity, Context context, List<Movie> movieList) {
        super(context, R.layout.showtimes_row_layout, movieList);
        this.moviesList = movieList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.showtimes_row_layout, parent, false);

        ImageView imageView = (ImageView) row.findViewById(R.id.poster);
        TextView titleView = (TextView) row.findViewById(R.id.title);
        TextView directorView = (TextView) row.findViewById(R.id.director);
        TextView runtimeView = (TextView) row.findViewById(R.id.runtime);
        TextView genreView = (TextView) row.findViewById(R.id.genre);
        TextView castView = (TextView) row.findViewById(R.id.cast);
        TextView showtimesView = (TextView) row.findViewById(R.id.showtimes);

        Movie movie = moviesList.get(position);

        titleView.setText(movie.getTitle());
        directorView.setText("Director: " + movie.getDirector());
        int runtime = movie.getRuntime();
        int hours = runtime/60;
        int minutes = runtime-(hours*60);
        runtimeView.setText("Runtime: " + hours + "h " + minutes + "m");
        genreView.setText("Genre: " + movie.getGenre());
        castView.setText("Cast: " + movie.getCast());
        showtimesView.setText("Showtimes: " + movie.getShowtimes());

        String imagePath = movie.getPath();

        String posterUrl = URL_IMAGE_API + "w300" + imagePath + "?api_key=" + API_KEY;

        Picasso.with(activity)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_image)
                .into(imageView);

        return row;
    }
}
