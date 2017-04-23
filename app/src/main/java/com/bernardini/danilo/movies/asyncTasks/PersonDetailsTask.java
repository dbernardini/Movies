package com.bernardini.danilo.movies.asyncTasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.activities.FullscreenImageActivity;
import com.bernardini.danilo.movies.activities.MovieActivity;
import com.bernardini.danilo.movies.activities.PersonActivity;
import com.bernardini.danilo.movies.activities.TvActivity;
import com.bernardini.danilo.movies.entries.Entry;
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

public class PersonDetailsTask extends AsyncTask<PersonActivity, Void, String>  {

    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_API = "https://api.themoviedb.org/3";
    public final static String PROFILE_PATH = "com.bernardini.danilo.movies.PROFILE_PATH";
    private ProgressDialog progDialog;
    private PersonActivity activity;
    private String gender;
    private boolean allMoviesActor = false;
    private boolean allTvActor = false;
    private boolean allMoviesDirector = false;
    private boolean allTvDirector = false;
    private boolean allMoviesComposer = false;
    private boolean allTvComposer = false;


    public PersonDetailsTask(ProgressDialog progDialog){
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
    protected String doInBackground(PersonActivity... params) {
        activity = params[0];
        String personId = activity.getPersonID();

        HttpURLConnection personConnection = null;
        BufferedReader personBuffReader;

        HttpURLConnection creditsConnection = null;
        BufferedReader creditsBuffReader;

        HttpURLConnection tvCreditsConnection = null;
        BufferedReader tvCreditsBuffReader;

        String JSONResult = null;

        try {
            String personUrl = URL_API + "/person/" + personId + "?api_key=" + API_KEY + "&language=it";
            String movieCreditsUrl = URL_API + "/person/" + personId + "/movie_credits?api_key=" + API_KEY + "&language=it";
            String tvCreditsUrl = URL_API + "/person/" + personId + "/tv_credits?api_key=" + API_KEY + "&language=it";

            URL personDetailsUrl = new URL(personUrl);
            personConnection = (HttpURLConnection) personDetailsUrl.openConnection();
            personConnection.setRequestMethod("GET");
            personConnection.connect();

            URL creditsDetailsUrl = new URL(movieCreditsUrl);
            creditsConnection = (HttpURLConnection) creditsDetailsUrl.openConnection();
            creditsConnection.setRequestMethod("GET");
            creditsConnection.connect();

            URL tvCreditsDetailsUrl = new URL(tvCreditsUrl);
            tvCreditsConnection = (HttpURLConnection) tvCreditsDetailsUrl.openConnection();
            tvCreditsConnection.setRequestMethod("GET");
            tvCreditsConnection.connect();

            InputStream personInputStream = personConnection.getInputStream();
            if (personInputStream == null)
                return null;
            personBuffReader = new BufferedReader(new InputStreamReader(personInputStream));

            InputStream creditsInputStream = creditsConnection.getInputStream();
            if (creditsInputStream == null)
                return null;
            creditsBuffReader = new BufferedReader(new InputStreamReader(creditsInputStream));

            InputStream tvCreditsInputStream = tvCreditsConnection.getInputStream();
            if (tvCreditsInputStream == null)
                return null;
            tvCreditsBuffReader = new BufferedReader(new InputStreamReader(tvCreditsInputStream));


            StringBuffer personBuffer = new StringBuffer();
            String line;
            while ((line = personBuffReader.readLine()) != null) {
                personBuffer.append(line + "\n");
            }
            if (personBuffer.length() == 0)
                return null;
            String personJSONResult = personBuffer.toString();

            StringBuffer creditsBuffer = new StringBuffer();
            while ((line = creditsBuffReader.readLine()) != null) {
                creditsBuffer.append(line + "\n");
            }
            if (creditsBuffer.length() == 0)
                return null;
            String creditsJSONResult = creditsBuffer.toString();

            StringBuffer tvCreditsBuffer = new StringBuffer();
            while ((line = tvCreditsBuffReader.readLine()) != null) {
                tvCreditsBuffer.append(line + "\n");
            }
            if (tvCreditsBuffer.length() == 0)
                return null;
            String tvCreditsJSONResult = tvCreditsBuffer.toString();


            JSONResult = "{\"person\":" + personJSONResult + ",\"movie_credits\":" + creditsJSONResult + ",\"tv_credits\":" + tvCreditsJSONResult + "}";


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        personConnection.disconnect();
        creditsConnection.disconnect();
        tvCreditsConnection.disconnect();

        return JSONResult;
    }

    @Override
    protected void onPostExecute(String result) {
        String name = "";
        final String profilePath;
        String birthday = "";
        String deathday = "";
        String birthPlace = "";
        String biography = "";
        try {
            JSONObject JSONObject = new JSONObject(result);
            JSONObject JSONPerson = JSONObject.getJSONObject("person");
            JSONObject JSONMovieCredits = JSONObject.getJSONObject("movie_credits");
            JSONObject JSONTvCredits = JSONObject.getJSONObject("tv_credits");

            name = JSONPerson.getString("name");
            profilePath = JSONPerson.getString("profile_path");
            birthday = JSONPerson.getString("birthday");
            deathday = JSONPerson.getString("deathday");
            birthPlace = JSONPerson.getString("place_of_birth");
            biography = JSONPerson.getString("biography");
            gender = JSONPerson.getString("gender");

            final ArrayList<Entry> movieCastEntries = new ArrayList<>();
            final ArrayList<Entry> movieDirectorEntries = new ArrayList<>();
            final ArrayList<Entry> movieComposerEntries = new ArrayList<>();
            final ArrayList<Entry> tvCastEntries = new ArrayList<>();
            final ArrayList<Entry> tvDirectorEntries = new ArrayList<>();
            final ArrayList<Entry> tvComposerEntries = new ArrayList<>();


            JSONArray movieCastArray = JSONMovieCredits.getJSONArray("cast");
            JSONArray movieCrewArray = JSONMovieCredits.getJSONArray("crew");

            JSONArray tvCastArray = JSONTvCredits.getJSONArray("cast");
            JSONArray tvCrewArray = JSONTvCredits.getJSONArray("crew");

            int movieCastArrayLength = movieCastArray.length();
            for (int i = 0; i < movieCastArrayLength; i++) {
                JSONObject JSONMovie = movieCastArray.getJSONObject(i);
                String title = JSONMovie.getString("title");
                String date = JSONMovie.getString("release_date");
                String year = "";
                if (!date.equals(""))
                    year = date.substring(0,4);
                String posterPath = JSONMovie.getString("poster_path");
                String id = JSONMovie.getString("id");
                if (posterPath != null) {
                    Entry movieEntry = new Entry(title, year, posterPath, id, "movie");
                    movieCastEntries.add(movieEntry);
                }
            }

            int movieCrewArrayLength = movieCrewArray.length();
            for (int i = 0; i < movieCrewArrayLength; i++) {
                JSONObject JSONMovie = movieCrewArray.getJSONObject(i);
                String department = JSONMovie.getString("department");
                String job = JSONMovie.getString("job");
                if (department.equals("Directing") && job.equals("Director")) {
                    String title = JSONMovie.getString("title");
                    String date = JSONMovie.getString("release_date");
                    String year = "";
                    if (!date.equals(""))
                        year = date.substring(0, 4);
                    String posterPath = JSONMovie.getString("poster_path");
                    String id = JSONMovie.getString("id");
                    if (!posterPath.equals("null")) {
                        Entry movieEntry = new Entry(title, year, posterPath, id, "movie");
                        movieDirectorEntries.add(movieEntry);
                    }
                }
                else if (department.equals("Sound") && job.equals("Original Music Composer")) {
                    String title = JSONMovie.getString("title");
                    String date = JSONMovie.getString("release_date");
                    String year = "";
                    if (!date.equals(""))
                        year = date.substring(0, 4);
                    String posterPath = JSONMovie.getString("poster_path");
                    String id = JSONMovie.getString("id");
                    if (!posterPath.equals("null")) {
                        Entry movieEntry = new Entry(title, year, posterPath, id, "movie");
                        movieComposerEntries.add(movieEntry);
                    }
                }
            }


            int tvCastArrayLength = tvCastArray.length();
            for (int i = 0; i < tvCastArrayLength; i++) {
                JSONObject JSONMovie = tvCastArray.getJSONObject(i);
                String title = JSONMovie.getString("name");
                String date = JSONMovie.getString("first_air_date");
                String year = "";
                if (!date.equals(""))
                    year = date.substring(0,4);
                String posterPath = JSONMovie.getString("poster_path");
                String id = JSONMovie.getString("id");
                if (!posterPath.equals("null")) {
                    Entry movieEntry = new Entry(title, year, posterPath, id, "movie");
                    tvCastEntries.add(movieEntry);
                }
            }

            int tvCrewArrayLength = tvCrewArray.length();
            for (int i = 0; i < tvCrewArrayLength; i++) {
                JSONObject JSONMovie = tvCrewArray.getJSONObject(i);
                String department = JSONMovie.getString("department");
                String job = JSONMovie.getString("job");
                if (department.equals("Directing") && job.equals("Director")) {
                    String title = JSONMovie.getString("name");
                    String date = JSONMovie.getString("first_air_date");
                    String year = "";
                    if (!date.equals(""))
                        year = date.substring(0, 4);
                    String posterPath = JSONMovie.getString("poster_path");
                    String id = JSONMovie.getString("id");
                    if (!posterPath.equals("null")) {
                        Entry movieEntry = new Entry(title, year, posterPath, id, "movie");
                        tvDirectorEntries.add(movieEntry);
                    }
                }
                else if (department.equals("Sound") && job.equals("Original Music Composer")) {
                    String title = JSONMovie.getString("name");
                    String date = JSONMovie.getString("first_air_date");
                    String year = "";
                    if (!date.equals(""))
                        year = date.substring(0, 4);
                    String posterPath = JSONMovie.getString("poster_path");
                    String id = JSONMovie.getString("id");
                    if (!posterPath.equals("null")) {
                        Entry movieEntry = new Entry(title, year, posterPath, id, "movie");
                        tvComposerEntries.add(movieEntry);
                    }
                }
            }


            ImageView profileView = (ImageView) activity.findViewById(R.id.person_profile);
            TextView nameView = (TextView) activity.findViewById(R.id.person_name);
            TextView birthdayView = (TextView) activity.findViewById(R.id.person_birthday);
            TextView deathdayView = (TextView) activity.findViewById(R.id.person_deathday);
            TextView birthPlaceView = (TextView) activity.findViewById(R.id.person_place_birth);
            TextView biographyView = (TextView) activity.findViewById(R.id.person_biography);


            profileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!profilePath.equals("null")) {
                        Intent intent = new Intent(activity, FullscreenImageActivity.class);
                        intent.putExtra(PROFILE_PATH, profilePath);
                        activity.startActivity(intent);
                    }
                }
            });


            String posterUrl = URL_IMAGE_API + "w300" + profilePath + "?api_key=" + API_KEY + "&language=it";

            Picasso.with(activity)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.no_image)
                    .into(profileView);

            progDialog.dismiss();

            nameView.setText(name);
            if (!birthday.equals("") && birthday!= null && !birthday.equals("null"))
                birthdayView.setText("Data di nascita: " + getDate(birthday));
            else
                birthdayView.setText("Data di nascita: -");
            if (deathday != null && !deathday.equals("null") && !deathday.equals(""))
                deathdayView.setText("Data di morte: " + getDate(deathday));
            else {
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, R.id.person_birthday);
                p.addRule(RelativeLayout.RIGHT_OF, R.id.person_profile);
                birthPlaceView.setLayoutParams(p);
            }
            if (birthPlace != null && !birthPlace.equals("null"))
                birthPlaceView.setText("Luogo di nascita: " + birthPlace);
            else
                birthPlaceView.setText("Luogo di nascita: -");
            if (biography != null && !biography.equals("null") && !biography.equals(""))
                biographyView.setText(biography);


            int actorSize = movieCastEntries.size() + tvCastEntries.size();
            int directorSize = movieDirectorEntries.size() + tvDirectorEntries.size();
            int composerSize = movieComposerEntries.size() + tvComposerEntries.size();

            //attore
            if (actorSize > directorSize && actorSize >= composerSize){
                setActor(movieCastEntries, tvCastEntries);
                setDirector(movieDirectorEntries, tvDirectorEntries);
                setComposer(movieComposerEntries, tvComposerEntries);
            }

            //regista
            else if (directorSize >= actorSize && directorSize >= composerSize){
                setDirector(movieDirectorEntries, tvDirectorEntries);
                setActor(movieCastEntries, tvCastEntries);
                setComposer(movieComposerEntries, tvComposerEntries);
            }

            //compositore
            else if (composerSize >= actorSize && composerSize >= directorSize){
                setComposer(movieComposerEntries, tvComposerEntries);
                setDirector(movieDirectorEntries, tvDirectorEntries);
                setActor(movieCastEntries, tvCastEntries);
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setActor(final ArrayList<Entry> movieCastEntries, final ArrayList<Entry> tvCastEntries){
        if (movieCastEntries.size() > 0) {
            final LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout);

            TextView actorTextView = new TextView(activity);
            actorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            if (gender.equals("1"))
                actorTextView.setText("Attrice (Cinema):");
            else
                actorTextView.setText("Attore (Cinema):");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 50, 0, 10);
            actorTextView.setLayoutParams(layoutParams);
            linearLayout.addView(actorTextView);


            final LayoutInflater inflater = LayoutInflater.from(activity);
            final LinearLayout actorLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            actorLayout.setOrientation(LinearLayout.VERTICAL);
            actorLayout.setLayoutParams(params);

            final int movieEntriesSize = movieCastEntries.size();

            for (int i = 0; i < 5 && i < movieEntriesSize; i++) {
                final Entry movieEntry = movieCastEntries.get(i);

                View movieRow = inflater.inflate(R.layout.movie_row_layout, actorLayout, false);

                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                View line = movieRow.findViewById(R.id.line);
//                line.setVisibility(View.VISIBLE);

                Drawable posterImage = movieEntry.getImage();
                if (posterImage != null)
                    posterView.setImageDrawable(posterImage);
                titleView.setText(movieEntry.getName());
                yearView.setText(movieEntry.getDetails());

                String posterPath = movieEntry.getImagePath();

                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(posterView);

                movieRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, MovieActivity.class);
                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                        activity.startActivity(intent);
                    }
                });
                actorLayout.addView(movieRow);
            }
            linearLayout.addView(actorLayout);

            if(movieEntriesSize > 5) {
                TextView showAllView = new TextView(activity);
                actorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                showAllView.setText("MOSTRA TUTTO");
                showAllView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                LinearLayout.LayoutParams textlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textlayoutParams.setMargins(0, 20, 0, 10);
                showAllView.setLayoutParams(textlayoutParams);
                linearLayout.addView(showAllView);

                showAllView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(allMoviesActor) {
                            allMoviesActor = false;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA TUTTO");
                            for (int i = movieEntriesSize-1; i > 4; i--) {
                                actorLayout.removeViewAt(i);
                            }
                        }
                        else {
                            allMoviesActor = true;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA MENO");
                            for (int i = 5; i < movieEntriesSize; i++) {
                                final Entry movieEntry = movieCastEntries.get(i);

                                View movieRow = inflater.inflate(R.layout.movie_row_layout, actorLayout, false);

                                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                                View line = movieRow.findViewById(R.id.line);
//                                line.setVisibility(View.VISIBLE);

                                Drawable posterImage = movieEntry.getImage();
                                if (posterImage != null)
                                    posterView.setImageDrawable(posterImage);
                                titleView.setText(movieEntry.getName());
                                yearView.setText(movieEntry.getDetails());

                                String posterPath = movieEntry.getImagePath();

                                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                                Picasso.with(activity)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.no_image)
                                        .into(posterView);

                                movieRow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, MovieActivity.class);
                                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                                        activity.startActivity(intent);
                                    }
                                });
                                actorLayout.addView(movieRow);
                            }
                        }
                    }
                });
            }
        }

        if (tvCastEntries.size() > 0) {
            LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout);

            TextView actorTextView = new TextView(activity);
            actorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            if (gender.equals("1"))
                actorTextView.setText("Attrice (TV):");
            else
                actorTextView.setText("Attore (TV):");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 50, 0, 10);
            actorTextView.setLayoutParams(layoutParams);
            linearLayout.addView(actorTextView);


            final LayoutInflater inflater = LayoutInflater.from(activity);
            final LinearLayout actorLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            actorLayout.setOrientation(LinearLayout.VERTICAL);
            actorLayout.setLayoutParams(params);


            final int tvEntriesSize = tvCastEntries.size();

            for (int i = 0; i < 5 && i < tvEntriesSize; i++) {
                final Entry movieEntry = tvCastEntries.get(i);

                View movieRow = inflater.inflate(R.layout.movie_row_layout, actorLayout, false);

                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                View line = movieRow.findViewById(R.id.line);
//                line.setVisibility(View.VISIBLE);

                Drawable posterImage = movieEntry.getImage();
                if (posterImage != null)
                    posterView.setImageDrawable(posterImage);
                titleView.setText(movieEntry.getName());
                yearView.setText(movieEntry.getDetails());

                String posterPath = movieEntry.getImagePath();

                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(posterView);

                movieRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, TvActivity.class);
                        intent.putExtra("TV_ID", movieEntry.getId());
                        activity.startActivity(intent);
                    }
                });
                actorLayout.addView(movieRow);
            }
            linearLayout.addView(actorLayout);

            if(tvEntriesSize > 5) {
                TextView showAllView = new TextView(activity);
                actorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                showAllView.setText("MOSTRA TUTTO");
                showAllView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                LinearLayout.LayoutParams textlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textlayoutParams.setMargins(0, 20, 0, 10);
                showAllView.setLayoutParams(textlayoutParams);
                linearLayout.addView(showAllView);

                showAllView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(allTvActor) {
                            allTvActor = false;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA TUTTO");
                            for (int i = tvEntriesSize-1; i > 4; i--) {
                                actorLayout.removeViewAt(i);
                            }
                        }
                        else {
                            allTvActor = true;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA MENO");
                            for (int i = 5; i < tvEntriesSize; i++) {
                                final Entry movieEntry = tvCastEntries.get(i);

                                View movieRow = inflater.inflate(R.layout.movie_row_layout, actorLayout, false);

                                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                                View line = movieRow.findViewById(R.id.line);
//                                line.setVisibility(View.VISIBLE);

                                Drawable posterImage = movieEntry.getImage();
                                if (posterImage != null)
                                    posterView.setImageDrawable(posterImage);
                                titleView.setText(movieEntry.getName());
                                yearView.setText(movieEntry.getDetails());

                                String posterPath = movieEntry.getImagePath();

                                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                                Picasso.with(activity)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.no_image)
                                        .into(posterView);

                                movieRow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, MovieActivity.class);
                                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                                        activity.startActivity(intent);
                                    }
                                });
                                actorLayout.addView(movieRow);
                            }
                        }
                    }
                });
            }
        }

    }

    private void setDirector(final ArrayList<Entry> movieDirectorEntries, final ArrayList<Entry> tvDirectorEntries){
        if (movieDirectorEntries.size() > 0) {
            LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout);
            TextView directorTextView = new TextView(activity);
            directorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            directorTextView.setText("Regista (Cinema):");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 50, 0, 10);
            directorTextView.setLayoutParams(layoutParams);
            linearLayout.addView(directorTextView);

            final LayoutInflater inflater = LayoutInflater.from(activity);

            final LinearLayout directorLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            directorLayout.setOrientation(LinearLayout.VERTICAL);
            directorLayout.setLayoutParams(params);

            final int movieEntriesSize = movieDirectorEntries.size();
            for (int i = 0; i < 5 && i < movieEntriesSize; i++) {
                final Entry movieEntry = movieDirectorEntries.get(i);

                View movieRow = inflater.inflate(R.layout.movie_row_layout, directorLayout, false);

                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                View line = movieRow.findViewById(R.id.line);
//                line.setVisibility(View.VISIBLE);

                Drawable posterImage = movieEntry.getImage();
                if (posterImage != null)
                    posterView.setImageDrawable(posterImage);
                titleView.setText(movieEntry.getName());
                yearView.setText(movieEntry.getDetails());

                String posterPath = movieEntry.getImagePath();

                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(posterView);

                movieRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, MovieActivity.class);
                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                        activity.startActivity(intent);
                    }
                });
                directorLayout.addView(movieRow);
            }
            linearLayout.addView(directorLayout);

            if(movieEntriesSize > 5) {
                TextView showAllView = new TextView(activity);
                directorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                showAllView.setText("MOSTRA TUTTO");
                showAllView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                LinearLayout.LayoutParams textlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textlayoutParams.setMargins(0, 20, 0, 10);
                showAllView.setLayoutParams(textlayoutParams);
                linearLayout.addView(showAllView);

                showAllView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(allMoviesDirector) {
                            allMoviesDirector = false;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA TUTTO");
                            for (int i = movieEntriesSize-1; i > 4; i--) {
                                directorLayout.removeViewAt(i);
                            }
                        }
                        else {
                            allMoviesDirector = true;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA MENO");
                            for (int i = 5; i < movieEntriesSize; i++) {
                                final Entry movieEntry = movieDirectorEntries.get(i);

                                View movieRow = inflater.inflate(R.layout.movie_row_layout, directorLayout, false);

                                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                                View line = movieRow.findViewById(R.id.line);
//                                line.setVisibility(View.VISIBLE);

                                Drawable posterImage = movieEntry.getImage();
                                if (posterImage != null)
                                    posterView.setImageDrawable(posterImage);
                                titleView.setText(movieEntry.getName());
                                yearView.setText(movieEntry.getDetails());

                                String posterPath = movieEntry.getImagePath();

                                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                                Picasso.with(activity)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.no_image)
                                        .into(posterView);

                                movieRow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, MovieActivity.class);
                                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                                        activity.startActivity(intent);
                                    }
                                });
                                directorLayout.addView(movieRow);
                            }
                        }
                    }
                });
            }
        }

        if (tvDirectorEntries.size() > 0) {
            LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout);
            TextView directorTextView = new TextView(activity);
            directorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            directorTextView.setText("Regista (TV):");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 50, 0, 10);
            directorTextView.setLayoutParams(layoutParams);
            linearLayout.addView(directorTextView);

            final LayoutInflater inflater = LayoutInflater.from(activity);

            final LinearLayout directorLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            directorLayout.setOrientation(LinearLayout.VERTICAL);
            directorLayout.setLayoutParams(params);

            final int tvEntriesSize = tvDirectorEntries.size();
            for (int i = 0; i < 5 && i < tvEntriesSize; i++) {
                final Entry movieEntry = tvDirectorEntries.get(i);

                View movieRow = inflater.inflate(R.layout.movie_row_layout, directorLayout, false);

                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                View line = movieRow.findViewById(R.id.line);
//                line.setVisibility(View.VISIBLE);

                Drawable posterImage = movieEntry.getImage();
                if (posterImage != null)
                    posterView.setImageDrawable(posterImage);
                titleView.setText(movieEntry.getName());
                yearView.setText(movieEntry.getDetails());

                String posterPath = movieEntry.getImagePath();

                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(posterView);

                movieRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, TvActivity.class);
                        intent.putExtra("TV_ID", movieEntry.getId());
                        activity.startActivity(intent);
                    }
                });
                directorLayout.addView(movieRow);
            }
            linearLayout.addView(directorLayout);

            if(tvEntriesSize > 5) {
                TextView showAllView = new TextView(activity);
                directorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                showAllView.setText("MOSTRA TUTTO");
                showAllView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                LinearLayout.LayoutParams textlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textlayoutParams.setMargins(0, 20, 0, 10);
                showAllView.setLayoutParams(textlayoutParams);
                linearLayout.addView(showAllView);

                showAllView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(allTvDirector) {
                            allTvDirector = false;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA TUTTO");
                            for (int i = tvEntriesSize-1; i > 4; i--) {
                                directorLayout.removeViewAt(i);
                            }
                        }
                        else {
                            allTvDirector = true;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA MENO");
                            for (int i = 5; i < tvEntriesSize; i++) {
                                final Entry movieEntry = tvDirectorEntries.get(i);

                                View movieRow = inflater.inflate(R.layout.movie_row_layout, directorLayout, false);

                                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                                View line = movieRow.findViewById(R.id.line);
//                                line.setVisibility(View.VISIBLE);

                                Drawable posterImage = movieEntry.getImage();
                                if (posterImage != null)
                                    posterView.setImageDrawable(posterImage);
                                titleView.setText(movieEntry.getName());
                                yearView.setText(movieEntry.getDetails());

                                String posterPath = movieEntry.getImagePath();

                                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                                Picasso.with(activity)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.no_image)
                                        .into(posterView);

                                movieRow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, MovieActivity.class);
                                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                                        activity.startActivity(intent);
                                    }
                                });
                                directorLayout.addView(movieRow);
                            }
                        }
                    }
                });
            }
        }
    }

    private void setComposer(final ArrayList<Entry> movieComposerEntries, final ArrayList<Entry> tvComposerEntries){
        if (movieComposerEntries.size() > 0) {
            LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout);
            TextView composerTextView = new TextView(activity);
            composerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            composerTextView.setText("Compositore (Cinema):");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 50, 0, 10);
            composerTextView.setLayoutParams(layoutParams);
            linearLayout.addView(composerTextView);

            final LayoutInflater inflater = LayoutInflater.from(activity);
            final LinearLayout composerLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            composerLayout.setOrientation(LinearLayout.VERTICAL);
            composerLayout.setLayoutParams(params);


            final int movieEntriesSize = movieComposerEntries.size();
            for (int i = 0; i < 5 && i < movieEntriesSize; i++) {
                final Entry movieEntry = movieComposerEntries.get(i);

                View movieRow = inflater.inflate(R.layout.movie_row_layout, composerLayout, false);

                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                View line = movieRow.findViewById(R.id.line);
//                line.setVisibility(View.VISIBLE);

                Drawable posterImage = movieEntry.getImage();
                if (posterImage != null)
                    posterView.setImageDrawable(posterImage);
                titleView.setText(movieEntry.getName());
                yearView.setText(movieEntry.getDetails());

                String posterPath = movieEntry.getImagePath();

                String posterUrl = URL_IMAGE_API + "w300" + posterPath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(posterView);


                movieRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, MovieActivity.class);
                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                        activity.startActivity(intent);
                    }
                });
                composerLayout.addView(movieRow);
            }
            linearLayout.addView(composerLayout);

            if(movieEntriesSize > 5) {
                TextView showAllView = new TextView(activity);
                composerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                showAllView.setText("MOSTRA TUTTO");
                showAllView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                LinearLayout.LayoutParams textlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textlayoutParams.setMargins(0, 20, 0, 10);
                showAllView.setLayoutParams(textlayoutParams);
                linearLayout.addView(showAllView);

                showAllView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(allMoviesComposer) {
                            allMoviesComposer = false;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA TUTTO");
                            for (int i = movieEntriesSize-1; i > 4; i--) {
                                composerLayout.removeViewAt(i);
                            }
                        }
                        else {
                            allMoviesComposer = true;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA MENO");
                            for (int i = 5; i < movieEntriesSize; i++) {
                                final Entry movieEntry = movieComposerEntries.get(i);

                                View movieRow = inflater.inflate(R.layout.movie_row_layout, composerLayout, false);

                                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                                View line = movieRow.findViewById(R.id.line);
//                                line.setVisibility(View.VISIBLE);

                                Drawable posterImage = movieEntry.getImage();
                                if (posterImage != null)
                                    posterView.setImageDrawable(posterImage);
                                titleView.setText(movieEntry.getName());
                                yearView.setText(movieEntry.getDetails());

                                String posterPath = movieEntry.getImagePath();

                                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                                Picasso.with(activity)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.no_image)
                                        .into(posterView);

                                movieRow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, MovieActivity.class);
                                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                                        activity.startActivity(intent);
                                    }
                                });
                                composerLayout.addView(movieRow);
                            }
                        }
                    }
                });
            }
        }

        if (tvComposerEntries.size() > 0) {
            LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout);
            TextView composerTextView = new TextView(activity);
            composerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            composerTextView.setText("Compositore (TV):");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 50, 0, 10);
            composerTextView.setLayoutParams(layoutParams);
            linearLayout.addView(composerTextView);

            final LayoutInflater inflater = LayoutInflater.from(activity);
            final LinearLayout composerLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            composerLayout.setOrientation(LinearLayout.VERTICAL);
            composerLayout.setLayoutParams(params);


            final int tvEntriesSize = tvComposerEntries.size();
            for (int i = 0; i < 5 && i < tvEntriesSize; i++) {
                final Entry movieEntry = tvComposerEntries.get(i);

                View movieRow = inflater.inflate(R.layout.movie_row_layout, composerLayout, false);

                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                View line = movieRow.findViewById(R.id.line);
//                line.setVisibility(View.VISIBLE);

                Drawable posterImage = movieEntry.getImage();
                if (posterImage != null)
                    posterView.setImageDrawable(posterImage);
                titleView.setText(movieEntry.getName());
                yearView.setText(movieEntry.getDetails());

                String posterPath = movieEntry.getImagePath();

                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                Picasso.with(activity)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.no_image)
                        .into(posterView);

                movieRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, TvActivity.class);
                        intent.putExtra("TV_ID", movieEntry.getId());
                        activity.startActivity(intent);
                    }
                });
                composerLayout.addView(movieRow);
            }
            linearLayout.addView(composerLayout);

            if(tvEntriesSize > 5) {
                TextView showAllView = new TextView(activity);
                composerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                showAllView.setText("MOSTRA TUTTO");
                showAllView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                LinearLayout.LayoutParams textlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textlayoutParams.setMargins(0, 20, 0, 10);
                showAllView.setLayoutParams(textlayoutParams);
                linearLayout.addView(showAllView);

                showAllView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(allTvComposer) {
                            allTvComposer = false;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA TUTTO");
                            for (int i = tvEntriesSize-1; i > 4; i--) {
                                composerLayout.removeViewAt(i);
                            }
                        }
                        else {
                            allTvComposer = true;
                            TextView b = (TextView) v;
                            b.setText("MOSTRA MENO");
                            for (int i = 5; i < tvEntriesSize; i++) {
                                final Entry movieEntry = tvComposerEntries.get(i);

                                View movieRow = inflater.inflate(R.layout.movie_row_layout, composerLayout, false);

                                ImageView posterView = (ImageView) movieRow.findViewById(R.id.movie_poster_list);
                                TextView titleView = (TextView) movieRow.findViewById(R.id.movie_title_list);
                                TextView yearView = (TextView) movieRow.findViewById(R.id.movie_year_list);
//                                View line = movieRow.findViewById(R.id.line);
//                                line.setVisibility(View.VISIBLE);

                                Drawable posterImage = movieEntry.getImage();
                                if (posterImage != null)
                                    posterView.setImageDrawable(posterImage);
                                titleView.setText(movieEntry.getName());
                                yearView.setText(movieEntry.getDetails());

                                String posterPath = movieEntry.getImagePath();

                                String posterUrl = URL_IMAGE_API + "w154" + posterPath + "?api_key=" + API_KEY + "&language=it";

                                Picasso.with(activity)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.no_image)
                                        .into(posterView);

                                movieRow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, MovieActivity.class);
                                        intent.putExtra("MOVIE_ID", movieEntry.getId());
                                        activity.startActivity(intent);
                                    }
                                });
                                composerLayout.addView(movieRow);
                            }
                        }
                    }
                });
            }
        }

    }

    private String getDate(String date){
        String day = date.substring(8,10);
        String month = date.substring(5,7);
        String year = date.substring(0,4);
        switch (month){
            case "01" : {
                month = "gennaio";
                break;
            }
            case "02" : {
                month = "febbraio";
                break;
            }
            case "03" : {
                month = "marzo";
                break;
            }
            case "04" : {
                month = "aprile";
                break;
            }
            case "05" : {
                month = "maggio";
                break;
            }
            case "06" : {
                month = "giugno";
                break;
            }
            case "07" : {
                month = "luglio";
                break;
            }
            case "08" : {
                month = "agosto";
                break;
            }
            case "09" : {
                month = "settembre";
                break;
            }
            case "10" : {
                month = "ottobre";
                break;
            }
            case "11" : {
                month = "novembre";
                break;
            }
            case "12" : {
                month = "dicembre";
                break;
            }
        }
        return day + " " + month + " " + year;
    }

}
