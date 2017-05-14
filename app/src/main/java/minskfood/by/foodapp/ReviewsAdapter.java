package minskfood.by.foodapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import minskfood.by.foodapp.models.place.Review;


/**
 * RecyclerView adapter to display a list of {@link Review}.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private List<Review> reviews;


    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.review = reviews.get(position);
        holder.authorView.setText(reviews.get(position).getAuthor());
        holder.textView.setText(reviews.get(position).getText());
    }

    @Override
    public int getItemCount() {
        // FIXME: Fix error handling or update behavior
        // FIXME: Update current details view when updating titles for example.
        int size;
        try {
            size = reviews.size();
        } catch (IllegalStateException e){
            e.printStackTrace();
            return 0;
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Review review;

        @BindView(R.id.text_author) TextView authorView;
        @BindView(R.id.text_review) TextView textView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
