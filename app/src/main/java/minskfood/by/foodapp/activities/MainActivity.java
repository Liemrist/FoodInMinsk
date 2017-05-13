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
        implements PlacesRequestAsync.OnPostExecuteListener,
        TitlesFragment.OnFragmentInteractionListener,
        DetailsFragment.OnFragmentInteractionListener,
        SoapRequestAsync.OnPostExecuteListener {

    public static final String URL_PLACES = "http://env-2955146.mycloud.by/places";
    private static final String CUR_POSITION = "CUR_POSITION";
    private static final int REVIEW_ACTIVITY_INDEX = 0;
    protected String curCheckPosition;
    private boolean dualPane;
    private Realm realm;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);

        setContentView(R.layout.fragment_layout);

        if (savedInstanceState != null) {
            curCheckPosition = savedInstanceState.getString(CUR_POSITION, "null");
        }

        dualPane =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (findViewById(R.id.fragment_container) != null) {
            TitlesFragment firstFragment = new TitlesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, firstFragment)
                    .commit();
        } else {
            DetailsFragment newFragment = DetailsFragment.newInstance(curCheckPosition);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.details, newFragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) realm.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CUR_POSITION, curCheckPosition);
    }

    /**
     * Shows or hides menu group
     *
     * @param showMenu boolean for show menu or not
     */
    public void showOverflowMenu(boolean showMenu) {
        if (menu == null)
            return;
        menu.setGroupVisible(R.id.main_menu_group, showMenu);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REVIEW_ACTIVITY_INDEX:
                if (resultCode == android.app.Activity.RESULT_OK) {
                    addReview(data);
                    break;
                } else {
                    break;
                }
        }
    }

    /**
     * GET request to Places server
     *
     * @param response - onResponse from server (in JSON array)
     */
    @Override
    public void onResponse(String response) {
        boolean isLegal = response != null && !response.equals("No onResponse received.")
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

    @Override
    public void onTitleInteraction(String index) {
        curCheckPosition = index;
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
            showOverflowMenu(false);
        }
    }

    @Override
    public List<Place> composeAdapter() {
        showOverflowMenu(true);
        // todo: use RealmResults and add changeListener (https://github.com/realm/realm-java/issues/2946)
        // also try to use places = realm.where(Place.class).findAllAsync();
        realm = Realm.getDefaultInstance();
        List<Place> places = null;
        try {
            places = realm.where(Place.class).findAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // fixme: check with null array on server and fix if statement
        if (places != null) {
            curCheckPosition = places.get(0).getId();
        }
        return places;
    }

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

    /**
     * SOAP request result handler
     *
     * @param response - onResponse from soap service on Places server
     */
    @Override
    public void onSoapPostExecute(String response) {
        Toast.makeText(this, "onResponse: " + response, Toast.LENGTH_SHORT).show();
    }

    private void addReview(Intent data) {
        String id = curCheckPosition;
        String author = data.getStringExtra(ReviewActivity.EXTRA_REVIEW_AUTHOR);
        String text = data.getStringExtra(ReviewActivity.EXTRA_REVIEW_TEXT);

        if (author.equals("") || text.equals("")) {
            Toast.makeText(this, "Review creating failed", Toast.LENGTH_SHORT).show();
            return;
        }

        new SoapRequestAsync(MainActivity.this).execute(id, author, text);
        
        realm.executeTransaction(realm1 -> {
            Place place = realm1.where(Place.class).equalTo("_id", id).findFirst();
            place.addReview(new Review(author, text));
        });
    }
}
