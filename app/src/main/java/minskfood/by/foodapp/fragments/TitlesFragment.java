package minskfood.by.foodapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.realm.Realm;
import io.realm.internal.IOException;
import minskfood.by.foodapp.PlacesAdapter;
import minskfood.by.foodapp.PlacesRequestAsync;
import minskfood.by.foodapp.R;
import minskfood.by.foodapp.activities.DetailsActivity;
import minskfood.by.foodapp.activities.MainActivity;
import minskfood.by.foodapp.models.place.Place;


public class TitlesFragment extends Fragment implements PlacesAdapter.onListFragmentInteraction {
    protected String mCurCheckPosition = "0";
    protected boolean mDualPane;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private Realm realm;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("curChoice", mCurCheckPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_titles, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_titles);
        // A layout manager positions item views inside a RecyclerView and determines when
        // to reuse item views that are no longer visible to the user.
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        // todo: use RealmResults and add changeListener (https://github.com/realm/realm-java/issues/2946)
        // also try to use places = realm.where(Place.class).findAllAsync();
        realm = Realm.getDefaultInstance();
        List<Place> places = null;
        try {
            places = realm.where(Place.class).findAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (places != null && places.size() != 0) {
            mCurCheckPosition = places.get(0).getId();
        }
        adapter = new PlacesAdapter(places, TitlesFragment.this);
        recyclerView.setAdapter(adapter);

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_titles);
        mySwipeRefreshLayout.setOnRefreshListener(() -> {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            new PlacesRequestAsync(getActivity()).execute(MainActivity.URL_PLACES);
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getString("curChoice", "0");
        }

        if (mDualPane) {
            // In dual-pane mode, the list reviewView highlights the selected item.
            //getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null) realm.close();
    }

    @Override
    public void onPlaceInteraction(Place item) {
        showDetails(item.getId());
    }

    public void createNewAdapter(List<Place> places) {
        adapter = new PlacesAdapter(places, TitlesFragment.this);
        recyclerView.setAdapter(adapter);
    }

    public void updateListView() {
        adapter.notifyDataSetChanged();
        // To signal refresh has finished
        mySwipeRefreshLayout.setRefreshing(false);
    }

    protected void showDetails(String index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            //getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment) getFragmentManager()
                    .findFragmentById(R.id.details);
            if (details == null || !details.getShownIndex().equals(index)) {
                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Make new fragment to show this selection.
                details = DetailsFragment.newInstance(index);
//                transaction.add(R.id.details, details);
                transaction.replace(R.id.details, details);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
}
