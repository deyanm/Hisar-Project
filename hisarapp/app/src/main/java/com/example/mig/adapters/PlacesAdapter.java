package com.example.mig.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mig.R;
import com.example.mig.databinding.ItemPlaceBinding;
import com.example.mig.model.Poi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesRecyclerViewHolder> implements Filterable {

    private List<Poi> pois;
    private List<Poi> poisListFiltered;
    private Context mContext;
    private ItemPlaceBinding binding;
    private PlaceClickListener mPlaceClickListener;
    private ValueFilter valueFilter;

    public PlacesAdapter(Context mContext, List<Poi> pois, PlaceClickListener placeClickListener) {
        this.mContext = mContext;
        this.pois = pois;
        this.poisListFiltered = pois;
        this.mPlaceClickListener = placeClickListener;
    }

    @NonNull
    @Override
    public PlacesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = ItemPlaceBinding.inflate(inflater, parent, false);
        return new PlacesRecyclerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesRecyclerViewHolder holder, int position) {
        Poi poi = pois.get(position);
        holder.binding.placeNameTv.setText(poi.getName());
        if (poi.getDistanceKm() > 0) {
            holder.binding.placeTypeAwayTv.setText(Html.fromHtml(poi.getType() + " &#xb7; " + String.format(Locale.US, "%.2f", poi.getDistanceKm()) + mContext.getString(R.string.away)));

        } else {
            holder.binding.placeTypeAwayTv.setText(poi.getType());
        }

        if (poi.getImages() != null && poi.getImages().get(0) != null) {
            int id = mContext.getResources().getIdentifier(poi.getImages().get(0), "drawable", mContext.getPackageName());
            Glide.with(mContext).load(id).centerCrop().into(holder.binding.placeImage);
        }
        holder.binding.addToFav.setImageResource(poi.isFav() ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_baseline_favorite_border_24);
        holder.binding.addToFav.setOnClickListener(v -> {
            mPlaceClickListener.onFavClick(poi, position, !poi.isFav());
        });
        holder.itemView.setOnClickListener(v -> mPlaceClickListener.onPlaceClick(poi));
    }

    @Override
    public int getItemCount() {
        return pois.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Poi> filterList = new ArrayList<>();
                for (int i = 0; i < poisListFiltered.size(); i++) {
                    if ((poisListFiltered.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(poisListFiltered.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = poisListFiltered.size();
                results.values = poisListFiltered;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            pois = (List<Poi>) results.values;
            notifyDataSetChanged();
        }

    }

    static class PlacesRecyclerViewHolder extends RecyclerView.ViewHolder {

        private ItemPlaceBinding binding;

        public PlacesRecyclerViewHolder(ItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface PlaceClickListener {
        void onPlaceClick(Poi poi);

        void onFavClick(Poi poi, int position, boolean isFav);
    }
}