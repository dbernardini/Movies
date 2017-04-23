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

import com.bernardini.danilo.movies.entries.Entry;
import com.bernardini.danilo.movies.entries.MovieEntry;
import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.asyncTasks.ImageTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter {

    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private List<Entry> movieList;
    private int resource;
    private Context context;
    private Activity activity;

    public MovieAdapter(Activity activity, Context context, int resource, List<Entry> movieList) {
        super(context, resource, movieList);
        this.resource = resource;
        this.movieList = movieList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource, parent, false);
        ImageView imageView = (ImageView) row.findViewById(R.id.movie_poster_list);
        TextView nameView = (TextView) row.findViewById(R.id.movie_title_list);
        TextView detailsView = (TextView) row.findViewById(R.id.movie_year_list);

        Entry entry = movieList.get(position);
        Drawable image = entry.getImage();
        if (image != null)
            imageView.setImageDrawable(image);
        nameView.setText(entry.getName());
        detailsView.setText(entry.getDetails());

        String imagePath = entry.getImagePath();

        String posterUrl = URL_IMAGE_API + "w300" + imagePath + "?api_key=" + API_KEY + "&language=it";

        Picasso.with(activity)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_image)
                .into(imageView);

        return row;
    }
}
