package com.bernardini.danilo.movies.asyncTasks;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;

import com.bernardini.danilo.movies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageTask extends AsyncTask<String,Void,Drawable> {

    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_IMAGE_API = "https://image.tmdb.org/t/p/";
    private String posterPath;
    private Activity activity;
    private ImageView posterView;
    private ProgressDialog progDialog;

    public ImageTask(Activity activity, ImageView posterView, ProgressDialog progDialog){
        this.activity = activity;
        this.posterView = posterView;
        this.progDialog = progDialog;
    }

    public ImageTask(Activity activity, ImageView posterView){
        this.activity = activity;
        this.posterView = posterView;
        this.progDialog = null;
    }

    protected void onPreExecute() {
        if (progDialog != null) {
            super.onPreExecute();
            progDialog.setMessage("Caricamento...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.show();
        }
    }

    @Override
    protected Drawable doInBackground(String... params) {

        posterPath = params[0];
        if (posterPath.equals("null"))
            return null;
        String size = params[1];
        String posterUrl = URL_IMAGE_API + size + posterPath;

        InputStream is = null;
        try {
            is = (InputStream) new URL(posterUrl).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable poster = Drawable.createFromStream(is, "poster");
        return poster;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPostExecute(Drawable poster) {
        if (progDialog != null)
            progDialog.dismiss();
        if (poster != null)
            posterView.setImageDrawable(poster);
        else
            posterView.setImageDrawable(activity.getDrawable(R.drawable.no_image));

    }
}
