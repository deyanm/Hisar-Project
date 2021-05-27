package com.example.mig.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mig.R;
import com.example.mig.databinding.ItemFavBinding;
import com.example.mig.model.Poi;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavViewHolder> {

    private List<Poi> pois;
    private Context mContext;
    private ItemFavBinding binding;
    private PlacesAdapter.PlaceClickListener mPlaceClickListener;

    public FavouritesAdapter(Context mContext, List<Poi> pois, PlacesAdapter.PlaceClickListener placeClickListener) {
        this.mContext = mContext;
        this.pois = pois;
        this.mPlaceClickListener = placeClickListener;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = ItemFavBinding.inflate(inflater, parent, false);
        return new FavViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        Poi poi = pois.get(position);
        holder.binding.placeNameTv.setText(poi.getName());
        holder.binding.placeTypeAwayTv.setText(poi.getType());

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

    static class FavViewHolder extends RecyclerView.ViewHolder {

        private ItemFavBinding binding;

        public FavViewHolder(ItemFavBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}