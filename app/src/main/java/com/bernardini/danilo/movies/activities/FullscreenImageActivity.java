package com.bernardini.danilo.movies.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.asyncTasks.ImageTask;
import com.bernardini.danilo.movies.asyncTasks.MovieDetailsTask;
import com.bernardini.danilo.movies.asyncTasks.PersonDetailsTask;
import com.bernardini.danilo.movies.asyncTasks.TvDetailsTask;
import com.squareup.picasso.Picasso;

public class FullscreenImageActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));


        getSupportActionBar().hide();


        setContentView(R.layout.activity_fullscreen_image);

        Intent intent = getIntent();
        String path = intent.getStringExtra(MovieDetailsTask.POSTER_PATH);
        if (path == null)
            path = intent.getStringExtra(PersonDetailsTask.PROFILE_PATH);
        if (path == null)
            path = intent.getStringExtra(TvDetailsTask.TV_PATH);

        ImageView imageView = (ImageView) findViewById(R.id.fullscreen_image);

        ProgressDialog progress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        new ImageTask(this, imageView, progress).execute(path, "original");
    }

}
