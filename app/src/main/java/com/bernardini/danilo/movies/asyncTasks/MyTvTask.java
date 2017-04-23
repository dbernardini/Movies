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

public class MyTvTask extends AsyncTask<Void, Void, LinkedList<Entry>[]> {

    private final int SEEN_TV = 10;
    private final int WISH_TV = 11;
    private final int WATCHING_TV = 12;
    private final static String API_KEY = "fb15b5b20cf78591805745a88b5ce4ff";
    private final static String URL_IMAGE_API = "http://image.tmdb.org/t/p/";
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinkedList<Entry> seenTv = new LinkedList<>();
    private LinkedList<Entry> wishTv = new LinkedList<>();
    private LinkedList<Entry> ownTv = new LinkedList<>();

    public MyTvTask(Activity activity) {
        this.activity = activity;
        this.swipeRefreshLayout = null;
    }

    public MyTvTask(Activity activity, SwipeRefreshLayout swipeRefreshLayout) {
        this.activity = activity;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected LinkedList<Entry>[] doInBackground(Void... params) {

        LinkedList<Entry>[] result = new LinkedList[3];

        DBManager dbManager = new DBManager(activity);

        Cursor cursor = dbManager.query("tv", "seen");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
            String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
            seenTv.add(new Entry(name, path, id, "tv"));
            cursor.moveToNext();
        }
        result[0] = seenTv;

        cursor = dbManager.query("tv", "wish");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
            String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
            wishTv.add(new Entry(name, path, id, "tv"));
            cursor.moveToNext();
        }
        result[1] = wishTv;

        cursor = dbManager.query("tv", "own");
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String name = cursor.getString(cursor.getColumnIndex(DBContract.TITLE));
            String id = cursor.getString(cursor.getColumnIndex(DBContract.ID));
            String path = cursor.getString(cursor.getColumnIndex(DBContract.PATH));
            ownTv.add(new Entry(name, path, id, "tv"));
            cursor.moveToNext();
        }
        result[2] = ownTv;

        return result;
    }

    @Override
    protected void onPostExecute(LinkedList<Entry>[] result) {

        Button seenTvButton = (Button) activity.findViewById(R.id.seen_tv_button);
        Button wishTvButton = (Button) activity.findViewById(R.id.wish_tv_button);
        Button watchingTvButton = (Button) activity.findViewById(R.id.own_tv_button);

        seenTvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", SEEN_TV);
                activity.startActivity(intent);
            }
        });

        wishTvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", WISH_TV);
                activity.startActivity(intent);
            }
        });

        watchingTvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", WATCHING_TV);
                activity.startActivity(intent);
            }
        });


        LinkedList<Entry> seenTv = result[0];
        LinkedList<Entry> wishTv = result[1];
        LinkedList<Entry> ownTv = result[2];

        if (seenTv.size() <= 10)
            seenTvButton.setVisibility(View.INVISIBLE);
        else
            seenTvButton.setVisibility(View.VISIBLE);
        if (wishTv.size() <= 10)
            wishTvButton.setVisibility(View.INVISIBLE);
        else
            wishTvButton.setVisibility(View.VISIBLE);
        if (ownTv.size() <= 10)
            watchingTvButton.setVisibility(View.INVISIBLE);
        else
            watchingTvButton.setVisibility(View.VISIBLE);

        LinearLayout seenTvLayout = (LinearLayout) activity.findViewById(R.id.seen_tv_layout);
        LinearLayout wishTvLayout = (LinearLayout) activity.findViewById(R.id.wish_tv_layout);
        LinearLayout ownTvLayout = (LinearLayout) activity.findViewById(R.id.own_tv_layout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 50, 0, 0);

        if (seenTv.size() != 0)
            setPosters(seenTv, seenTvLayout);
        else {
            if (seenTvLayout.getChildAt(0) != null)
                seenTvLayout.removeAllViews();
            TextView noTv = new TextView(activity);
            noTv.setText("Nessuna serie vista");
            noTv.setLayoutParams(layoutParams);
            seenTvLayout.addView(noTv);
        }
        if (wishTv.size() != 0)
            setPosters(wishTv, wishTvLayout);
        else {
            if (wishTvLayout.getChildAt(0) != null)
                wishTvLayout.removeAllViews();
            TextView noTv = new TextView(activity);
            noTv.setText("Nessuna serie da vedere");
            noTv.setLayoutParams(layoutParams);
            wishTvLayout.addView(noTv);

        }
        if (ownTv.size() != 0)
            setPosters(ownTv, ownTvLayout);
        else {
            if (ownTvLayout.getChildAt(0) != null)
                ownTvLayout.removeAllViews();
            TextView noTv = new TextView(activity);
            noTv.setText("Non sto vedendo nessuna serie");
            noTv.setLayoutParams(layoutParams);
            ownTvLayout.addView(noTv);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPosters(final LinkedList<Entry> entries, LinearLayout layout){

        layout.removeAllViews();

        for (int i = 0; i < 10 && i < entries.size(); i++){
            final Entry entry = entries.get(i);
            String posterPath = entry.getImagePath();
            ImageView imageView = new ImageView(activity);

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
