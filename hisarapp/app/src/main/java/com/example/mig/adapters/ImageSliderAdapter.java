package com.example.mig.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.mig.R;
import com.example.mig.model.SliderItem;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public ImageSliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);

//        int id = context.getResources().getIdentifier(sliderItem.getImageUrl(), "drawable", context.getPackageName());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setTint(ContextCompat.getColor(context, R.color.colorAccent));
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        if (sliderItem.getImageUrl().startsWith("http")) {
            String url = sliderItem.getImageUrl().startsWith("http:") ? sliderItem.getImageUrl().replace("http", "https") : sliderItem.getImageUrl();
            Glide.with(context)
                    .load(Uri.parse(url))
                    .placeholder(circularProgressDrawable)
                    .centerCrop()
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        } else {
            Glide.with(context)
                    .load(Uri.parse("https://raw.githubusercontent.com/deyanm/hisarserver/main/images/" + sliderItem.getImageUrl() + ".jpg"))
                    .placeholder(circularProgressDrawable)
                    .centerCrop()
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        }

//        viewHolder.itemView.setOnClickListener(v -> Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;
        }
    }

}
