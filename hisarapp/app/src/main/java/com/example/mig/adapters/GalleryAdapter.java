package com.example.mig.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mig.databinding.GalleryItemBinding;
import com.example.mig.model.ImageModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> implements Filterable {

    private Context context;
    ItemListener mListener;

    private List<ImageModel> modelList;
    private List<ImageModel> mStringFilterList;
    private ValueFilter valueFilter;
    private GalleryItemBinding binding;

    public GalleryAdapter(Context context, ItemListener itemListener, List<ImageModel> modelList) {
        this.context = context;
        this.mListener = itemListener;
        this.modelList = modelList;
        mStringFilterList = modelList;
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {

        GalleryItemBinding binding;

        public GalleryViewHolder(GalleryItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
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
        if (model.getImageUrl() != null) {
            int id = context.getResources().getIdentifier(model.getImageUrl(), "drawable", context.getPackageName());
            Picasso.get().load(id).fit().centerCrop().into(holder.binding.galleryImage);
        }
        holder.itemView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
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

//            if (constraint != null && constraint.length() > 0) {
//                List<ImageModel> filterList = new ArrayList<>();
//
//
//                for (int i = 0; i < mStringFilterList.size(); i++) {
//                    if (!mStringFilterList.get(i).isCuisine) {
//
//                        if (mStringFilterList.get(i).cuisines.contains(constraint)) {
//                            ImageModel model = new ImageModel(mStringFilterList.get(i).id, mStringFilterList.get(i).name, mStringFilterList.get(i).cuisines, false, -1);
//                            filterList.add(model);
//                        }
//                    }
//                }
//
//
//                if (filterList.size() == 0) {
//
//                    for (int i = 0; i < mStringFilterList.size(); i++) {
//                        if ((mStringFilterList.get(i).name.toUpperCase())
//                                .contains(constraint.toString().toUpperCase()) && !mStringFilterList.get(i).isCuisine) {
//
//                            ImageModel model = new ImageModel(mStringFilterList.get(i).id, mStringFilterList.get(i).name, mStringFilterList.get(i).cuisines, false, -1);
//                            if (!model.isCuisine)
//                                filterList.add(model);
//                        }
//                    }
//                }
//
//
//                results.count = filterList.size();
//                results.values = filterList;
//            } else {
//
//                results.count = mStringFilterList.size();
//                results.values = mStringFilterList;
//            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            modelList = (List<ImageModel>) results.values;

            notifyDataSetChanged();
        }
    }

    public interface ItemListener {
        void onItemClick(ImageModel model);
    }
}
