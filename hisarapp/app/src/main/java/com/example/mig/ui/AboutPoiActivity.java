package com.example.mig.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mig.R;
import com.example.mig.adapters.ImageSliderAdapter;
import com.example.mig.databinding.ActivityAboutPoiBinding;
import com.example.mig.model.Poi;
import com.example.mig.model.SliderItem;
import com.example.mig.ui.fragments.PlaceAboutFragment;
import com.example.mig.ui.fragments.PlaceBookingFragment;
import com.example.mig.ui.fragments.PlaceReviewsFragment;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.AboutPoiViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AboutPoiActivity extends AppCompatActivity {

    private static final int NUM_TABS = 3;

    ActivityAboutPoiBinding binding;
    AboutPoiViewModel viewModel;
    private Poi poi;
    private SliderView sliderView;
    private ImageSliderAdapter adapter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutPoiBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(AboutPoiViewModel.class);
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());

        poi = (Poi) getIntent().getSerializableExtra("POI");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindViews();

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
        if (poi.getImages() != null && poi.getImages().size() > 0) {
            addItems(poi.getImages());
        }
    }

    private void bindViews() {
        String[] titles = {"Booking", "About", "Reviews"};
        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;

        FragmentStateAdapter adapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return PlaceBookingFragment.newInstance(poi);
                    case 1:
                        return PlaceAboutFragment.newInstance(poi);
                    case 2:
                        return PlaceReviewsFragment.newInstance(poi);
                }
                return PlaceBookingFragment.newInstance(poi);
            }

            @Override
            public int getItemCount() {
                return NUM_TABS;
            }
        };
        viewPager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {
            tab.setText(titles[position]);
            viewPager.setCurrentItem(tab.getPosition(), true);
        });
        tabLayoutMediator.attach();
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
        menuInflater.inflate(R.menu.about_menu, menu);
        menu.findItem(R.id.action_fav).setIcon(poi.isFav() ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_baseline_favorite_border_24);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = poi.getShortDescription();
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, poi.getName());
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        } else if (item.getItemId() == R.id.action_fav) {
            poi.setFav(!poi.isFav());
            item.setIcon(poi.isFav() ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_baseline_favorite_border_24);
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}