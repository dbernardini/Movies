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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bernardini.danilo.movies.R;
import com.bernardini.danilo.movies.asyncTasks.MovieDetailsTask;
import com.bernardini.danilo.movies.asyncTasks.PersonDetailsTask;

public class PersonActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private String personID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        personID = intent.getStringExtra("PERSON_ID");

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected())
            Toast.makeText(getApplicationContext(), "Nessuna connessione di rete disponibile", Toast.LENGTH_LONG).show();
        else {
            ProgressDialog progress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            new PersonDetailsTask(progress).execute(this);
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

    public String getPersonID() {
        return personID;
    }
}
