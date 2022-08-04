package com.example.mig.ui;

import static com.example.mig.utils.Utils.showSnackbar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.mig.BuildConfig;
import com.example.mig.R;
import com.example.mig.adapters.BlogAdapter;
import com.example.mig.databinding.ActivityMainBinding;
import com.example.mig.model.Place;
import com.example.mig.model.Poi;
import com.example.mig.utils.Constants;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.MainViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import okio.BufferedSink;
import okio.Okio;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int SETTINGS_ACTIVITY = 1111;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private ProgressDialog mProgressDialog;

    private MapView mMapView;
    private MapElementLayer mPinLayer;
    private static final Geopoint HISAR_LOC = new Geopoint(42.5041069, 24.7034471);
    private static final Geopoint PIRO_LOC = new Geopoint(45.9961283, 27.2476761);
    private List<Poi> combinedPois;
    private Address currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());

        mMapView = new MapView(this, MapRenderMode.VECTOR);
        mMapView.setCredentialsKey(BuildConfig.CREDENTIALS_KEY);
        binding.mapView.addView(mMapView);
        mMapView.onCreate(savedInstanceState);
        mPinLayer = new MapElementLayer();
        mMapView.getLayers().add(mPinLayer);

        bindViews();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.loading_data));
        mProgressDialog.show();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationRequest();
        buildLocationSettingsRequest();
        viewModel.getVersion().observe(this, version -> {
            if (version.getData() != null) {
                if (!(new File(getFilesDir() + "/db.json").exists())) {
                    viewModel.getFileResponse().observe(this, fileResponse -> {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.cancel();
                        }
                        try {
                            File file = new File(getFilesDir() + "/db.json");

                            BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
                            bufferedSink.writeAll(fileResponse.source());
                            bufferedSink.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    viewModel.downloadFile();
                } else if (viewModel.getCurrentFileVersion() == -1 || (Integer) version.getData() > viewModel.getCurrentFileVersion()) {
                    viewModel.setCurrentFileVersion((Integer) version.getData());
                    viewModel.getFileResponse().observe(this, fileResponse -> {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.cancel();
                        }
                        try {
                            File file = new File(getFilesDir() + "/db.json");

                            BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
                            bufferedSink.writeAll(fileResponse.source());
                            bufferedSink.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    viewModel.downloadFile();
                } else {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                    }
                }
            } else if (version.getError() != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.cancel();
                }
            }

        });

        viewModel.getCurrentWeatherObserver().observe(this, locationResponse -> {
            if (locationResponse.getCurrent() != null) {
                binding.weatherDegrees.setText(locationResponse.getCurrent().getTempC().toString() + "°");
                Glide.with(this).load(Uri.parse("https:" + locationResponse.getCurrent().getCondition().getIcon())).centerCrop().into(binding.degreesImage);
            } else if (locationResponse.getError() != null) {
                Log.d(TAG, "Error getting weather data: " + locationResponse.getError());
            }
        });

        viewModel.getChannel().observe(this, channel -> {
            if (channel != null) {
                RecyclerView recyclerView = findViewById(R.id.blogPostsRV);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                BlogAdapter adapter = new BlogAdapter(this, channel.getArticles());
                recyclerView.setAdapter(adapter);
            }
        });
        getData();
    }

    void getData() {
        viewModel.getVersions();
        viewModel.fetchFeed();
    }

    void bindViews() {
        binding.appBarTitle.setText(getString(R.string.title_activity_main));
        binding.nearbyTv.setText(getString(R.string.nearby_places));
        binding.exploreNearbyBtn.setText(getString(R.string.explore_nearby));
        binding.blogTipsTv.setText(getString(R.string.news));

        binding.mapTv.setText(getString(R.string.title_activity_map));
        binding.mapBtn.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        binding.migTv.setText(getString(R.string.title_activity_about));
        binding.migBtn.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        binding.placesTv.setText(getString(R.string.title_activity_places));
        binding.placesBtn.setOnClickListener(v -> startActivity(new Intent(this, PlacesActivity.class).putExtra("TYPE", Constants.SIGHTS_KEY)));
        binding.hotelsTv.setText(getString(R.string.title_activity_hotels));
        binding.hotelsBtn.setOnClickListener(v -> startActivity(new Intent(this, PlacesActivity.class).putExtra("TYPE", Constants.HOTEL_KEY)));
        binding.infoTv.setText(getString(R.string.title_activity_info));
        binding.infoBtn.setOnClickListener(v -> startActivity(new Intent(this, InfoActivity.class)));
        binding.restTv.setText(getString(R.string.title_activity_restaurants));
        binding.restBtn.setOnClickListener(v -> startActivity(new Intent(this, PlacesActivity.class).putExtra("TYPE", Constants.REST_KEY)));
        binding.galleryTv.setText(getString(R.string.title_activity_gallery));
        binding.galleryBtn.setOnClickListener(v -> startActivity(new Intent(this, GalleryActivity.class)));
        binding.moreTv.setText(getString(R.string.title_activity_more));

        binding.swiperefresh.setOnRefreshListener(this);
        binding.settingsBtn.setOnClickListener(v -> startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_ACTIVITY));

        binding.exploreNearbyBtn.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class).putExtra("MY_LOC", true)));

        if (viewModel.getCurrentPlaceId() == 1) {
            binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 6));
        } else if (viewModel.getCurrentPlaceId() == 2) {
            binding.button2.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
        }
        binding.button1.setOnClickListener(v -> {
            viewModel.setCurrentPlaceId(1);
            binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 6));
            binding.button2.setStrokeWidth(0);
        });
        binding.button2.setOnClickListener(v -> {
            viewModel.setCurrentPlaceId(2);
            binding.button2.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 6));
            binding.button1.setStrokeWidth(0);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
        if (viewModel.getCurrentPlaceId() == 1) {
            mMapView.setScene(
                    MapScene.createFromLocationAndZoomLevel(HISAR_LOC, 15),
                    MapAnimationKind.NONE);
        } else if (viewModel.getCurrentPlaceId() == 2) {
            mMapView.setScene(
                    MapScene.createFromLocationAndZoomLevel(PIRO_LOC, 15),
                    MapAnimationKind.NONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            viewModel.getPlace().observe(this, place -> {
//                selectedSight = place.getPois().getSights().get(0);
                showPins(place);
//                currentPlace = place;
            });
            String langCode = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
            viewModel.getCurrentPlace(langCode);

            viewModel.getLocation().observe(this, s -> {
                Geopoint geopoint = new Geopoint(s.getLatitude(), s.getLongitude());
                mMapView.setScene(
                        MapScene.createFromLocationAndZoomLevel(geopoint, 15),
                        MapAnimationKind.NONE);
                MapIcon pushpin = new MapIcon();
//                pushpin.setImage(getPinImage());
                pushpin.setLocation(geopoint);
                pushpin.setTitle(getString(R.string.my_loc));
                mPinLayer.getElements().add(pushpin);
            });
        }, 700);
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
//                    selectedSight = sight1;
                    break;
                }
            }
            return false;
        });
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


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            try {
                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);

                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    if (viewModel.getCurrentPlaceId() == -1) {
                        switch (address.getCountryCode()) {
                            case "BG":
                                viewModel.setCurrentPlaceId(1);
                                binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 6));
                                binding.button2.setStrokeWidth(0);
                                break;
                            case "RO":
                                viewModel.setCurrentPlaceId(2);
                                binding.button2.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 6));
                                binding.button1.setStrokeWidth(0);
                                break;
                            default:
                                viewModel.setCurrentPlaceId(1);
                                binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 6));
                                binding.button2.setStrokeWidth(0);

                        }
                    }
                    currentAddress = address;
                    viewModel.getCurrentWeather(address.getLocality());
                    binding.cityLocationTv.setText(String.format("%s, %s", address.getLocality(), address.getCountryName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "Stopped location updates in " + MainActivity.class.getSimpleName());
                });
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(this, R.string.permission_rationale,
                    android.R.string.ok, view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startLocationUpdates();
            } else {
                showSnackbar(this, R.string.permission_denied_explanation,
                        R.string.title_activity_settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_ACTIVITY) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                recreate();
            }
        }
    }

    @Override
    public void onRefresh() {
        getData();
        viewModel.getCurrentWeather(currentAddress != null ? currentAddress.getLocality() : null);
        binding.swiperefresh.setRefreshing(false);
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        binding.appBarTitle.setText(getString(R.string.title_activity_main));
    }
}