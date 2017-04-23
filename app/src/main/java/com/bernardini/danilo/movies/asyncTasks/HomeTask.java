package com.bernardini.danilo.movies.asyncTasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.activities.ShowAllActivity;


public class HomeTask extends AsyncTask<Void, Void, String> {

    private final static int POPULAR_MOVIES = 0;
    private final static int RATED_MOVIES = 1;
    private final static int NOW_PLAYING_MOVIES = 2;
    private final static int UPCOMING_MOVIES = 3;
    private final static int POPULAR_TV = 4;
    private final static int RATED_TV = 5;
    private final static int UPCOMING_TV = 6;
    private Activity activity;

    public HomeTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {

        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostExecute(String result) {

        Button popularMoviesButton = (Button) activity.findViewById(R.id.popular_movies_button);
        Button ratedMoviesButton = (Button) activity.findViewById(R.id.rated_movies_button);
        Button playingMoviesButton = (Button) activity.findViewById(R.id.playing_movies_button);
        Button upcomingMoviesButton = (Button) activity.findViewById(R.id.upcoming_movies_button);
        Button popularTvButton = (Button) activity.findViewById(R.id.popular_tv_button);
        Button ratedTvButton = (Button) activity.findViewById(R.id.rated_tv_button);
        Button upcomingTvButton = (Button) activity.findViewById(R.id.upcoming_tv_button);

        popularMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", POPULAR_MOVIES);
                activity.startActivity(intent);
            }
        });

        ratedMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", RATED_MOVIES);
                activity.startActivity(intent);
            }
        });

        playingMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", NOW_PLAYING_MOVIES);
                activity.startActivity(intent);
            }
        });

        upcomingMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", UPCOMING_MOVIES);
                activity.startActivity(intent);
            }
        });

        popularTvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", POPULAR_TV);
                activity.startActivity(intent);
            }
        });

        ratedTvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", RATED_TV);
                activity.startActivity(intent);
            }
        });

        upcomingTvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (activity, ShowAllActivity.class);
                intent.putExtra("TARGET", UPCOMING_TV);
                activity.startActivity(intent);
            }
        });


        new SetPostersTask(activity, (LinearLayout) activity.findViewById(R.id.popular_movies_layout), "movie").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, POPULAR_MOVIES);
        new SetPostersTask(activity, (LinearLayout) activity.findViewById(R.id.rated_movies_layout), "movie").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, RATED_MOVIES);
        new SetPostersTask(activity, (LinearLayout) activity.findViewById(R.id.now_playing_movies_layout), "movie").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, NOW_PLAYING_MOVIES);
        new SetPostersTask(activity, (LinearLayout) activity.findViewById(R.id.upcoming_movies_layout), "movie").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UPCOMING_MOVIES);
        new SetPostersTask(activity, (LinearLayout) activity.findViewById(R.id.popular_tv_layout), "tv").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, POPULAR_TV);
        new SetPostersTask(activity, (LinearLayout) activity.findViewById(R.id.rated_tv_layout), "tv").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, RATED_TV);
        new SetPostersTask(activity, (LinearLayout) activity.findViewById(R.id.upcoming_tv_layout), "tv").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UPCOMING_TV);

        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected())
            Toast.makeText(activity, "Nessuna connessione di rete", Toast.LENGTH_LONG).show();

    }

}
