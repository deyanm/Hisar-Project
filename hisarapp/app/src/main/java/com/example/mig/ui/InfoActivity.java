package com.example.mig.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mig.R;
import com.example.mig.adapters.ImageSliderAdapter;
import com.example.mig.databinding.ActivityInfoBinding;
import com.example.mig.model.SliderItem;
import com.example.mig.utils.Utils;
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
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String langCode = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
        viewModel.getCurrentPlace(langCode);
        viewModel.getPlace().observe(this, place -> {
            if (place != null) {
                binding.placeNameTv.setText(place.getInfo().getName());
                binding.locTv.setText(place.getInfo().getAddress());
                binding.fullDescTv.setText(Html.fromHtml(place.getInfo().getFullDescription(), new ImageGetter(), null));

                if (place.getInfo().getImages() != null && place.getInfo().getImages().size() > 0) {
                    addItems(place.getInfo().getImages());
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

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            if (source == null) {
                return null;
            }
            int id;

            id = getResources().getIdentifier(source, "drawable", getPackageName());

            if (id == 0) {
                // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
                id = getResources().getIdentifier(source, "drawable", "android");
            }

            if (id == 0) {
                // prevent a crash if the resource still can't be found
                return null;
            } else {
                Drawable d = ResourcesCompat.getDrawable(getResources(), id, getTheme());
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        }

    }
}