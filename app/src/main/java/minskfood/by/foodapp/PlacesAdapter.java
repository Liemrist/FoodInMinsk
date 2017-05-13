package minskfood.by.foodapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import minskfood.by.foodapp.models.place.Place;


/**
 * RecyclerView adapter to display a list of {@link Place}.
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private List<Place> places;
    private OnPlaceClickListener listener;

    public PlacesAdapter(List<Place> places, OnPlaceClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false));
    }

    // Replaces the contents of the reviewView with the element from dataset at the position.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.place = places.get(position);
        holder.addressView.setText(places.get(position).getLocation().getAddress());
        holder.nameView.setText(places.get(position).getName());
        holder.tagsView.setText(places.get(position).getTagsString());
        switch (holder.place.getType()) {
            case "Restaurant":
                holder.imageView.setImageResource(R.drawable.goof);
                break;
            case "Cafe":
                holder.imageView.setImageResource(R.drawable.coffee);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.cafe);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaceClick(holder.place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    /**
     * Provides access to all views for each data item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public Place place;

        @BindView(R.id.image_place) ImageView imageView;
        @BindView(R.id.text_address) TextView addressView;
        @BindView(R.id.text_name) TextView nameView;
        @BindView(R.id.text_tags) TextView tagsView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
