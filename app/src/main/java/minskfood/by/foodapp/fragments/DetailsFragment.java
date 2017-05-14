package minskfood.by.foodapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import minskfood.by.foodapp.R;
import minskfood.by.foodapp.ReviewsAdapter;
import minskfood.by.foodapp.models.place.Place;


public class DetailsFragment extends Fragment {
    private static final String PLACE_ID = "index";

    @BindView(R.id.button_new_review) Button newPostView;
    @BindView(R.id.recycler_reviews) RecyclerView recyclerView;

    @BindView(R.id.text_name) TextView nameView;
    @BindView(R.id.text_type) TextView typeView;
    @BindView(R.id.text_prices) TextView pricesView;
    @BindView(R.id.text_district) TextView districtView;
    @BindView(R.id.text_address) TextView addressView;
    @BindView(R.id.text_worktime) TextView worktimeView;
    @BindView(R.id.text_tags) TextView tagsView;
    @BindView(R.id.text_description) TextView descriptionView;

    private OnFragmentInteractionListener listener;


    public static DetailsFragment newInstance(String index) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(PLACE_ID, index);
        fragment.setArguments(args);
        return fragment;
    }

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
        if (container == null) return null;

        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Place place = listener.getPlaceById(getArguments().getString(PLACE_ID));

            if (place != null) {
                nameView.setText(place.getName());
                typeView.setText(place.getType());
                pricesView.setText(place.getPrices());
                districtView.setText(place.getLocation().getDistrict());
                addressView.setText(place.getLocation().getAddress());
                worktimeView.setText(place.getWorkTime().getTime());
                descriptionView.setText(place.getDescription());
                if (!place.getTags().equals("")){
                    tagsView.setText(place.getTags());
                } else {
                    tagsView.setVisibility(View.GONE);
                }

                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                        recyclerView.getContext(), mLayoutManager.getOrientation());
                recyclerView.addItemDecoration(mDividerItemDecoration);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(false); // Smooth scrolling in ScrollView
                recyclerView.setAdapter(new ReviewsAdapter(place.getReviews()));
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @OnClick(R.id.button_new_review)
    public void onCreateReviewClick() {
        if (listener != null) listener.onCreateReviewInteraction();
    }

    public String getShownIndex() {
        if (getArguments() != null) {
            return getArguments().getString(PLACE_ID, "0");
        } else {
            return "0";
        }
    }

    public void updateListView() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        void onCreateReviewInteraction();

        Place getPlaceById(String id);
    }
}
