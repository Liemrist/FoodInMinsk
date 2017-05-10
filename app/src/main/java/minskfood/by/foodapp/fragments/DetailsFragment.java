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

import minskfood.by.foodapp.R;
import minskfood.by.foodapp.ReviewsAdapter;
import minskfood.by.foodapp.models.place.Place;


public class DetailsFragment extends Fragment {
    private OnFragmentInteractionListener listener;

    public static DetailsFragment newInstance(String index) {
        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putString("index", index);
        fragment.setArguments(args);

        return fragment;
    }

    // Initialization goes in onCreate method (gay says it should be in fragment lifecycle)

    public String getShownIndex() {
        if (getArguments() != null) {
            return getArguments().getString("index", "defaultValue");
        } else {
            return "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        Button newPostView = (Button) view.findViewById(R.id.button_new_review);
        newPostView.setOnClickListener(v -> onButtonPressed());

        if (getArguments() != null) {
            String index = getArguments().getString("index");

            Place place = listener.getPlaceById(index);

            // Initializes all components
            if (place != null) {
                TextView nameView = (TextView) view.findViewById(R.id.text_name);
                TextView typeView = (TextView) view.findViewById(R.id.tv_type);
                TextView pricesView = (TextView) view.findViewById(R.id.tv_prices);
                TextView districtView = (TextView) view.findViewById(R.id.tv_district);
                TextView addressView = (TextView) view.findViewById(R.id.tv_address);
                TextView worktimeView = (TextView) view.findViewById(R.id.tv_worktime);
                TextView tagsView = (TextView) view.findViewById(R.id.tv_tags);
                TextView descriptionView = (TextView) view.findViewById(R.id.tv_description);
                RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_reviews);

                nameView.setText(place.getName());
                typeView.setText(place.getType());
                pricesView.setText(place.getPrices());
                districtView.setText(place.getLocation().getDistrict());
                addressView.setText(place.getLocation().getAddress());
                worktimeView.setText(place.getWorkTime().getTimeString());
                tagsView.setText(place.getTagsString());
                descriptionView.setText(place.getDescription());

                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                        mRecyclerView.getContext(), mLayoutManager.getOrientation());

                mRecyclerView.addItemDecoration(mDividerItemDecoration);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(new ReviewsAdapter(place.getReviews()));
            }
        }

        return view;
    }

    public void onButtonPressed() {
        if (listener != null) {
            listener.onCreateReviewInteraction();
        }
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
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onCreateReviewInteraction();

        Place getPlaceById(String id);
    }
}
