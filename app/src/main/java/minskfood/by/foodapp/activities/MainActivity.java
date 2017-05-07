package minskfood.by.foodapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import minskfood.by.foodapp.GetPlaces;
import minskfood.by.foodapp.R;
import minskfood.by.foodapp.fragments.TitlesFragment;
import minskfood.by.foodapp.models.place.Place;


public class MainActivity extends AppCompatActivity implements GetPlaces.OnPostExecuteListener {
    public static final String URL_PLACES = "http://env-2955146.mycloud.by/places";

    private Realm realm;

    @Override
    public void response(String response) {
        if (response != null && !response.equals("No response received.")
                && !response.equals("Response null")) {
            // Get a Realm instance for this thread
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
            realm = Realm.getInstance(realmConfiguration);

            realm.executeTransaction(realm1 -> realm1.deleteAll());
            realm.executeTransaction(realm12 -> realm12
                    .createOrUpdateAllFromJson(Place.class, response));
        }

        TitlesFragment titlesFragment = (TitlesFragment) getSupportFragmentManager()
                .findFragmentById(R.id.titles);
        if (titlesFragment != null) {
            titlesFragment.updateListView();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
        setContentView(R.layout.fragment_layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: 5/7/2017 handle search
                Toast.makeText(MainActivity.this, "submit", Toast.LENGTH_SHORT).show();

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Toast.makeText(MainActivity.this, "changed text", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return true;
    }
}
