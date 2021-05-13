package com.example.mig.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mig.databinding.ItemPlaceBinding;
import com.example.mig.model.Poi;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesRecyclerViewHolder> {
    private List<Poi> pois;
    private Context mContext;
    private ItemPlaceBinding binding;

    public PlacesAdapter(Context mContext, List<Poi> pois) {
        this.mContext = mContext;
        this.pois = pois;
    }

    @NonNull
    @Override
    public PlacesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = ItemPlaceBinding.inflate(inflater, parent, false);
        return new PlacesRecyclerViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull PlacesRecyclerViewHolder holder, int position) {
        Poi poi = pois.get(position);
        holder.binding.placeNameTv.setText(poi.getName());
        holder.binding.placeTypeAwayTv.setText(Html.fromHtml(poi.getType() + " &#xb7; " + "7km away"));

        if (poi.getImages() != null && poi.getImages().get(0) != null) {
            int id = mContext.getResources().getIdentifier(poi.getImages().get(0), "drawable", mContext.getPackageName());
            Picasso.get().load(id).fit().centerCrop().into(holder.binding.placeImage);
        }
//        holder.binding.movieItemRelativeLayout.setOnClickListener((View.OnClickListener) view -> {
//            HomeDirections.ActionHome3ToMovieDetails action = HomeDirections
//                    .actionHome3ToMovieDetails(moviesList.get(position).getId());
//            Navigation.findNavController(view).navigate(action);
//        });
    }

    @Override
    public int getItemCount() {
        return pois == null ? 0 : pois.size();
    }

    static class PlacesRecyclerViewHolder extends RecyclerView.ViewHolder {

        private ItemPlaceBinding binding;

        public PlacesRecyclerViewHolder(ItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
        notifyDataSetChanged();
    }
}