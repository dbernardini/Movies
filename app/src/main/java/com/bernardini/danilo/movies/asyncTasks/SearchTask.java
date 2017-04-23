package com.bernardini.danilo.movies.asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bernardini.danilo.movies.activities.MovieActivity;
import com.bernardini.danilo.movies.activities.PersonActivity;
import com.bernardini.danilo.movies.activities.TvActivity;
import com.bernardini.danilo.movies.adapters.MovieAdapter;
import com.bernardini.danilo.movies.entries.PersonEntry;
import com.bernardini.danilo.movies.entries.Entry;
import com.bernardini.danilo.movies.entries.MovieEntry;
import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.activities.DisplayResultsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchTask extends AsyncTask<Activity, Void, String> {

    private Activity activity;
    private TextView textView;
    private ListView listView;
    private ArrayList<Entry> entries;
    private ProgressDialog progDialog;

    public SearchTask(ProgressDialog progDialog){
        this.progDialog = progDialog;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Caricamento...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();
    }

    @Override
    protected String doInBackground(Activity... params) {
        activity = params[0];
        textView = (TextView) activity.findViewById(R.id.results);
        listView = (ListView) activity.findViewById(R.id.list_view);
        entries = new ArrayList<>();

        HttpURLConnection urlConnection = null;
        BufferedReader buffReader = null;
        String JSONResult = null;

        try {
            URL url = new URL(DisplayResultsActivity.stringUrl);
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


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();
        return JSONResult;
    }

    protected void onPostExecute(String result) {
        try {
            entries = getEntriesFromJSON(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (entries.size() == 0)
        textView.setVisibility(View.VISIBLE);

        progDialog.dismiss();

        MovieAdapter adapter = new MovieAdapter(activity, activity, R.layout.movie_row_layout, entries);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry)parent.getItemAtPosition(position);
                String entryId = entry.getId();
                String type = entry.getType();
                switch (type){
                    case "person":{
                        Intent intent = new Intent(activity, PersonActivity.class);
                        intent.putExtra("PERSON_ID", entryId);
                        activity.startActivity(intent);
                        break;
                    }
                    case "movie":{
                        Intent intent = new Intent(activity, MovieActivity.class);
                        intent.putExtra("MOVIE_ID", entryId);
                        activity.startActivity(intent);
                        break;
                    }
                    case "tv":{
                        Intent intent = new Intent(activity, TvActivity.class);
                        intent.putExtra("TV_ID", entryId);
                        activity.startActivity(intent);
                        break;
                    }
                }

            }
        });

    }

    private ArrayList<Entry> getEntriesFromJSON(String JSONString) throws JSONException{
        JSONObject JSONObject = new JSONObject(JSONString);
        JSONArray resultsArray = JSONObject.getJSONArray("results");
        int resultsLength = resultsArray.length();
        for (int i = 0; i < resultsLength; i++){
            JSONObject JSONResult = resultsArray.getJSONObject(i);
            String id = JSONResult.getString("id");
            String mediaType = JSONResult.getString("media_type");
            switch (mediaType){
                case "person":{
                    String name = JSONResult.getString("name");
                    String profilePath = JSONResult.getString("profile_path");
                    JSONArray knownForArray = JSONResult.getJSONArray("known_for");
                    String knownFor = "";
                    int knownForLength = knownForArray.length();
                    for (int j = 0; i < 3 && i < knownForLength; i++) {
                        JSONObject knownForObject = knownForArray.getJSONObject(i);
                        knownFor += knownForObject.getString("title") + ", ";
                    }
                    if (!knownFor.equals(""))
                        knownFor = knownFor.substring(0, knownFor.length() -2);
                    Entry entry = new Entry(name, knownFor, profilePath, id, "person");
                    entries.add(entry);
                    break;
                }
                case "movie":{
                    String title = JSONResult.getString("title");
                    String date = JSONResult.getString("release_date");
                    String year = "";
                    if (!date.equals(""))
                        year = date.substring(0,4);
                    String posterPath = JSONResult.getString("poster_path");
                    Entry entry = new Entry(title, year, posterPath, id, "movie");
                    entries.add(entry);
                    break;
                }
                case "tv":{
                    String title = JSONResult.getString("name");
                    String date = JSONResult.getString("first_air_date");
                    String year = "";
                    if (!date.equals(""))
                        year = date.substring(0,4);
                    String posterPath = JSONResult.getString("poster_path");
                    Entry entry = new Entry(title, year, posterPath, id, "tv");
                    entries.add(entry);
                    break;
                }
            }



        }
        return entries;
    }

}
