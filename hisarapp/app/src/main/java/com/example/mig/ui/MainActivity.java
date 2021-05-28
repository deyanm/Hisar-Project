package com.example.mig.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mig.BuildConfig;
import com.example.mig.R;
import com.example.mig.databinding.ActivityMainBinding;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import okio.BufferedSink;
import okio.Okio;

import static com.example.mig.utils.Utils.showSnackbar;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

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
    private String lastLocale;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_main);
        setSupportActionBar(toolbar);

        bindViews();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading application data...");
        mProgressDialog.show();

        lastLocale = viewModel.getLangLocale();
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
        viewModel.getVersions();
    }

    void bindViews() {
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

        if (viewModel.getCurrentPlaceId() == 1) {
            binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
        } else if (viewModel.getCurrentPlaceId() == 2) {
            binding.button2.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
        }
        binding.button1.setOnClickListener(v -> {
            viewModel.setCurrentPlaceId(1);
            binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
            binding.button2.setStrokeWidth(0);
        });
        binding.button2.setOnClickListener(v -> {
            viewModel.setCurrentPlaceId(2);
            binding.button2.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
            binding.button1.setStrokeWidth(0);
        });
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
            startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_ACTIVITY);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationUpdates();
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
                    String countryName = addresses.get(0).getCountryCode();
                    if (viewModel.getCurrentPlaceId() == -1) {
                        switch (countryName) {
                            case "BG":
                                viewModel.setCurrentPlaceId(1);
                                binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
                                binding.button2.setStrokeWidth(0);
                                break;
                            case "RO":
                                viewModel.setCurrentPlaceId(2);
                                binding.button2.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
                                binding.button1.setStrokeWidth(0);
                                break;

                        }
                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
        if (lastLocale != null && !viewModel.getLangLocale().equals(lastLocale)) {
            recreate();
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
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
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
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
                finish();
            }
        }
    }
}