package minskfood.by.foodapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import io.realm.Realm;
import io.realm.internal.IOException;
import minskfood.by.foodapp.PlacesAdapter;
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
        DetailsFragment.OnFragmentInteractionListener,
        PlacesAdapter.OnPlaceClickListener {

    private static final String URL_PLACES = "http://krabsburger.mycloud.by/places";
    private static final String CURRENT_POSITION = "CURRENT_POSITION";
    private static final int REVIEW_ACTIVITY_INDEX = 0;

    private boolean dualPane;
    private String currentCheckPosition;

    private Menu menu;
    private Realm realm;
    private View currentView;

    // todo: try load data on the first start after installation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            currentCheckPosition = savedInstanceState.getString(CURRENT_POSITION, null);
        }

        dualPane =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (dualPane) {
            currentView = findViewById(R.id.landscape_container);
            DetailsFragment newFragment = DetailsFragment.newInstance(currentCheckPosition);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details, newFragment)
                    .commit();
        } else {
            currentView = findViewById(R.id.portrait_container);
            TitlesFragment firstFragment = new TitlesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.portrait_container, firstFragment)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_POSITION, currentCheckPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.search, menu);
        initSearch(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REVIEW_ACTIVITY_INDEX && resultCode == android.app.Activity.RESULT_OK) {
            addReview(data);
        }
    }

    /********* TitlesFragment method implementations  ********/

    @Override
    public void onSwipeRefreshInteraction() {
        new PlacesRequestAsync(MainActivity.this).execute(MainActivity.URL_PLACES);
    }

    @Override
    public List<Place> composeAdapter() {
        showMenuGroup(true);

        List<Place> places = null;
        try {
            places = realm.where(Place.class).findAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // todo: check with null array on server and fix if needed
        if (places != null && !places.isEmpty()) {
            currentCheckPosition = places.get(0).getId();
        }
        return places;
    }

    /********* DetailsFragment method implementations  ********/

    @Override
    public void onCreateReviewInteraction() {
        if (currentCheckPosition != null) {
            Intent intent = new Intent(MainActivity.this, ReviewActivity.class);
            startActivityForResult(intent, REVIEW_ACTIVITY_INDEX);
        } else {
            Snackbar.make(currentView, R.string.place_not_selected, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public Place getPlaceById(String id) {
        Place place = null;
        try {
            place = realm.where(Place.class).equalTo("_id", id).findFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return place;
    }

    /********* Other method implementations  ********/

    @Override
    public void onSoapPostExecute(Review response) {
        if (response != null) {
            realm.executeTransaction(realm1 -> {
                Place place = realm1.where(Place.class)
                        .equalTo("_id", currentCheckPosition)
                        .findFirst();
                place.addReview(response);
            });

            DetailsFragment fragment;
            if (dualPane) {
                fragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.details);
            } else {
                fragment = (DetailsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.portrait_container);
            }
            if (fragment != null) fragment.updateListView();

            Snackbar.make(currentView, R.string.review_create, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(currentView, R.string.review_create_failed, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRestPostExecute(String response) {
        if (response != null) {
            realm.executeTransaction(realm1 -> realm1.deleteAll());
            realm.executeTransaction(realm12 -> realm12
                    .createOrUpdateAllFromJson(Place.class, response));

            Snackbar.make(currentView, R.string.update, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(currentView, R.string.update_failed, Snackbar.LENGTH_SHORT).show();
        }

        TitlesFragment fragment;
        if (dualPane) {
            fragment = (TitlesFragment) getSupportFragmentManager().findFragmentById(R.id.titles);
        } else {
            fragment = (TitlesFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.portrait_container);
        }
        if (fragment != null) {
            fragment.updateListView();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void onPlaceClick(Place place) {
        currentCheckPosition = place.getId();
        DetailsFragment fragment;
        if (dualPane) {
            // Check what fragment is currently shown, replace if needed.
            fragment = (DetailsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.details);
            if (fragment == null || !fragment.getShownIndex().equals(currentCheckPosition)) {
                fragment = DetailsFragment.newInstance(currentCheckPosition);
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.details, fragment)
                        .commit();
            }
        } else {
            fragment = DetailsFragment.newInstance(currentCheckPosition);
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.portrait_container, fragment)
                    .commit();
            showMenuGroup(false);
        }
    }

    /********* Helper Methods  ********/

    private void addReview(Intent data) {
        String id = currentCheckPosition;
        String author = data.getStringExtra(ReviewActivity.EXTRA_AUTHOR);
        String text = data.getStringExtra(ReviewActivity.EXTRA_TEXT);

        if (author.equals("") || text.equals("")) {
            Snackbar.make(currentView, R.string.review_create_failed, Snackbar.LENGTH_LONG).show();
            return;
        }

        new SoapRequestAsync(MainActivity.this).execute(id, author, text);
    }

    private void initSearch(Menu menu) {
        MenuItem myActionMenuItem = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        // Performs search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchText) {
                searchAndUpdateTitles(searchText);
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onQueryTextChange(String searchText) {
                searchAndUpdateTitles(searchText);
                return false;
            }

            private void searchAndUpdateTitles(String searchText) {
                realm.executeTransaction(realm1 -> {
                    Fragment fragment;
                    if (dualPane) {
                        fragment = getSupportFragmentManager().findFragmentById(R.id.titles);
                    } else {
                        fragment = getSupportFragmentManager()
                                .findFragmentById(R.id.portrait_container);
                    }
                    if (fragment == null || !(fragment instanceof TitlesFragment))
                        return;

                    List<Place> places =
                            realm1.where(Place.class)
                                    .contains("name", searchText)
                                    .or()
                                    .contains("tags.tag", searchText)
                                    .findAll();

                    ((TitlesFragment) fragment).createNewAdapter(places);
                });
            }
        });
    }

    public void showMenuGroup(boolean show) {
        if (menu == null) return;
        menu.setGroupVisible(R.id.main_menu_group, show);
        // shows home button on the action bar when only details fragment visible
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!show);
            getSupportActionBar().setHomeButtonEnabled(!show);
        }
    }
}
