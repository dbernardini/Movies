package com.bernardini.danilo.movies.asyncTasks;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bernardini.danilo.movies.activities.PersonActivity;
import com.bernardini.danilo.movies.activities.TvActivity;
import com.bernardini.danilo.movies.database.DBContract;
import com.bernardini.danilo.movies.database.DBManager;
import com.bernardini.danilo.movies.entries.Entry;
import com.bernardini.danilo.movies.activities.FullscreenImageActivity;
import com.bernardini.danilo.movies.R;
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

public class TvDetailsTask extends AsyncTask<TvActivity, Void, String> {

    public final static String TV_PATH = "com.bernardini.danilo.movies.TV_PATH";
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_API = "https://api.themoviedb.org/3";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";

    private TvActivity activity;
    private ProgressDialog progDialog;
    private boolean seen = false;
    private boolean wish = false;
    private boolean watching = false;
    private boolean allCast = false;
    private String tvId;
    private DBManager dbManager;

    public TvDetailsTask(ProgressDialog progDialog){
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
    protected String doInBackground(TvActivity... params) {

        activity = params[0];
        tvId = activity.getTvId();

        dbManager = new DBManager(activity);
        Cursor cursor = dbManager.query("tv", "seen");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String dbId = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            if (dbId.equals(tvId)) {
                seen = true;
                break;
            }
            cursor.moveToNext();
        }
        cursor = dbManager.query("tv", "wish");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String dbId = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            if (dbId.equals(tvId)) {
                wish = true;
                break;
            }
            cursor.moveToNext();
        }
        cursor = dbManager.query("tv", "own");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String dbId = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            if (dbId.equals(tvId)) {
                watching = true;
                break;
            }
            cursor.moveToNext();
        }


        HttpURLConnection tvConnection = null;
        BufferedReader tvBuffReader;

        HttpURLConnection creditsConnection = null;
        BufferedReader creditsBuffReader;

        String JSONResult = null;


        try {
            String tvUrl = URL_API + "/tv/" + tvId + "?api_key=" + API_KEY + "&language=it";
            String creditsUrl = URL_API + "/tv/" + tvId + "/credits?api_key=" + API_KEY;

            URL tvDetailsUrl = new URL(tvUrl);
            tvConnection = (HttpURLConnection) tvDetailsUrl.openConnection();
            tvConnection.setRequestMethod("GET");
            tvConnection.connect();

            URL tvCreditsUrl = new URL(creditsUrl);
            creditsConnection = (HttpURLConnection) tvCreditsUrl.openConnection();
            creditsConnection.setRequestMethod("GET");
            creditsConnection.connect();

            InputStream tvInputStream = tvConnection.getInputStream();
            if (tvInputStream == null)
                return null;
            tvBuffReader = new BufferedReader(new InputStreamReader(tvInputStream));

            InputStream creditsInputStream = creditsConnection.getInputStream();
            if (creditsInputStream == null)
                return null;
            creditsBuffReader = new BufferedReader(new InputStreamReader(creditsInputStream));


            StringBuffer tvBuff = new StringBuffer();
            String line;

            while ((line = tvBuffReader.readLine()) != null) {
                tvBuff.append(line + "\n");
            }
            if (tvBuff.length() == 0)
                return null;

            String tvJSONResult = tvBuff.toString();


            StringBuffer creditsBuff = new StringBuffer();

            while ((line = creditsBuffReader.readLine()) != null) {
                creditsBuff.append(line + "\n");
            }
            if (creditsBuff.length() == 0)
                return null;

            String creditsJSONResult = creditsBuff.toString();

            JSONResult = "{\"tv\":" + tvJSONResult + ",\"credits\":" + creditsJSONResult + "}";

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tvConnection != null) {
            tvConnection.disconnect();
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
            JSONObject JSONTv = JSONObject.getJSONObject("tv");
            JSONObject JSONCredits = JSONObject.getJSONObject("credits");

            final String title = JSONTv.getString("name");
            String date = JSONTv.getString("first_air_date");
            String year = "";
            if (!date.equals(""))
                year = date.substring(0, 4);
            final String posterPath = JSONTv.getString("poster_path");

            JSONArray createdByArray = JSONTv.getJSONArray("created_by");

            String creator = "";
            for (int i = 0; i < createdByArray.length(); i++) {
                JSONObject JSONCreator = createdByArray.getJSONObject(i);
                creator += JSONCreator.getString("name") + ", ";
            }
            if (!creator.equals(""))
                creator = creator.substring(0, creator.length()-2);


            JSONArray genresArray = JSONTv.getJSONArray("genres");
            int genresLength = genresArray.length();
            String genres = "";
            for (int i = 0; i < genresLength; i++) {
                JSONObject JSONGenre = genresArray.getJSONObject(i);
                String genre = JSONGenre.getString("name");
                genres += genre + ", ";
            }
            if (!genres.equals(""))
                genres = genres.substring(0, genres.length()-2);

            String seasons = JSONTv.getString("number_of_seasons");
            String rating = JSONTv.getString("vote_average");
            String overView = JSONTv.getString("overview");



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


            final ImageView posterView = (ImageView) activity.findViewById(R.id.tv_poster);
            TextView titleView = (TextView) activity.findViewById(R.id.tv_title);
            TextView yearView = (TextView) activity.findViewById(R.id.tv_year);
            TextView seasonsView = (TextView) activity.findViewById(R.id.tv_seasons);
            TextView creatorView = (TextView) activity.findViewById(R.id.tv_creator);
            TextView genresView = (TextView) activity.findViewById(R.id.tv_genres);
            TextView ratingView = (TextView) activity.findViewById(R.id.tv_rating);
            TextView overviewView = (TextView) activity.findViewById(R.id.tv_overview);
            TextView movieCast = (TextView) activity.findViewById(R.id.tv_cast);
            final TextView showAllCast = (TextView) activity.findViewById(R.id.show_all_cast);
            final CheckBox seenCheckBox = (CheckBox) activity.findViewById(R.id.seen_checkbox);
            final CheckBox wishCheckBox = (CheckBox) activity.findViewById(R.id.wish_checkbox);
            final CheckBox watchingCheckBox = (CheckBox) activity.findViewById(R.id.watching_checkbox);


            posterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!posterPath.equals("null")) {
                        Intent intent = new Intent(activity, FullscreenImageActivity.class);
                        intent.putExtra(TV_PATH, posterPath);
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
            if (!seasons.equals("null"))
                seasonsView.setText("Stagioni: " + seasons);
            else
                seasonsView.setText("Stagioni: -");
            if (!creator.equals(""))
                creatorView.setText("Creato da: " + creator);
            else
                creatorView.setText("Creato da: -");
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


            if (seen)
                seenCheckBox.setChecked(true);
            else if (wish)
                wishCheckBox.setChecked(true);
            else if (watching)
                watchingCheckBox.setChecked(true);

            seenCheckBox.setVisibility(View.VISIBLE);
            wishCheckBox.setVisibility(View.VISIBLE);
            watchingCheckBox.setVisibility(View.VISIBLE);

            seenCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(seen){
                        seen = false;
                        dbManager.delete("tv", "seen", tvId);
                    }
                    else {
                        seen = true;
                        dbManager.insert("tv", "seen", tvId, title, posterPath);
                        if (wish) {
                            wish = false;
                            wishCheckBox.setChecked(false);
                            dbManager.delete("tv", "wish", tvId);
                        }
                        if (watching) {
                            watching = false;
                            watchingCheckBox.setChecked(false);
                            dbManager.delete("tv", "own", tvId);
                        }
                    }
                }
            });

            wishCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(wish){
                        wish = false;
                        dbManager.delete("tv", "wish", tvId);
                    }
                    else {
                        wish = true;
                        dbManager.insert("tv", "wish", tvId, title, posterPath);
                        if (seen) {
                            seen = false;
                            seenCheckBox.setChecked(false);
                            dbManager.delete("tv", "seen", tvId);
                        }
                        if (watching) {
                            watching = false;
                            watchingCheckBox.setChecked(false);
                            dbManager.delete("tv", "own", tvId);
                        }
                    }

                }
            });

            watchingCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(watching){
                        watching = false;
                        dbManager.delete("tv", "own", tvId);
                    }
                    else {
                        watching = true;
                        dbManager.insert("tv", "own", tvId, title, posterPath);
                        if (seen) {
                            seen = false;
                            seenCheckBox.setChecked(false);
                            dbManager.delete("tv", "seen", tvId);
                        }
                        if (wish) {
                            wish = false;
                            wishCheckBox.setChecked(false);
                            dbManager.delete("tv", "wish", tvId);
                        }
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

                            String posterUrl = URL_IMAGE_API + "w92" + profilePath + "?api_key=" + API_KEY + "&language=it";

                            Picasso.with(activity)
                                    .load(posterUrl)
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
