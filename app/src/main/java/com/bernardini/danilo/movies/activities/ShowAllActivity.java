package com.bernardini.danilo.movies.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.asyncTasks.MyMoviesTask;
import com.bernardini.danilo.movies.asyncTasks.ShowAllTask;

public class ShowAllActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

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

    private int target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        Intent intent = getIntent();
        target = intent.getIntExtra("TARGET", -1);

        switch(target){
            case POPULAR_MOVIES: {
                setTitle("Film più popolari");
                break;
            }
            case RATED_MOVIES: {
                setTitle("Film più votati");
                break;
            }
            case NOW_PLAYING_MOVIES: {
                setTitle("Film al cinema (USA)");
                break;
            }
            case UPCOMING_MOVIES: {
                setTitle("Film in uscita (USA)");
                break;
            }
            case POPULAR_TV: {
                setTitle("Serie tv più popolari");
                break;
            }
            case RATED_TV: {
                setTitle("Serie tv più votate");
                break;
            }
            case UPCOMING_TV: {
                setTitle("Serie in arrivo (USA)");
                break;
            }
            case SEEN_MOVIES: {
                setTitle("Film visti");
                break;
            }
            case WISH_MOVIES: {
                setTitle("Film da vedere");
                break;
            }
            case OWN_MOVIES: {
                setTitle("Film acquistati");
                break;
            }
            case SEEN_TV: {
                setTitle("Serie tv viste");
                break;
            }
            case WISH_TV: {
                setTitle("Serie tv da vedere");
                break;
            }
            case WATCHING_TV: {
                setTitle("Serie che sto vedendo");
                break;
            }
        }

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected())
            Toast.makeText(getApplicationContext(), "Nessuna connessione di rete disponibile", Toast.LENGTH_LONG).show();
        else {
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new ShowAllTask(ShowAllActivity.this, target, progressDialog, swipeRefreshLayout).execute();
                }
            });
            new ShowAllTask(this, target, progressDialog).execute();
        }
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

    public int getTarget() {
        return target;
    }
}
