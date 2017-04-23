package com.bernardini.danilo.movies.asyncTasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.activities.MovieActivity;
import com.bernardini.danilo.movies.activities.TvActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetPostersTask extends AsyncTask<Integer, Void, String>  {

    private Activity activity;
    private LinearLayout layout;

    private final static int POPULAR_MOVIES = 0;
    private final static int RATED_MOVIES = 1;
    private final static int NOW_PLAYING_MOVIES = 2;
    private final static int UPCOMING_MOVIES = 3;
    private final static int POPULAR_TV = 4;
    private final static int RATED_TV = 5;
    private final static int UPCOMING_TV = 6;
    private final static String API_URL_MOVIE = "http://api.themoviedb.org/3/discover/movie";
    private final static String API_URL_TV = "http://api.themoviedb.org/3/discover/tv";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private String type;

    public SetPostersTask(Activity activity, LinearLayout layout, String type) {
        this.activity = activity;
        this.layout = layout;
        this.type = type;
    }

    @Override
    protected String doInBackground(Integer... params) {
        int code = params[0];
        String result = getJSON(code);
        return result;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostExecute(String result) {

        try {
            JSONObject JSONObject = new JSONObject(result);
            JSONArray resultsArray = JSONObject.getJSONArray("results");
            int resultsLength = resultsArray.length();
            for (int i = 0; i < 10 && i < resultsLength; i++) {
                final JSONObject JSONResult = resultsArray.getJSONObject(i);
                final String id = JSONResult.getString("id");
                String posterPath = JSONResult.getString("poster_path");
                ImageView imageView = new ImageView(activity);
                imageView.setTag(id);
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, activity.getResources().getDisplayMetrics());
                int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112, activity.getResources().getDisplayMetrics());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                layoutParams.setMarginEnd(15);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                layout.addView(imageView);

                String posterUrl = URL_IMAGE_API + "w300" + posterPath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals("movie")) {
                            Intent intent = new Intent(activity, MovieActivity.class);
                            intent.putExtra("MOVIE_ID", id);
                            activity.startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(activity, TvActivity.class);
                            intent.putExtra("TV_ID", id);
                            activity.startActivity(intent);
                        }
                    }

                });
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String getJSON(int queryType){
        String stringUrl = "";
        switch (queryType){
            case POPULAR_MOVIES: {
                stringUrl = API_URL_MOVIE + "?sort_by=popularity.desc&api_key=" + API_KEY + "&language=it";
                break;
            }
            case RATED_MOVIES: {
                stringUrl = API_URL_MOVIE + "?sort_by=vote_average.desc&vote_count.gte=3000&api_key=" + API_KEY + "&language=it";
                break;
            }
            case NOW_PLAYING_MOVIES: {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.MONTH, -1);
                String currentDate = dateFormat.format(date);
                String pastDate = dateFormat.format(calendar.getTime());
                stringUrl = API_URL_MOVIE + "?primary_release_date.gte=" + pastDate + "&primary_release_date.lte=" + currentDate +"&api_key=" + API_KEY + "&language=it";
                break;
            }
            case UPCOMING_MOVIES:{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                String tomorrowDate = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 6);
                String futureDate = dateFormat.format(calendar.getTime());
                stringUrl = API_URL_MOVIE + "?primary_release_date.gte=" + tomorrowDate + "&primary_release_date.lte=" + futureDate +"&api_key=" + API_KEY + "&language=it";
                break;
            }
            case POPULAR_TV: {
                stringUrl = API_URL_TV + "?sort_by=popularity.desc&api_key=" + API_KEY + "&language=it";
                break;
            }
            case RATED_TV: {
                stringUrl = API_URL_TV + "?sort_by=vote_average.desc&vote_count.gte=200&api_key=" + API_KEY + "&language=it";
                break;
            }
            case UPCOMING_TV:{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                String tomorrowDate = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 6);
                String futureDate = dateFormat.format(calendar.getTime());
                stringUrl = API_URL_TV + "?first_air_date.gte=" + tomorrowDate + "&first_air_date.lte=" + futureDate +"&api_key=" + API_KEY + "&language=it";
                break;
            }
        }
        HttpURLConnection urlConnection = null;
        BufferedReader buffReader;
        String JSONResult = null;
        try {
            URL url = new URL(stringUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null)
                return null;
            buffReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer buff = new StringBuffer();
            String line;

            while ((line = buffReader.readLine()) != null) {
                buff.append(line + "\n");
            }

            if (buff.length() == 0)
                return null;

            JSONResult = buff.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();
        return JSONResult;
    }
}
