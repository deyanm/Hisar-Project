package com.example.mig.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.mig.R;
import com.example.mig.adapters.ImageSliderAdapter;
import com.example.mig.databinding.ActivityInfoBinding;
import com.example.mig.model.SliderItem;
import com.example.mig.viewmodel.InfoViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InfoActivity extends AppCompatActivity {

    ActivityInfoBinding binding;
    InfoViewModel viewModel;
    private SliderView sliderView;
    private ImageSliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(InfoViewModel.class);
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel.getCurrentPlace();
        viewModel.getPlace().observe(this, place -> {
            if (place != null) {
                binding.placeNameTv.setText(place.getInfo().getName());
                binding.locTv.setText(place.getInfo().getAddress());
                binding.fullDescTv.setText(place.getInfo().getFullDescription());

                if (place.getInfo().getImages() != null && place.getInfo().getImages().size() > 0) {
                    addItems(place.getInfo().getImages());

                    for (String image : place.getInfo().getImages()) {
                        ImageView imageView = new ImageView(this);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        int id = this.getResources().getIdentifier(image, "drawable", this.getPackageName());
                        imageView.setImageResource(id);
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setLayoutParams(lp);
                        binding.imagesLayout.addView(imageView);
                    }
                }
            }
        });

        sliderView = findViewById(R.id.imageSlider);

        adapter = new ImageSliderAdapter(this);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(5);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();


        sliderView.setOnIndicatorClickListener(position -> Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition()));
    }

    public void addItems(List<String> images) {
        List<SliderItem> sliderItemList = new ArrayList<>();
        for (String imageString : images) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setImageUrl(imageString);
            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}