package com.deyanm.hisar.ui;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.deyanm.hisar.BuildConfig;
import com.deyanm.hisar.R;
import com.deyanm.hisar.databinding.ActivityMapBinding;
import com.deyanm.hisar.model.Place;
import com.deyanm.hisar.model.Poi;
import com.deyanm.hisar.utils.Constants;
import com.deyanm.hisar.viewmodel.MapViewModel;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity {
    private static final String TAG = MapActivity.class.getSimpleName();
    private ActivityMapBinding binding;
    private MapViewModel mapViewModel;
    private MapView mMapView;
    private MapElementLayer mPinLayer;
    private static final Geopoint HISAR_LOC = new Geopoint(42.5041069, 24.7034471);
    private Poi selectedSight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_map);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapView = new MapView(this, MapRenderMode.RASTER);
        mMapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        binding.mapView.addView(mMapView);
        mMapView.onCreate(savedInstanceState);
        mPinLayer = new MapElementLayer();
        mMapView.getLayers().add(mPinLayer);

        mapViewModel.getPlace().observe(this, place -> {
            Log.d(TAG, place.getName());
            selectedSight = place.getPois().getSights().get(0);
            showSightInfo(selectedSight);
            showPins(place);
        });
        mapViewModel.getCurrentPlace();

        binding.directionsBtn.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + selectedSight.getLocation().getLat() + "," + selectedSight.getLocation().getLon());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
        binding.moreBtn.setOnClickListener(view -> startActivity(new Intent(MapActivity.this, AboutPoiActivity.class).putExtra("TYPE", Constants.SIGHTS_KEY).putExtra("POI", selectedSight)));
        binding.shareBtn.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = selectedSight.getShortDescription();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, selectedSight.getName());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
        binding.myLocBtn.setOnClickListener(view -> {

        });
    }

    private void showPins(Place place) {
        for (Poi sight : place.getPois().getSights()) {
            Geopoint location = new Geopoint(sight.getLocation().getLat(), sight.getLocation().getLon());
            String title = sight.getName();

            MapIcon pushpin = new MapIcon();
            pushpin.setLocation(location);
            pushpin.setTitle(title);

            mPinLayer.getElements().add(pushpin);
        }

        mPinLayer.addOnMapElementTappedListener(mapElementTappedEventArgs -> {
            for (Poi sight1 : place.getPois().getSights()) {
                float[] distance = new float[1];
                Location.distanceBetween(sight1.getLocation().getLat(), sight1.getLocation().getLon(), mapElementTappedEventArgs.location.getPosition().getLatitude(), mapElementTappedEventArgs.location.getPosition().getLongitude(), distance);
                if (distance[0] < 50.0) {
                    showSightInfo(sight1);
                    selectedSight = sight1;
                    break;
                }
            }
            return false;
        });
    }

    private void showSightInfo(Poi sight) {
        binding.placeTitle.setText(sight.getName());
//        binding.placeImage.setImageResource();
        binding.placeText.setText(sight.getShortDescription());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
        mMapView.setScene(
                MapScene.createFromLocationAndZoomLevel(HISAR_LOC, 15),
                MapAnimationKind.NONE);
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