package minskfood.by.foodapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import minskfood.by.foodapp.models.place.Place;


/**
 * Adapter for recycler_titles in fragment_titles
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    private List<Place> places;
    private onListFragmentInteraction listener;

    public PlacesAdapter(List<Place> items, onListFragmentInteraction listener) {
        places = items;
        this.listener = listener;
    }

    // Create new views
    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a reviewView
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the reviewView with that element
        holder.place = places.get(position);
        holder.imageView.setImageResource(R.drawable.goof);
        holder.nameView.setText(places.get(position).getName());
        holder.addressView.setText(places.get(position).getLocation().getAddress());
        holder.textView.setText(places.get(position).getTagsString());

        holder.placeView.setOnClickListener(v -> {
            if (null != listener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                listener.onPlaceInteraction(holder.place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public interface onListFragmentInteraction {
        void onPlaceInteraction(Place item);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one reviewView per item, and
    // you provide access to all the views for a data item in a reviewView holder
    @SuppressWarnings("WeakerAccess")
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View placeView;
        public final TextView addressView;
        public final TextView nameView;
        public final TextView textView;
        public final ImageView imageView;
        public Place place;

        public ViewHolder(View placeView) {
            super(placeView);
            this.placeView = placeView;
            addressView = (TextView) placeView.findViewById(R.id.text_address);
            nameView = (TextView) placeView.findViewById(R.id.text_name);
            textView = (TextView) placeView.findViewById(R.id.text_tags);
            imageView = (ImageView) placeView.findViewById(R.id.image_place);
        }
    }
}
