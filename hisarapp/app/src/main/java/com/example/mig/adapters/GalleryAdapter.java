package com.example.mig.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mig.R;
import com.example.mig.model.ImageModel;
import com.example.mig.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

//public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> implements Filterable {
//
//    private static final String TAG = GalleryAdapter.class.getSimpleName();
//    private Context context;
//    ItemListener mListener;
//    private GalleryItemBinding binding;
//    private List<ImageModel> modelListFiltered;
//    private List<ImageModel> modelList;
//    private ValueFilter valueFilter;
//
//    public GalleryAdapter(Context context, ItemListener itemListener, List<ImageModel> modelList) {
//        this.context = context;
//        this.mListener = itemListener;
//        this.modelList = modelList;
//        this.modelListFiltered = modelList;
//    }
//
//    public class GalleryViewHolder extends RecyclerView.ViewHolder {
//
//        GalleryItemBinding binding;
//
//        public GalleryViewHolder(GalleryItemBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }
//
//    @NonNull
//    @Override
//    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        binding = GalleryItemBinding.inflate(inflater, parent, false);
//        return new GalleryViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
//        ImageModel model = modelList.get(position);
//        holder.binding.textTv.setText(model.getName());
//        if (model.getImageUrl() != null) {
//            int id = context.getResources().getIdentifier(model.getImageUrl(), "drawable", context.getPackageName());
//            Glide.with(context).load(id).into(holder.binding.galleryImage);
//        }
//        holder.itemView.setOnClickListener(view -> {
//            if (mListener != null) {
//                mListener.onItemClick(model);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        Log.d(TAG, modelList.size() + "");
//        return modelList.size();
//    }
//
//
//    public void renewItems(List<ImageModel> imageModels) {
//        this.modelList = imageModels;
//        this.modelListFiltered = modelList;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public Filter getFilter() {
//        if (valueFilter == null) {
//            valueFilter = new ValueFilter();
//        }
//        return valueFilter;
//    }
//
//    private class ValueFilter extends Filter {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//
//            if (constraint != null && constraint.length() > 0) {
//                List<ImageModel> filterList = new ArrayList<>();
//                for (int i = 0; i < modelListFiltered.size(); i++) {
//                    if ((modelListFiltered.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
//                        filterList.add(modelListFiltered.get(i));
//                    }
//                }
//                results.count = filterList.size();
//                results.values = filterList;
//            } else {
//                results.count = modelListFiltered.size();
//                results.values = modelListFiltered;
//            }
//            return results;
//
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint,
//                                      FilterResults results) {
//            modelList = (List<ImageModel>) results.values;
//            notifyDataSetChanged();
//        }
//
//    }
//
//
//    public interface ItemListener {
//        void onItemClick(ImageModel model);
//    }
//}

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryItemHolder> {
    //Declare GalleryItems List
    private List<ImageModel> galleryItems;
    private List<ImageModel> galleryItemsFiltered;
    Context context;
    //Declare GalleryAdapterCallBacks
    GalleryAdapterCallBacks mAdapterCallBacks;

    public GalleryAdapter(Context context) {
        this.context = context;
        //get GalleryAdapterCallBacks from contex
        this.mAdapterCallBacks = (GalleryAdapterCallBacks) context;
        //Initialize GalleryItem List
        this.galleryItems = new ArrayList<>();
    }

    //This method will take care of adding new Gallery items to RecyclerView
    public void addGalleryItems(List<ImageModel> galleryItems) {
        int previousSize = this.galleryItems.size();
        this.galleryItems.addAll(galleryItems);
        notifyItemRangeInserted(previousSize, galleryItems.size());

    }

    @NonNull
    @Override
    public GalleryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_row_gallery_item, parent, false);
        return new GalleryItemHolder(row);
    }

    @Override
    public void onBindViewHolder(GalleryItemHolder holder, int position) {
        //get current Gallery Item
        ImageModel currentItem = galleryItems.get(position);

        if (currentItem.getImageUrl() != null) {
            if (currentItem.getImageUrl().startsWith("http")) {
                String url = currentItem.getImageUrl().startsWith("http:") ? currentItem.getImageUrl().replace("http", "https") : currentItem.getImageUrl();
                Glide.with(context)
                        .load(Uri.parse(url))
                        .centerCrop()
                        .apply(new RequestOptions().override(ScreenUtils.getScreenWidth(context) / 2, ScreenUtils.getScreenHeight(context) / 3))
                        .into(holder.imageViewThumbnail);
            } else {
                Glide.with(context)
                        .load(Uri.parse("https://raw.githubusercontent.com/deyanm/hisarserver/main/images/" + currentItem.getImageUrl() + ".jpg"))
                        .centerCrop()
                        .apply(new RequestOptions().override(ScreenUtils.getScreenWidth(context) / 2, ScreenUtils.getScreenHeight(context) / 3))
                        .into(holder.imageViewThumbnail);
            }
        }

        //set name of Image
        holder.textViewImageName.setText(currentItem.getName());
        //set on click listener on imageViewThumbnail
        holder.imageViewThumbnail.setOnClickListener(view -> {
            //call onItemSelected method and pass the position and let activity decide what to do when item selected
            mAdapterCallBacks.onItemSelected(position);
        });

    }

    public void renewItems(List<ImageModel> imageModels) {
        this.galleryItems = imageModels;
        this.galleryItemsFiltered = galleryItems;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    public class GalleryItemHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewImageName;

        public GalleryItemHolder(View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewImageName = itemView.findViewById(R.id.textViewImageName);

        }
    }

    //Interface for communication of Adapter and MainActivity
    public interface GalleryAdapterCallBacks {
        //call this method to notify about item is clicked
        void onItemSelected(int position);
    }


}

