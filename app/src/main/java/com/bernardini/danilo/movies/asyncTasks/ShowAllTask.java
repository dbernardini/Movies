package com.bernardini.danilo.movies.asyncTasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.activities.MovieActivity;
import com.bernardini.danilo.movies.activities.TvActivity;
import com.bernardini.danilo.movies.adapters.ImageAdapter;
import com.bernardini.danilo.movies.database.DBContract;
import com.bernardini.danilo.movies.database.DBManager;
import com.bernardini.danilo.movies.entries.Entry;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowAllTask extends AsyncTask<Void, Void, ArrayList<Entry>>{

    private final static String API_URL_MOVIE = "http://api.themoviedb.org/3/discover/movie";
    private final static String API_URL_TV = "http://api.themoviedb.org/3/discover/tv";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";

    private final int POPULAR_MOVIES = 0;
    private final int RATED_MOVIES = 1;
    private final int NOW_PLAYING_MOVIES = 2;
    private final int UPCOMING_MOVIES = 3;
    private final int POPULAR_TV = 4;
    private final int RATED_TV = 5;
    private final int UPCOMING_TV = 6;
    private final int SEEN_MOVIES = 7;
    private final int WISH_MOVIES = 8;
    private final int OWN_MOVIES = 9;
    private final int SEEN_TV = 10;
    private final int WISH_TV = 11;
    private final int WATCHING_TV = 12;

    private Activity activity;
    private int target;
    private GridView gridView;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ShowAllTask(Activity activity, int target, ProgressDialog progressDialog){
        this.activity = activity;
        this.target = target;
        this.progressDialog = progressDialog;
        this.swipeRefreshLayout = null;
    }

    public ShowAllTask(Activity activity, int target, ProgressDialog progressDialog, SwipeRefreshLayout swipeRefreshLayout){
        this.activity = activity;
        this.target = target;
        this.progressDialog = progressDialog;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected ArrayList<Entry> doInBackground(Void... params) {
        ArrayList<Entry> result = null;
        String stringUrl = "";
        DBManager dbManager = new DBManager(activity);

        switch (target){

            case POPULAR_MOVIES: {
                stringUrl = API_URL_MOVIE + "?sort_by=popularity.desc&api_key=" + API_KEY + "&language=it";
                result = getJSONMovie(stringUrl);
                break;
            }
            case RATED_MOVIES: {
                stringUrl = API_URL_MOVIE + "?sort_by=vote_average.desc&vote_count.gte=3000&api_key=" + API_KEY + "&language=it";
                result = getJSONMovie(stringUrl);
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
                result = getJSONMovie(stringUrl);
                break;
            }
            case UPCOMING_MOVIES: {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                String tomorrowDate = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 6);
                String futureDate = dateFormat.format(calendar.getTime());
                stringUrl = API_URL_MOVIE + "?primary_release_date.gte=" + tomorrowDate + "&primary_release_date.lte=" + futureDate +"&api_key=" + API_KEY + "&language=it";
                result = getJSONMovie(stringUrl);
                break;
            }
            case POPULAR_TV: {
                stringUrl = API_URL_TV + "?sort_by=popularity.desc&api_key=" + API_KEY + "&language=it";
                result = getJSONTv(stringUrl);
                break;
            }
            case RATED_TV: {
                stringUrl = API_URL_TV + "?sort_by=vote_average.desc&vote_count.gte=100&api_key=" + API_KEY + "&language=it";
                result = getJSONTv(stringUrl);
                break;
            }
            case UPCOMING_TV: {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 1);
                String tomorrowDate = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 6);
                String futureDate = dateFormat.format(calendar.getTime());
                stringUrl = API_URL_TV + "?first_air_date.gte=" + tomorrowDate + "&first_air_date.lte=" + futureDate +"&api_key=" + API_KEY + "&language=it";
                result = getJSONTv(stringUrl);
                break;
            }
            case SEEN_MOVIES: {
                result = new ArrayList<>();
                Cursor cursor = dbManager.query("movie", "seen");
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
                    String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
                    String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
                    result.add(new Entry(name, path, id, "movie"));
                    cursor.moveToNext();
                }
                break;
            }
            case WISH_MOVIES: {
                result = new ArrayList<>();
                Cursor cursor = dbManager.query("movie", "wish");
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
                    String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
                    String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
                    result.add(new Entry(name, path, id, "movie"));
                    cursor.moveToNext();
                }
                break;
            }
            case OWN_MOVIES: {
                result = new ArrayList<>();
                Cursor cursor = dbManager.query("movie", "own");
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
                    String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
                    String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
                    result.add(new Entry(name, path, id, "movie"));
                    cursor.moveToNext();
                }
                break;
            }
            case SEEN_TV: {
                result = new ArrayList<>();
                Cursor cursor = dbManager.query("tv", "seen");
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
                    String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
                    String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
                    result.add(new Entry(name, path, id, "tv"));
                    cursor.moveToNext();
                }
                break;
            }
            case WISH_TV: {
                result = new ArrayList<>();
                Cursor cursor = dbManager.query("tv", "wish");
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
                    String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
                    String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
                    result.add(new Entry(name, path, id, "tv"));
                    cursor.moveToNext();
                }
                break;
            }
            case WATCHING_TV: {
                result = new ArrayList<>();
                Cursor cursor = dbManager.query("tv", "own");
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
                    String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
                    String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
                    result.add(new Entry(name, path, id, "tv"));
                    cursor.moveToNext();
                }
                break;
            }
        }

        return result;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostExecute(ArrayList<Entry> entries) {

        gridView = (GridView) activity.findViewById(R.id.gridview);

        progressDialog.dismiss();

        ImageAdapter adapter = new ImageAdapter(activity, entries);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getItemAtPosition(position);
                String entryId = entry.getId();
                String type = entry.getType();
                if (type.equals("movie")){
                    Intent intent = new Intent(activity, MovieActivity.class);
                    intent.putExtra("MOVIE_ID", entryId);
                    activity.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(activity, TvActivity.class);
                    intent.putExtra("TV_ID", entryId);
                    activity.startActivity(intent);
                }
            }
        });

        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    private ArrayList<Entry> getJSONMovie(String stringUrl){

        ArrayList<Entry> result = new ArrayList<>();
        String JSONStringResult1 = null;
        String JSONStringResult2 = null;
        String JSONStringResult = null;
        HttpURLConnection urlConnection = null;
        BufferedReader buffReader;
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

            JSONStringResult1 = buff.toString();



            url = new URL(stringUrl + "&page=2");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            if (inputStream == null)
                return null;
            buffReader = new BufferedReader(new InputStreamReader(inputStream));

            buff = new StringBuffer();

            while ((line = buffReader.readLine()) != null) {
                buff.append(line + "\n");
            }

            if (buff.length() == 0)
                return null;

            JSONStringResult2 = buff.toString();

            JSONStringResult = "{\"page1\":" + JSONStringResult1 + ",\"page2\":" + JSONStringResult2 + "}";


        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();


        try {
            JSONObject JSONObject = new JSONObject(JSONStringResult);
            JSONObject JSONObject1 = JSONObject.getJSONObject("page1");
            JSONArray resultsArray1 = JSONObject1.getJSONArray("results");
            int resultsLength = resultsArray1.length();
            for (int i = 0; i < resultsLength; i++) {
                final JSONObject JSONResult = resultsArray1.getJSONObject(i);
                String title = JSONResult.getString("title");
                final String id = JSONResult.getString("id");
                String posterPath = JSONResult.getString("poster_path");
                String posterUrl = URL_IMAGE_API + "w300" + posterPath + "?api_key=" + API_KEY + "&language=it";

                result.add(new Entry(title, posterUrl, id, "movie"));
            }
            JSONObject JSONObject2 = JSONObject.getJSONObject("page2");
            JSONArray resultsArray2 = JSONObject2.getJSONArray("results");
            resultsLength = resultsArray2.length();
            for (int i = 0; i < resultsLength; i++) {
                final JSONObject JSONResult = resultsArray2.getJSONObject(i);
                String title = JSONResult.getString("title");
                final String id = JSONResult.getString("id");
                String posterPath = JSONResult.getString("poster_path");
                String posterUrl = URL_IMAGE_API + "w300" + posterPath + "?api_key=" + API_KEY + "&language=it";

                result.add(new Entry(title, posterUrl, id, "movie"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return result;
    }

    private ArrayList<Entry> getJSONTv(String stringUrl){

        ArrayList<Entry> result = new ArrayList<>();
        String JSONStringResult1 = null;
        String JSONStringResult2 = null;
        String JSONStringResult = null;
        HttpURLConnection urlConnection = null;
        BufferedReader buffReader;
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

            JSONStringResult1 = buff.toString();


            url = new URL(stringUrl + "&page=2");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            if (inputStream == null)
                return null;
            buffReader = new BufferedReader(new InputStreamReader(inputStream));

            buff = new StringBuffer();

            while ((line = buffReader.readLine()) != null) {
                buff.append(line + "\n");
            }

            if (buff.length() == 0)
                return null;

            JSONStringResult2 = buff.toString();

            JSONStringResult = "{\"page1\":" + JSONStringResult1 + ",\"page2\":" + JSONStringResult2 + "}";

        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();


        try {
            JSONObject JSONObject = new JSONObject(JSONStringResult);
            JSONObject JSONObject1 = JSONObject.getJSONObject("page1");
            JSONArray resultsArray1 = JSONObject1.getJSONArray("results");
            int resultsLength = resultsArray1.length();
            for (int i = 0; i < resultsLength; i++) {
                final JSONObject JSONResult = resultsArray1.getJSONObject(i);
                String name = JSONResult.getString("name");
                final String id = JSONResult.getString("id");
                String posterPath = JSONResult.getString("poster_path");
                String posterUrl = URL_IMAGE_API + "w300" + posterPath + "?api_key=" + API_KEY + "&language=it";

                result.add(new Entry(name, posterUrl, id, "tv"));
            }
            JSONObject JSONObject2 = JSONObject.getJSONObject("page2");
            JSONArray resultsArray2 = JSONObject2.getJSONArray("results");
            resultsLength = resultsArray2.length();
            for (int i = 0; i < resultsLength; i++) {
                final JSONObject JSONResult = resultsArray2.getJSONObject(i);
                String name = JSONResult.getString("name");
                final String id = JSONResult.getString("id");
                String posterPath = JSONResult.getString("poster_path");
                String posterUrl = URL_IMAGE_API + "w300" + posterPath + "?api_key=" + API_KEY + "&language=it";

                result.add(new Entry(name, posterUrl, id, "tv"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return result;
    }
}
