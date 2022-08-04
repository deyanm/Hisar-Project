package com.example.mig.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mig.BuildConfig;
import com.example.mig.R;
import com.example.mig.databinding.ActivityMapBinding;
import com.example.mig.model.Place;
import com.example.mig.model.Poi;
import com.example.mig.utils.Constants;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapImage;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity {
    private static final String TAG = MapActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private ActivityMapBinding binding;
    private MapViewModel mapViewModel;
    private MapView mMapView;
    private MapElementLayer mPinLayer;
    private static final Geopoint HISAR_LOC = new Geopoint(42.5041069, 24.7034471);
    private static final Geopoint PIRO_LOC = new Geopoint(45.9961283, 27.2476761);
    private Poi selectedSight;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<Poi> combinedPois;
    private Place currentPlace;
    private boolean requestUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        Utils.checkLocale(this, mapViewModel.getLangLocale());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_map);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapView = new MapView(this, MapRenderMode.VECTOR);
        mMapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        binding.mapView.addView(mMapView);
        mMapView.onCreate(savedInstanceState);
        mPinLayer = new MapElementLayer();
        mMapView.getLayers().add(mPinLayer);

        bindViews();
        showSpinner();
    }

    private void bindViews() {
        binding.myLocBtn.setOnClickListener(v -> {
            getLastLocation();
        });
        binding.directionsBtn.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + selectedSight.getLocation().getLat() + "," + selectedSight.getLocation().getLon());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
        binding.moreBtn.setOnClickListener(view -> startActivity(new Intent(MapActivity.this, AboutPoiActivity.class).putExtra("TYPE", Constants.SIGHTS_KEY).putExtra("POI", selectedSight)));
    }

    private void showPins(Place place) {
        combinedPois = new ArrayList<>();
        combinedPois.addAll(place.getPois().getSights());
        combinedPois.addAll(place.getPois().getHotels());
        combinedPois.addAll(place.getPois().getRestaurants());
        for (Poi poi : combinedPois) {
            if (poi.getLocation() != null) {
                Geopoint location = new Geopoint(poi.getLocation().getLat(), poi.getLocation().getLon());
                String title = poi.getName();

                MapIcon pushpin = new MapIcon();
                pushpin.setLocation(location);
                pushpin.setTitle(title);

                mPinLayer.getElements().add(pushpin);
            }
        }

        mPinLayer.addOnMapElementTappedListener(mapElementTappedEventArgs -> {
            for (Poi sight1 : combinedPois) {
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
        if (sight.getImages() != null && sight.getImages().get(0) != null) {
            if (sight.getImages().get(0).startsWith("http")) {
                String url = sight.getImages().get(0).startsWith("http:") ? sight.getImages().get(0).replace("http", "https") : sight.getImages().get(0);
                Glide.with(this)
                        .load(Uri.parse(url))
                        .centerCrop()
                        .into(binding.placeImage);
            } else {
                Glide.with(this)
                        .load(Uri.parse("https://raw.githubusercontent.com/deyanm/hisarserver/main/images/" + sight.getImages().get(0) + ".jpg"))
                        .centerCrop()
                        .into(binding.placeImage);
            }
        }
        binding.placeText.setText(sight.getShortDescription());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
        if (mapViewModel.getCurrentPlaceId() == 1) {
            mMapView.setScene(
                    MapScene.createFromLocationAndZoomLevel(HISAR_LOC, 15),
                    MapAnimationKind.NONE);
        } else if (mapViewModel.getCurrentPlaceId() == 2) {
            mMapView.setScene(
                    MapScene.createFromLocationAndZoomLevel(PIRO_LOC, 15),
                    MapAnimationKind.NONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
//        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mapViewModel.getPlace().observe(this, place -> {
                if (place.getPois().getSights().isEmpty()) {
                    binding.noPlacesLayout.setVisibility(View.VISIBLE);
                    binding.placeCardLayout.setVisibility(View.GONE);
                    return;
                } else {
                    binding.noPlacesLayout.setVisibility(View.GONE);
                    binding.placeCardLayout.setVisibility(View.VISIBLE);
                }
                if (selectedSight == null) {
                    selectedSight = place.getPois().getSights().get(0);
                }
                showSightInfo(selectedSight);
                showPins(place);
                currentPlace = place;
            });
            String langCode = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
            mapViewModel.getCurrentPlace(langCode);

            mapViewModel.getLocation().observe(this, s -> {
                Geopoint geopoint = new Geopoint(s.getLatitude(), s.getLongitude());
                mMapView.setScene(
                        MapScene.createFromLocationAndZoomLevel(geopoint, 15),
                        MapAnimationKind.NONE);
                MapIcon pushpin = new MapIcon();
                pushpin.setImage(getPinImage());
                pushpin.setLocation(geopoint);
                pushpin.setTitle(getString(R.string.my_loc));
                mPinLayer.getElements().add(pushpin);
            });
        }, 700);

        if (getIntent().hasExtra("MY_LOC")) {
            requestUserLocation = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
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
        getMenuInflater().inflate(R.menu.menu, menu);

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

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MapActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void getLastLocation() {
        // check if permissions are given
        if (checkLocationPermission()) {

            // check if location is enabled
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        mapViewModel.getLocation().setValue(location);
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkLocationPermission()) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            mapViewModel.getLocation().setValue(mLastLocation);
        }
    };

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private MapImage getPinImage() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pin, null);

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return new MapImage(bitmap);
    }

    private void showSpinner() {

        AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.filled_exposed_dropdown);

        editTextFilledExposedDropdown.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    if (combinedPois != null) {
                        mPinLayer.getElements().clear();
                        for (Poi poi : combinedPois) {
                            if (poi.getLocation() != null) {
                                Geopoint location = new Geopoint(poi.getLocation().getLat(), poi.getLocation().getLon());
                                String title = poi.getName();

                                MapIcon pushpin = new MapIcon();
                                pushpin.setLocation(location);
                                pushpin.setTitle(title);

                                mPinLayer.getElements().add(pushpin);
                            }
                        }
                    }
                    break;
                case 1:
                    mPinLayer.getElements().clear();
                    if (currentPlace != null) {
                        mPinLayer.getElements().clear();
                        for (Poi poi : currentPlace.getPois().getSights()) {
                            if (poi.getLocation() != null) {
                                Geopoint location = new Geopoint(poi.getLocation().getLat(), poi.getLocation().getLon());
                                String title = poi.getName();

                                MapIcon pushpin = new MapIcon();
                                pushpin.setLocation(location);
                                pushpin.setTitle(title);

                                mPinLayer.getElements().add(pushpin);
                            }
                        }
                    }
                    break;
                case 2:
                    mPinLayer.getElements().clear();
                    if (currentPlace != null) {
                        for (Poi poi : currentPlace.getPois().getHotels()) {
                            if (poi.getLocation() != null) {
                                Geopoint location = new Geopoint(poi.getLocation().getLat(), poi.getLocation().getLon());
                                String title = poi.getName();

                                MapIcon pushpin = new MapIcon();
                                pushpin.setLocation(location);
                                pushpin.setTitle(title);

                                mPinLayer.getElements().add(pushpin);
                            }
                        }
                    }
                    break;
                case 3:
                    mPinLayer.getElements().clear();
                    if (currentPlace != null) {
                        for (Poi poi : currentPlace.getPois().getRestaurants()) {
                            if (poi.getLocation() != null) {
                                Geopoint location = new Geopoint(poi.getLocation().getLat(), poi.getLocation().getLon());
                                String title = poi.getName();

                                MapIcon pushpin = new MapIcon();
                                pushpin.setLocation(location);
                                pushpin.setTitle(title);

                                mPinLayer.getElements().add(pushpin);
                            }
                        }
                    }
                    break;
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        editTextFilledExposedDropdown.setAdapter(adapter);
        editTextFilledExposedDropdown.setText(editTextFilledExposedDropdown.getAdapter().getItem(0).toString(), false);
    }

}