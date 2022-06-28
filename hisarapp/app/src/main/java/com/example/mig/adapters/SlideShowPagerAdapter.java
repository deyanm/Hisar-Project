package com.example.mig.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mig.R;
import com.example.mig.model.ImageModel;

import java.util.List;


public class SlideShowPagerAdapter extends PagerAdapter {

    Context mContext;
    //Layout inflater
    LayoutInflater mLayoutInflater;
    //list of Gallery Items
    List<ImageModel> galleryItems;

    public SlideShowPagerAdapter(Context context, List<ImageModel> galleryItems) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //set galleryItems
        this.galleryItems = galleryItems;
    }

    @Override
    public int getCount() {
        return galleryItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageViewThumbnail);

        ImageModel imageModel = galleryItems.get(position);

        if (imageModel.getImageUrl() != null) {
            if (imageModel.getImageUrl().startsWith("http")) {
                String url = imageModel.getImageUrl().startsWith("http:") ? imageModel.getImageUrl().replace("http", "https") : imageModel.getImageUrl();
                Glide.with(mContext)
                        .load(Uri.parse(url))
                        .fitCenter()
                        .into(imageView);
            } else {
                Glide.with(mContext)
                        .load(Uri.parse("https://raw.githubusercontent.com/deyanm/hisarserver/main/images/" + imageModel.getImageUrl() + ".jpg"))
                        .fitCenter()
                        .into(imageView);
            }
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

}