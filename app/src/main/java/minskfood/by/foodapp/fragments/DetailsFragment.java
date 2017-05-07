package minskfood.by.foodapp.fragments;

import android.content.Intent;
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
import android.widget.Toast;

import io.realm.Realm;
import io.realm.internal.IOException;
import minskfood.by.foodapp.R;
import minskfood.by.foodapp.ReviewsAdapter;
import minskfood.by.foodapp.activities.ReviewActivity;
import minskfood.by.foodapp.models.place.Place;


public class DetailsFragment extends Fragment {
    private static final int REVIEW_ACTIVITY_INDEX = 0;

    /**
     * Creates a new instance of DetailsFragment, initialized to show the text at 'index'.
     */
    public static DetailsFragment newInstance(String index) {
        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putString("index", index);
        fragment.setArguments(args);

        return fragment;
    }

    public String getShownIndex() {
        return getArguments().getString("index", "defaultValue");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        Button newPostView = (Button) view.findViewById(R.id.button_new_review);
        newPostView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ReviewActivity.class);
            startActivityForResult(intent, REVIEW_ACTIVITY_INDEX);
        });

        if (getArguments() != null) {
            String index = getArguments().getString("index");

            Realm realm;
            realm = Realm.getDefaultInstance();
            Place place = null;

            try {
                place = realm.where(Place.class).equalTo("_id", index).findFirst();
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REVIEW_ACTIVITY_INDEX:
                if (resultCode == android.app.Activity.RESULT_OK) {
                    String result = data.getStringExtra(ReviewActivity.EXTRA_REVIEW_DATA);
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    break;
                }
        }
    }
}
