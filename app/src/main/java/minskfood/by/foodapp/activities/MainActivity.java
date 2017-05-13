package minskfood.by.foodapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.internal.IOException;
import minskfood.by.foodapp.PlacesRequestAsync;
import minskfood.by.foodapp.R;
import minskfood.by.foodapp.SoapRequestAsync;
import minskfood.by.foodapp.fragments.DetailsFragment;
import minskfood.by.foodapp.fragments.TitlesFragment;
import minskfood.by.foodapp.models.place.Place;
import minskfood.by.foodapp.models.place.Review;


public class MainActivity extends AppCompatActivity
        implements SoapRequestAsync.OnPostExecuteListener,
        PlacesRequestAsync.OnPostExecuteListener,
        TitlesFragment.OnFragmentInteractionListener,
        DetailsFragment.OnFragmentInteractionListener {

    private static final String URL_PLACES = "http://env-2955146.mycloud.by/places";
    private static final String CURRENT_POSITION = "CURRENT_POSITION";
    private static final int REVIEW_ACTIVITY_INDEX = 0;
    private String currentCheckPosition;
    private boolean dualPane;
    private Realm realm;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            currentCheckPosition = savedInstanceState.getString(CURRENT_POSITION, "null");
        }

        dualPane =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (findViewById(R.id.fragment_container) != null) {
            TitlesFragment firstFragment = new TitlesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, firstFragment)
                    .commit();
        } else {
            DetailsFragment newFragment = DetailsFragment.newInstance(currentCheckPosition);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details, newFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        // Performs search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchAndUpdateTitles(s);
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onQueryTextChange(String s) {
                searchAndUpdateTitles(s);
                return false;
            }

            private void searchAndUpdateTitles(String s) {
                realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> {
                    List<Place> places =
                            realm1.where(Place.class)
                                    .contains("name", s)
                                    .or()
                                    .contains("tags.tag", s)
                                    .findAll();

                    if (dualPane) {
                        TitlesFragment titlesFragment = (TitlesFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.titles);
                        if (titlesFragment != null) {
                            titlesFragment.createNewAdapter(places);
                        }
                    } else {
                        TitlesFragment titlesFragment = (TitlesFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.fragment_container);
                        if (titlesFragment != null) {
                            titlesFragment.createNewAdapter(places);
                        }
                    }
                });
            }
        });

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_POSITION, currentCheckPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) realm.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REVIEW_ACTIVITY_INDEX && resultCode == android.app.Activity.RESULT_OK) {
            addReview(data);
        }
    }

    /********* TitlesFragment method implementations  ********/

    @Override
    public void onTitleInteraction(String index) {
        currentCheckPosition = index;
        if (dualPane) {
            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.details);
            if (details == null || !details.getShownIndex().equals(index)) {
                details = DetailsFragment.newInstance(index);
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.details, details)
                        .commit();
            }
        } else {
            DetailsFragment newFragment = DetailsFragment.newInstance(index);
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, newFragment)
                    .commit();
            showMenuGroup(false);
        }
    }

    @Override
    public void onSwipeRefreshInteraction() {
        new PlacesRequestAsync(this).execute(MainActivity.URL_PLACES);
    }

    @Override
    public List<Place> composeAdapter() {
        showMenuGroup(true);
        realm = Realm.getDefaultInstance();
        List<Place> places = null;
        try {
            places = realm.where(Place.class).findAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // fixme: check with null array on server and fix if statement
        if (places != null) {
            currentCheckPosition = places.get(0).getId();
        }
        return places;
    }

    /********* DetailsFragment method implementations  ********/

    @Override
    public void onCreateReviewInteraction() {
        Intent intent = new Intent(MainActivity.this, ReviewActivity.class);
        startActivityForResult(intent, REVIEW_ACTIVITY_INDEX);
    }

    @Override
    public Place getPlaceById(String id) {
        Realm realm;
        realm = Realm.getDefaultInstance();
        Place place = null;
        try {
            place = realm.where(Place.class).equalTo("_id", id).findFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return place;
    }

    /********* Rest and soap method implementations  ********/

    @Override
    public void onSoapPostExecute(String response) {
        Toast.makeText(this, "soap: " + response, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestPostExecute(String response) {
        boolean isLegal = response != null && !response.equals("No onRestPostExecute received.")
                && !response.equals("Response null");

        if (isLegal) {
            // Get a Realm instance for this thread
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
            realm = Realm.getInstance(realmConfiguration);

            realm.executeTransaction(realm1 -> realm1.deleteAll());
            realm.executeTransaction(realm12 -> realm12
                    .createOrUpdateAllFromJson(Place.class, response));
        }

        if (dualPane) {
            TitlesFragment titlesFragment = (TitlesFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.titles);
            if (titlesFragment != null) {
                titlesFragment.updateListView();
            }
        } else {
            TitlesFragment titlesFragment = (TitlesFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            if (titlesFragment != null) {
                titlesFragment.updateListView();
            }
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /********* Helper Methods  ********/

    public void showMenuGroup(boolean show) {
        if (menu == null) return;
        menu.setGroupVisible(R.id.main_menu_group, show);
    }

    private void addReview(Intent data) {
        String id = currentCheckPosition;
        String author = data.getStringExtra(ReviewActivity.EXTRA_AUTHOR);
        String text = data.getStringExtra(ReviewActivity.EXTRA_TEXT);

        if (author.equals("") || text.equals("")) {
            Toast.makeText(this, R.string.review_creating_fail, Toast.LENGTH_SHORT).show();
            return;
        }

        new SoapRequestAsync(MainActivity.this).execute(id, author, text);

        // TODO: 5/13/2017 add review to local db only if added to server
        realm.executeTransaction(realm1 -> {
            Place place = realm1.where(Place.class).equalTo("_id", id).findFirst();
            place.addReview(new Review(author, text));
        });
    }
}
