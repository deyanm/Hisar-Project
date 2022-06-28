package com.example.mig.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mig.R;
import com.example.mig.model.ImageModel;
import com.example.mig.utils.ScreenUtils;
import com.example.mig.utils.SquareLayout;

import java.util.List;

public class GalleryStripAdapter extends RecyclerView.Adapter<GalleryStripAdapter.GalleryStripItemHolder> {
    //Declare list of GalleryItems
    List<ImageModel> galleryItems;
    Context context;
    GalleryStripCallBacks mStripCallBacks;
    ImageModel mCurrentSelected;

    public GalleryStripAdapter(List<ImageModel> galleryItems, Context context, GalleryStripCallBacks StripCallBacks, int CurrentPosition) {
        //set galleryItems
        this.galleryItems = galleryItems;
        this.context = context;
        //set stripcallbacks
        this.mStripCallBacks = StripCallBacks;
        //set current selected
        mCurrentSelected = galleryItems.get(CurrentPosition);
        //set current selected item as selected
        mCurrentSelected.isSelected = true;
    }

    @NonNull
    @Override
    public GalleryStripItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_row_gallery_strip_item, parent, false);
        SquareLayout squareLayout = row.findViewById(R.id.squareLayout);
        return new GalleryStripItemHolder(squareLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryStripItemHolder holder, int position) {
        //get Curent Gallery Item
        ImageModel mCurrentItem = galleryItems.get(holder.getLayoutPosition());
        //get thumb square size 1/6 of screen width
        if (mCurrentItem.getImageUrl() != null) {
            if (mCurrentItem.getImageUrl().startsWith("http")) {
                String url = mCurrentItem.getImageUrl().startsWith("http:") ? mCurrentItem.getImageUrl().replace("http", "https") : mCurrentItem.getImageUrl();
                Glide.with(context)
                        .load(Uri.parse(url))
                        .centerCrop()
                        .apply(new RequestOptions().override(ScreenUtils.getScreenWidth(context) / 6, ScreenUtils.getScreenWidth(context) / 6))
                        .into(holder.imageViewThumbnail);
            } else {
                Glide.with(context)
                        .load(Uri.parse("https://raw.githubusercontent.com/deyanm/hisarserver/main/images/" + mCurrentItem.getImageUrl() + ".jpg"))
                        .centerCrop()
                        .apply(new RequestOptions().override(ScreenUtils.getScreenWidth(context) / 6, ScreenUtils.getScreenWidth(context) / 6))
                        .into(holder.imageViewThumbnail);
            }
        }
        //set current selected
        if (mCurrentItem.isSelected) {
            holder.imageViewThumbnail.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            //value 0 removes any background color
            holder.imageViewThumbnail.setBackgroundColor(0);
        }
        holder.imageViewThumbnail.setOnClickListener(view -> {
            //call onGalleryStripItemSelected on click and pass position
            mStripCallBacks.onGalleryStripItemSelected(holder.getLayoutPosition());
        });
    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    public static class GalleryStripItemHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;

        public GalleryStripItemHolder(View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
        }
    }

    //interface for communication on gallery strip interactions
    public interface GalleryStripCallBacks {
        void onGalleryStripItemSelected(int position);
    }

    //Method to highlight  selected item on gallery strip
    public void setSelected(int position) {
        //remove current selection
        mCurrentSelected.isSelected = false;
        //notify recyclerview that we changed  item to update its view
        notifyItemChanged(galleryItems.indexOf(mCurrentSelected));
        //select gallery item
        galleryItems.get(position).isSelected = true;
        //notify recyclerview that we changed  item to update its view
        notifyItemChanged(position);
        //set current selected
        mCurrentSelected = galleryItems.get(position);

    }

    //method to remove selection
    public void removeSelection() {
        mCurrentSelected.isSelected = false;
    }


}
