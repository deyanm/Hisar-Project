package com.example.mig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mig.databinding.GalleryItemBinding;
import com.example.mig.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> implements Filterable {

    private static final String TAG = GalleryAdapter.class.getSimpleName();
    private Context context;
    ItemListener mListener;
    private GalleryItemBinding binding;
    private List<ImageModel> modelListFiltered;
    private List<ImageModel> modelList;
    private ValueFilter valueFilter;

    public GalleryAdapter(Context context, ItemListener itemListener, List<ImageModel> modelList) {
        this.context = context;
        this.mListener = itemListener;
        this.modelList = modelList;
        this.modelListFiltered = modelList;
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {

        GalleryItemBinding binding;

        public GalleryViewHolder(GalleryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = GalleryItemBinding.inflate(inflater, parent, false);
        return new GalleryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        ImageModel model = modelList.get(position);
        holder.binding.textTv.setText(model.getName());
        if (model.getImageUrl() != null) {
            int id = context.getResources().getIdentifier(model.getImageUrl(), "drawable", context.getPackageName());
            Glide.with(context).load(id).into(holder.binding.galleryImage);
        }
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, modelList.size() + "");
        return modelList.size();
    }


    public void renewItems(List<ImageModel> imageModels) {
        this.modelList = imageModels;
        this.modelListFiltered = modelList;
        notifyDataSetChanged();
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
                List<ImageModel> filterList = new ArrayList<>();
                for (int i = 0; i < modelListFiltered.size(); i++) {
                    if ((modelListFiltered.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(modelListFiltered.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = modelListFiltered.size();
                results.values = modelListFiltered;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            modelList = (List<ImageModel>) results.values;
            notifyDataSetChanged();
        }

    }


    public interface ItemListener {
        void onItemClick(ImageModel model);
    }
}
