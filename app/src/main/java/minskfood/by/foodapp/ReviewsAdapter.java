package minskfood.by.foodapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import minskfood.by.foodapp.models.place.Review;


/**
 * Adapter for recycler_reviews in fragment_details
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private List<Review> reviews;

    public ReviewsAdapter(List<Review> items) {
        reviews = items;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        holder.review = reviews.get(position);
        holder.authorView.setText(reviews.get(position).getAuthor());
        holder.textView.setText(reviews.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @SuppressWarnings("WeakerAccess")
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View reviewView;
        public final TextView authorView;
        public final TextView textView;
        public Review review;

        ViewHolder(View reviewView) {
            super(reviewView);
            this.reviewView = reviewView;
            authorView = (TextView) reviewView.findViewById(R.id.text_author);
            textView = (TextView) reviewView.findViewById(R.id.text_review);
        }
    }
}
