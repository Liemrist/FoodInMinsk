package minskfood.by.foodapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import minskfood.by.foodapp.PlacesAdapter;
import minskfood.by.foodapp.R;
import minskfood.by.foodapp.models.place.Place;


public class TitlesFragment extends Fragment {
    @BindView(R.id.recycler_titles) RecyclerView recyclerView;
    @BindView(R.id.swiperefresh_titles) SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView.Adapter recyclerAdapter;
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
        View view = inflater.inflate(R.layout.fragment_titles, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(() -> listener.onSwipeRefreshInteraction());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Place> places = listener.composeAdapter();

        recyclerAdapter = new PlacesAdapter(places, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void createNewAdapter(List<Place> places) {
        recyclerAdapter = new PlacesAdapter(places, getActivity());
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void updateListView() {
        recyclerAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    public interface OnFragmentInteractionListener {
        void onSwipeRefreshInteraction();

        List<Place> composeAdapter();
    }
}
