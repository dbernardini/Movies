package com.bernardini.danilo.movies.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.adapters.MovieShowtimesAdapter;
import com.bernardini.danilo.movies.asyncTasks.SearchTask;
import com.bernardini.danilo.movies.entries.Entry;
import com.bernardini.danilo.movies.entries.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class ShowtimesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = "ShowtimesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtimes);

        final ListView moviesListView = (ListView) findViewById(R.id.movies);

        final ProgressDialog progress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progress.setMessage("Loading...");
        progress.setIndeterminate(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("movies")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progress.dismiss();
                        List<Movie> movies = new LinkedList();
                        for (DataSnapshot pictureSnap : dataSnapshot.getChildren()) {
                            Movie movie = pictureSnap.getValue(Movie.class);
                            movies.add(movie);
                        }
                        MovieShowtimesAdapter adapter = new MovieShowtimesAdapter(
                                ShowtimesActivity.this, ShowtimesActivity.this, movies);
                        moviesListView.setAdapter(adapter);
                        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Movie movie = (Movie)parent.getItemAtPosition(position);
                                Intent intent = new Intent(ShowtimesActivity.this, MovieActivity.class);
                                intent.putExtra("MOVIE_ID", movie.getId());
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.toString());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent (this, DisplayResultsActivity.class);
        String searchString = query.replace(' ', '+');
        intent.putExtra("SEARCH_STRING", searchString);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


}
