package com.example.mig.ui;

import static com.example.mig.utils.Utils.showSnackbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mig.BuildConfig;
import com.example.mig.R;
import com.example.mig.adapters.PlacesAdapter;
import com.example.mig.databinding.ActivityPlacesBinding;
import com.example.mig.model.Poi;
import com.example.mig.utils.Constants;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.PlacesViewModel;
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

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlacesActivity extends AppCompatActivity {

    private static final String TAG = PlacesActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private ActivityPlacesBinding binding;
    private PlacesViewModel viewModel;
    private ArrayList<Poi> pois;
    private PlacesAdapter placesAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlacesBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        switch (getIntent().getStringExtra("TYPE")) {
            case Constants.SIGHTS_KEY:
                toolbar.setTitle(R.string.title_activity_places);
                break;
            case Constants.HOTEL_KEY:
                toolbar.setTitle(R.string.title_activity_hotels);
                break;
            case Constants.REST_KEY:
                toolbar.setTitle(R.string.title_activity_restaurants);
                break;
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationRequest();
        buildLocationSettingsRequest();

        pois = new ArrayList<>();
        setUpRecyclerViews();
        viewModel.getPoisByType(getIntent().getStringExtra("TYPE"));
        viewModel.getPois().observe(this, pois -> {
            for (Poi poi : pois) {
                poi.setFav(viewModel.getIsPlaceFav(poi.getId()));
            }
            this.pois.clear();
            this.pois.addAll(pois);
            placesAdapter.notifyDataSetChanged();
            if (this.pois.isEmpty()) {
                binding.nothingShow.setVisibility(View.VISIBLE);
                binding.placesRecycler.setVisibility(View.GONE);
            } else {
                binding.nothingShow.setVisibility(View.GONE);
                binding.placesRecycler.setVisibility(View.VISIBLE);
            }
        });
        binding.reservationCardLayout.placeNameTvRes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesAdapter.getFilter().filter(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.reservationCardLayout.reservationSearchBtn.setOnClickListener(v -> {
            placesAdapter.getFilter().filter(binding.reservationCardLayout.placeNameTvRes.getText().toString().trim());
        });
        binding.reservationCardLayout.reservationMapBtn.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpRecyclerViews() {
        binding.placesRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        placesAdapter = new PlacesAdapter(this, pois, new PlacesAdapter.PlaceClickListener() {
            @Override
            public void onPlaceClick(Poi poi) {
                startActivity(new Intent(PlacesActivity.this, AboutPoiActivity.class).putExtra("POI", poi));
            }

            @Override
            public void onFavClick(Poi poi, int position, boolean isFav) {
                poi.setFav(isFav);
                placesAdapter.notifyItemChanged(position);
                viewModel.setIsPlaceFav(poi.getId(), isFav);
            }
        });
        binding.placesRecycler.setOnTouchListener((v, event) -> {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            return false;
        });
        binding.placesRecycler.setAdapter(placesAdapter);
    }

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

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mCurrentLocation == null) {
                mCurrentLocation = locationResult.getLastLocation();
                Log.d(TAG, "curr loc:" + mCurrentLocation.getLatitude());
                for (Poi poi : pois) {
                    if (poi.getLocation() != null && mCurrentLocation != null) {
                        if (mCurrentLocation.getLatitude() > 0 && mCurrentLocation.getLongitude() > 0) {
                            Location poiLoc = new Location("");
                            poiLoc.setLatitude(poi.getLocation().getLat());
                            poiLoc.setLongitude(poi.getLocation().getLon());
                            poi.setDistanceKm(mCurrentLocation.distanceTo(poiLoc) / 1000);
                        }
                    }

                }
                placesAdapter.notifyDataSetChanged();
            }
        }
    };

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");

                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(PlacesActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);
                            Toast.makeText(PlacesActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "Stopped location updates in " + MainActivity.class.getSimpleName());
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationUpdates();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(this, R.string.permission_rationale,
                    android.R.string.ok, (View.OnClickListener) view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(PlacesActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(PlacesActivity.this,
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
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startLocationUpdates();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(this, R.string.permission_denied_explanation,
                        R.string.title_activity_settings, (View.OnClickListener) view -> {
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