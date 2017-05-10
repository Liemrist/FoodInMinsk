package minskfood.by.foodapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import minskfood.by.foodapp.PlacesAdapter;
import minskfood.by.foodapp.PlacesRequestAsync;
import minskfood.by.foodapp.R;
import minskfood.by.foodapp.activities.MainActivity;
import minskfood.by.foodapp.models.place.Place;


public class TitlesFragment extends Fragment implements PlacesAdapter.onListFragmentInteraction {
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private OnFragmentInteractionListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        List<Place> places = listener.composeAdapter();

        adapter = new PlacesAdapter(places, TitlesFragment.this);

        View view = inflater.inflate(R.layout.fragment_titles, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_titles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_titles);
        mySwipeRefreshLayout.setOnRefreshListener(() ->
                new PlacesRequestAsync(getActivity()).execute(MainActivity.URL_PLACES));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onPlaceInteraction(Place item) {
        listener.onTitleInteraction(item.getId());
    }

    public void createNewAdapter(List<Place> places) {
        adapter = new PlacesAdapter(places, TitlesFragment.this);
        recyclerView.setAdapter(adapter);
    }

    public void updateListView() {
        adapter.notifyDataSetChanged();
        mySwipeRefreshLayout.setRefreshing(false);
    }

    public interface OnFragmentInteractionListener {
        void onTitleInteraction(String index);

        List<Place> composeAdapter();
    }
}
