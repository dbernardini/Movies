package com.bernardini.danilo.movies.asyncTasks;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bernardini.danilo.movies.activities.PersonActivity;
import com.bernardini.danilo.movies.database.DBContract;
import com.bernardini.danilo.movies.database.DBManager;
import com.bernardini.danilo.movies.entries.Entry;
import com.bernardini.danilo.movies.activities.FullscreenImageActivity;
import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.activities.MovieActivity;
import com.squareup.picasso.Picasso;

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

public class MovieDetailsTask extends AsyncTask<MovieActivity, Void, String> {

    public final static String POSTER_PATH = "com.bernardini.danilo.movies.POSTER_PATH";
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_API = "https://api.themoviedb.org/3";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private MovieActivity activity;
    private ProgressDialog progDialog;
    private boolean seen = false;
    private boolean wish = false;
    private boolean own = false;
    private boolean allCast = false;
    private DBManager dbManager = null;
    private String movieId;

    public MovieDetailsTask(ProgressDialog progDialog){
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
    protected String doInBackground(MovieActivity... params) {

        activity = params[0];
        movieId = activity.getMovieId();

        dbManager = new DBManager(activity);
        Cursor cursor = dbManager.query("movie", "seen");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String dbId = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            if (dbId.equals(movieId)) {
                seen = true;
                break;
            }
            cursor.moveToNext();
        }
        cursor = dbManager.query("movie", "wish");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String dbId = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            if (dbId.equals(movieId)) {
                wish = true;
                break;
            }
            cursor.moveToNext();
        }
        cursor = dbManager.query("movie", "own");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String dbId = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            if (dbId.equals(movieId)) {
                own = true;
                break;
            }
            cursor.moveToNext();
        }


        HttpURLConnection movieConnection = null;
        BufferedReader movieBuffReader;

        HttpURLConnection creditsConnection = null;
        BufferedReader creditsBuffReader;

        String JSONResult = null;


        try {
            String movieUrl = URL_API + "/movie/" + movieId + "?api_key=" + API_KEY + "&language=it";
            String creditsUrl = URL_API + "/movie/" + movieId + "/credits?api_key=" + API_KEY;

            URL movieDetailsUrl = new URL(movieUrl);
            movieConnection = (HttpURLConnection) movieDetailsUrl.openConnection();
            movieConnection.setRequestMethod("GET");
            movieConnection.connect();

            URL movieCreditsUrl = new URL(creditsUrl);
            creditsConnection = (HttpURLConnection) movieCreditsUrl.openConnection();
            creditsConnection.setRequestMethod("GET");
            creditsConnection.connect();

            InputStream movieInputStream = movieConnection.getInputStream();
            if (movieInputStream == null)
                return null;
            movieBuffReader = new BufferedReader(new InputStreamReader(movieInputStream));

            InputStream creditsInputStream = creditsConnection.getInputStream();
            if (creditsInputStream == null)
                return null;
            creditsBuffReader = new BufferedReader(new InputStreamReader(creditsInputStream));


            StringBuffer movieBuff = new StringBuffer();
            String line;

            while ((line = movieBuffReader.readLine()) != null) {
                movieBuff.append(line + "\n");
            }
            if (movieBuff.length() == 0)
                return null;

            String movieJSONResult = movieBuff.toString();


            StringBuffer creditsBuff = new StringBuffer();

            while ((line = creditsBuffReader.readLine()) != null) {
                creditsBuff.append(line + "\n");
            }
            if (creditsBuff.length() == 0)
                return null;

            String creditsJSONResult = creditsBuff.toString();

            JSONResult = "{\"movie\":" + movieJSONResult + ",\"credits\":" + creditsJSONResult + "}";

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (movieConnection != null) {
            movieConnection.disconnect();
        }
        if (creditsConnection != null) {
            creditsConnection.disconnect();
        }

        return JSONResult;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject JSONObject = new JSONObject(result);
            JSONObject JSONMovie = JSONObject.getJSONObject("movie");
            JSONObject JSONCredits = JSONObject.getJSONObject("credits");

            final String title = JSONMovie.getString("title");
            String date = JSONMovie.getString("release_date");
            String year = "";
            if (!date.equals(""))
                year = date.substring(0, 4);
            final String posterPath = JSONMovie.getString("poster_path");

            JSONArray crewArray = JSONCredits.getJSONArray("crew");

            String director = "";
            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject crewMember = crewArray.getJSONObject(i);
                String job = crewMember.getString("job");
                if (job.equals("Director"))
                    director += crewMember.getString("name") + ", ";
            }
            if (!director.equals(""))
                director = director.substring(0, director.length()-2);


            JSONArray genresArray = JSONMovie.getJSONArray("genres");
            int genresLength = genresArray.length();
            String genres = "";
            for (int i = 0; i < genresLength; i++) {
                JSONObject JSONGenre = genresArray.getJSONObject(i);
                String genre = JSONGenre.getString("name");
                genres += genre + ", ";
            }
            if (!genres.equals(""))
                genres = genres.substring(0, genres.length()-2);

            String length = JSONMovie.getString("runtime");
            String rating = JSONMovie.getString("vote_average");
            String overView = JSONMovie.getString("overview");



            final ArrayList<Entry> castEntries = new ArrayList<>();

            JSONArray castArray = JSONCredits.getJSONArray("cast");

            int castLength = castArray.length();
            for (int i = 0; i < castLength; i++) {
                JSONObject JSONCastMember = castArray.getJSONObject(i);
                String name = JSONCastMember.getString("name");
                String character = JSONCastMember.getString("character");
                String profilePath = JSONCastMember.getString("profile_path");
                String id = JSONCastMember.getString("id");

                Entry personEntry = new Entry(name, character, profilePath, id, "person");
                castEntries.add(personEntry);
            }


            final ImageView posterView = (ImageView) activity.findViewById(R.id.movie_poster);
            TextView titleView = (TextView) activity.findViewById(R.id.movie_title);
            TextView yearView = (TextView) activity.findViewById(R.id.movie_year);
            TextView lengthView = (TextView) activity.findViewById(R.id.movie_length);
            TextView directorView = (TextView) activity.findViewById(R.id.movie_director);
            TextView genresView = (TextView) activity.findViewById(R.id.movie_genres);
            TextView ratingView = (TextView) activity.findViewById(R.id.movie_rating);
            TextView overviewView = (TextView) activity.findViewById(R.id.movie_overview);
            final TextView movieCast = (TextView) activity.findViewById(R.id.movie_cast);
            final TextView showAllCast = (TextView) activity.findViewById(R.id.show_all_cast);
            final CheckBox seenCheckBox = (CheckBox) activity.findViewById(R.id.seen_checkbox);
            final CheckBox wishCheckBox = (CheckBox) activity.findViewById(R.id.wish_checkbox);
            CheckBox ownCheckBox = (CheckBox) activity.findViewById(R.id.own_checkbox);


            posterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!posterPath.equals("null")) {
                        Intent intent = new Intent(activity, FullscreenImageActivity.class);
                        intent.putExtra(POSTER_PATH, posterPath);
                        activity.startActivity(intent);
                    }
                }
            });

            String posterUrl = URL_IMAGE_API + "w300" + posterPath + "?api_key=" + API_KEY + "&language=it";

            Picasso.with(activity)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.no_image)
                    .into(posterView);

            progDialog.dismiss();

            titleView.setText(title);
            if (!year.equals("") && !year.equals("null"))
                yearView.setText("Anno: " + year);
            else
                yearView.setText("Anno: -");
            if (!length.equals("null") && !length.equals("0"))
                lengthView.setText("Durata: " + length + " minuti");
            else
                lengthView.setText("Durata: -");
            if (!director.equals(""))
                directorView.setText("Regia: " + director);
            else
                directorView.setText("Regia: -");
            if (!genres.equals(""))
                genresView.setText("Genere: " + genres);
            else
                genresView.setText("Genere: -");
            if (!rating.equals("0.0"))
                ratingView.setText("Valutazione: " + rating + "/10");
            else
                ratingView.setText("Valutazione: -");
            if (!overView.equals("null"))
                overviewView.setText(overView);
            else
                overviewView.setText("");


            if (seen){
                seenCheckBox.setChecked(true);
            }
            else if (wish){
                wishCheckBox.setChecked(true);
            }
            if (own){
                ownCheckBox.setChecked(true);
            }

            seenCheckBox.setVisibility(View.VISIBLE);
            wishCheckBox.setVisibility(View.VISIBLE);
            ownCheckBox.setVisibility(View.VISIBLE);


            seenCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(seen){
                        seen = false;
                        dbManager.delete("movie", "seen", movieId);
                    }
                    else {
                        seen = true;
                        dbManager.insert("movie", "seen", movieId, title, posterPath);
                        if (wish) {
                            wish = false;
                            wishCheckBox.setChecked(false);
                            dbManager.delete("movie", "wish", movieId);
                        }
                    }
                }
            });

            wishCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(wish){
                        wish = false;
                        dbManager.delete("movie", "wish", movieId);
                    }
                    else {
                        wish = true;
                        dbManager.insert("movie", "wish", movieId, title, posterPath);
                        if (seen) {
                            seen = false;
                            seenCheckBox.setChecked(false);
                            dbManager.delete("movie", "seen", movieId);
                        }
                    }

                }
            });

            ownCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (own) {
                        own = false;
                        dbManager.delete("movie", "own", movieId);
                    }
                    else {
                        own = true;
                        dbManager.insert("movie", "own", movieId, title, posterPath);
                    }
                }
            });


            if (castLength > 0)
                movieCast.setText("Cast:");
            if (castLength > 5)
                showAllCast.setText("MOSTRA TUTTO");


            final LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.cast_layout);
            final LayoutInflater inflater = LayoutInflater.from(activity);
            final int castEntriesSize = castEntries.size();
            for (int i = 0; i < 5 && i < castEntriesSize; i++) {
                final Entry personEntry = castEntries.get(i);

                View castRow = inflater.inflate(R.layout.cast_row_layout, linearLayout, false);

                ImageView profileView = (ImageView) castRow.findViewById(R.id.cast_profile_pic);
                TextView nameView = (TextView) castRow.findViewById(R.id.cast_name);
                TextView characterView = (TextView) castRow.findViewById(R.id.cast_character);

                Drawable profilePic = personEntry.getImage();
                if (profilePic != null)
                    profileView.setImageDrawable(profilePic);
                nameView.setText(personEntry.getName());
                characterView.setText(personEntry.getDetails());

                String profilePath = personEntry.getImagePath();

                String path = URL_IMAGE_API + "w154" + profilePath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(path)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(profileView);

                castRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, PersonActivity.class);
                        intent.putExtra("PERSON_ID", personEntry.getId());
                        activity.startActivity(intent);
                    }
                });
                linearLayout.addView(castRow);
            }

            assert showAllCast != null;
            showAllCast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(allCast) {
                        allCast = false;
                        TextView b = (TextView) v;
                        b.setText("MOSTRA TUTTO");
                        for (int i = castEntriesSize-1; i > 4; i--) {
                            linearLayout.removeViewAt(i);
                        }
                    }
                    else{
                        allCast = true;
                        TextView b = (TextView) v;
                        b.setText("MOSTRA MENO");
                        for (int i = 5; i < castEntriesSize; i++) {
                            final Entry personEntry = castEntries.get(i);

                            View castRow = inflater.inflate(R.layout.cast_row_layout, linearLayout, false);

                            ImageView profileView = (ImageView) castRow.findViewById(R.id.cast_profile_pic);
                            TextView nameView = (TextView) castRow.findViewById(R.id.cast_name);
                            TextView characterView = (TextView) castRow.findViewById(R.id.cast_character);

                            Drawable profilePic = personEntry.getImage();
                            if (profilePic != null)
                                profileView.setImageDrawable(profilePic);
                            nameView.setText(personEntry.getName());
                            characterView.setText(personEntry.getDetails());

                            String profilePath = personEntry.getImagePath();

                            String path = URL_IMAGE_API + "w92" + profilePath + "?api_key=" + API_KEY + "&language=it";

                            Picasso.with(activity)
                                    .load(path)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.no_image)
                                    .into(profileView);

                            castRow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(activity, PersonActivity.class);
                                    intent.putExtra("PERSON_ID", personEntry.getId());
                                    activity.startActivity(intent);
                                }
                            });
                            linearLayout.addView(castRow);
                        }
                    }
                }
            });

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }


}