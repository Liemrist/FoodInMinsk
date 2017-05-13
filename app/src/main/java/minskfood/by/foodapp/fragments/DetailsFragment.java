package minskfood.by.foodapp.fragments;

import android.content.Context;
import android.os.Bundle;
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
    @BindView(R.id.recycler_reviews) RecyclerView mRecyclerView;
    @BindView(R.id.text_name) TextView nameView;
    @BindView(R.id.tv_type) TextView typeView;
    @BindView(R.id.tv_prices) TextView pricesView;
    @BindView(R.id.tv_district) TextView districtView;
    @BindView(R.id.tv_address) TextView addressView;
    @BindView(R.id.tv_worktime) TextView worktimeView;
    @BindView(R.id.tv_tags) TextView tagsView;
    @BindView(R.id.tv_description) TextView descriptionView;

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

        if (getArguments() != null) {
            Place place = listener.getPlaceById(getArguments().getString(PLACE_ID));

            if (place != null) {
                nameView.setText(place.getName());
                typeView.setText(place.getType());
                pricesView.setText(place.getPrices());
                districtView.setText(place.getLocation().getDistrict());
                addressView.setText(place.getLocation().getAddress());
                worktimeView.setText(place.getWorkTime().getTime());
                tagsView.setText(place.getTags());
                descriptionView.setText(place.getDescription());

                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                        mRecyclerView.getContext(), mLayoutManager.getOrientation());
                mRecyclerView.addItemDecoration(mDividerItemDecoration);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setNestedScrollingEnabled(false); // Smooth scrolling in ScrollView
                mRecyclerView.setAdapter(new ReviewsAdapter(place.getReviews()));
            }
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @OnClick(R.id.button_new_review)
    public void createReview() {
        if (listener != null) listener.onCreateReviewInteraction();
    }

    public String getShownIndex() {
        if (getArguments() != null) {
            return getArguments().getString(PLACE_ID, "0");
        } else {
            return "0";
        }
    }

    public interface OnFragmentInteractionListener {
        void onCreateReviewInteraction();

        Place getPlaceById(String id);
    }
}
