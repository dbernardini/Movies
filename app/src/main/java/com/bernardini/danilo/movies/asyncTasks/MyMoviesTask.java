package com.bernardini.danilo.movies.asyncTasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.activities.MovieActivity;
import com.bernardini.danilo.movies.activities.ShowAllActivity;
import com.bernardini.danilo.movies.activities.TvActivity;
import com.bernardini.danilo.movies.database.DBContract;
import com.bernardini.danilo.movies.database.DBManager;
import com.bernardini.danilo.movies.entries.Entry;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class MyMoviesTask extends AsyncTask<Void, Void, LinkedList<Entry>[]> {

    private final int SEEN_MOVIES = 7;
    private final int WISH_MOVIES = 8;
    private final int OWN_MOVIES = 9;
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinkedList<Entry> seenMovies = new LinkedList<>();
    private LinkedList<Entry> wishMovies = new LinkedList<>();
    private LinkedList<Entry> ownMovies = new LinkedList<>();

    public MyMoviesTask(Activity activity) {
        this.activity = activity;
        this.swipeRefreshLayout = null;
    }

    public MyMoviesTask(Activity activity, SwipeRefreshLayout swipeRefreshLayout) {
        this.activity = activity;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected LinkedList<Entry>[] doInBackground(Void... params) {

        LinkedList<Entry>[] result = new LinkedList[3];

        DBManager dbManager = new DBManager(activity);

        Cursor cursor = dbManager.query("movie", "seen");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
            String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
            seenMovies.add(new Entry(name, path, id, "movie"));
            cursor.moveToNext();
        }
        result[0] = seenMovies;

        cursor = dbManager.query("movie", "wish");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
            String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
            wishMovies.add(new Entry(name, path, id, "movie"));
            cursor.moveToNext();
        }
        result[1] = wishMovies;

        cursor = dbManager.query("movie", "own");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
            String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
            ownMovies.add(new Entry(name, path, id, "movie"));
            cursor.moveToNext();
        }
        result[2] = ownMovies;
        return result;
    }

    @Override
    protected void onPostExecute(LinkedList<Entry>[] result) {

        Button seenMoviesButton = (Button) activity.findViewById(R.id.seen_movies_button);
        Button wishMoviesButton = (Button) activity.findViewById(R.id.wish_movies_button);
        Button ownMoviesButton = (Button) activity.findViewById(R.id.own_movies_button);

        seenMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", SEEN_MOVIES);
                activity.startActivity(intent);
            }
        });

        wishMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", WISH_MOVIES);
                activity.startActivity(intent);
            }
        });

        ownMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", OWN_MOVIES);
                activity.startActivity(intent);
            }
        });


        LinkedList<Entry> seenMovies = result[0];
        LinkedList<Entry> wishMovies = result[1];
        LinkedList<Entry> ownMovies = result[2];

        if (seenMovies.size() <= 10)
            seenMoviesButton.setVisibility(View.INVISIBLE);
        else
            seenMoviesButton.setVisibility(View.VISIBLE);
        if (wishMovies.size() <= 10)
            wishMoviesButton.setVisibility(View.INVISIBLE);
        else
            wishMoviesButton.setVisibility(View.VISIBLE);
        if (ownMovies.size() <= 10)
            ownMoviesButton.setVisibility(View.INVISIBLE);
        else
            ownMoviesButton.setVisibility(View.VISIBLE);

        LinearLayout seenMoviesLayout = (LinearLayout) activity.findViewById(R.id.seen_movies_layout);
        LinearLayout wishMoviesLayout = (LinearLayout) activity.findViewById(R.id.wish_movies_layout);
        LinearLayout ownMoviesLayout = (LinearLayout) activity.findViewById(R.id.own_movies_layout);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 50, 0, 0);

        if (seenMovies.size() != 0)
            setPosters(seenMovies, seenMoviesLayout);
        else {
            if (seenMoviesLayout.getChildAt(0) != null)
                seenMoviesLayout.removeAllViews();
            TextView noMovies = new TextView(activity);
            noMovies.setText("Nessun film visto");
            noMovies.setLayoutParams(layoutParams);
            seenMoviesLayout.addView(noMovies);

        }
        if (wishMovies.size() != 0)
            setPosters(wishMovies, wishMoviesLayout);
        else {
            if (wishMoviesLayout.getChildAt(0) != null)
                wishMoviesLayout.removeAllViews();
            TextView noMovies = new TextView(activity);
            noMovies.setText("Nessun film da vedere");
            noMovies.setLayoutParams(layoutParams);
            wishMoviesLayout.addView(noMovies);

        }
        if (ownMovies.size() != 0)
            setPosters(ownMovies, ownMoviesLayout);
        else {
            if (ownMoviesLayout.getChildAt(0) != null)
                ownMoviesLayout.removeAllViews();
            TextView noMovies = new TextView(activity);
            noMovies.setText("Nessun film acquistato");
            noMovies.setLayoutParams(layoutParams);
            ownMoviesLayout.addView(noMovies);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPosters(final LinkedList<Entry> entries, LinearLayout layout){

        layout.removeAllViews();

        for (int i = 0; i < 10 && i < entries.size(); i++){
            final Entry entry = entries.get(i);
            String posterPath = entry.getImagePath();
            ImageView imageView = new ImageView(activity);
            imageView.setTag(entry.getId());
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, activity.getResources().getDisplayMetrics());
            int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112, activity.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            layoutParams.setMarginEnd(5);
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
                    String type = entry.getType();
                    if (type.equals("movie")) {
                        Intent intent = new Intent(activity, MovieActivity.class);
                        intent.putExtra("MOVIE_ID", entry.getId());
                        activity.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(activity, TvActivity.class);
                        intent.putExtra("TV_ID", entry.getId());
                        activity.startActivity(intent);
                    }
                }
            });
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

}
