package com.example.mig.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.mig.BuildConfig;
import com.example.mig.R;
import com.example.mig.adapters.ImageSliderAdapter;
import com.example.mig.databinding.ActivityAboutPoiBinding;
import com.example.mig.model.Poi;
import com.example.mig.model.SliderItem;
import com.example.mig.viewmodel.AboutPoiViewModel;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AboutPoiActivity extends AppCompatActivity {

    ActivityAboutPoiBinding binding;
    AboutPoiViewModel viewModel;
    private Poi poi;
    private SliderView sliderView;
    private ImageSliderAdapter adapter;
    private MapView mMapView;
    private MapElementLayer mPinLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutPoiBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(AboutPoiViewModel.class);
        setContentView(binding.getRoot());

        poi = (Poi) getIntent().getSerializableExtra("POI");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(poi.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindViews();

        if (poi.getLocation() != null) {
            binding.locationLayout.setVisibility(View.VISIBLE);
            mMapView = new MapView(this, MapRenderMode.RASTER);
            mMapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
            binding.mapView.addView(mMapView);
            mMapView.onCreate(savedInstanceState);
            mPinLayer = new MapElementLayer();
            mMapView.getLayers().add(mPinLayer);
            Geopoint location = new Geopoint(poi.getLocation().getLat(), poi.getLocation().getLon());
            String title = poi.getName();

            MapIcon pushpin = new MapIcon();
            pushpin.setLocation(location);
            pushpin.setTitle(title);

            mPinLayer.getElements().add(pushpin);

            mMapView.setScene(MapScene.createFromLocationAndZoomLevel(location, 15), MapAnimationKind.NONE);
        }

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
        addItems(poi.getImages());
    }

    private void bindViews() {
        binding.placeNameTv.setText(poi.getName());
        binding.locTv.setText(poi.getAddress());
        binding.descTv.setText(poi.getShortDescription());
        binding.phoneTv.setText(poi.getPhone());
        binding.websiteTv.setText(poi.getLinks());
        binding.favFab.setImageResource(viewModel.getIsPlaceFav(poi.getId()) ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_baseline_favorite_border_24);
        binding.favFab.setOnClickListener(view -> {
            boolean currentState = viewModel.getIsPlaceFav(poi.getId());
            binding.favFab.setImageResource(currentState ? R.drawable.ic_baseline_favorite_border_24 : R.drawable.ic_baseline_favorite_24);
            viewModel.setIsPlaceFav(poi.getId(), !currentState);
        });
        binding.fullDescTv.setText(poi.getDescription());
        binding.directionsBtn.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + poi.getLocation().getLat() + "," + poi.getLocation().getLon());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
        binding.shareBtn.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = poi.getShortDescription();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, poi.getName());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}