package com.example.mig.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mig.R;
import com.example.mig.databinding.ActivityMainBinding;
import com.example.mig.utils.Constants;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.MainViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import okio.BufferedSink;
import okio.Okio;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;
    MainViewModel viewModel;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_main);
        setSupportActionBar(toolbar);

        bindViews();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } else {
            checkLocationPermission();
        }
        viewModel.getVersion().observe(this, version -> {
            if (viewModel.getCurrentFileVersion() == -1 || version > viewModel.getCurrentFileVersion()) {
                viewModel.setCurrentFileVersion(version);
                viewModel.getFileResponse().observe(this, fileResponse -> {
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
            }
        });
        viewModel.getVersions();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MainActivity.this,
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    void bindViews() {
        binding.mapBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MapActivity.class));
        });
        binding.migBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });
        binding.placesBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, PlacesActivity.class).putExtra("TYPE", Constants.SIGHTS_KEY));
        });
        binding.hotelsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, PlacesActivity.class).putExtra("TYPE", Constants.HOTEL_KEY));
        });
        binding.infoBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, InfoActivity.class));
        });

        binding.restBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, PlacesActivity.class).putExtra("TYPE", Constants.REST_KEY));
        });
        binding.galleryBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, GalleryActivity.class));
        });

        if (viewModel.getCurrentPlaceId() == -1) {
            viewModel.setCurrentPlaceId(1);
            binding.button1.setStrokeWidth(Utils.dp2px(MainActivity.this.getResources(), 5));
        } else if (viewModel.getCurrentPlaceId() == 1) {
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
            startActivityForResult(new Intent(this, SettingsActivity.class), 1111);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                try {
                    Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(locationList.get(locationList.size() - 1).getLatitude(), locationList.get(locationList.size() - 1).getLongitude(), 1);

                    if (addresses.size() > 0) {
                        String countryName = addresses.get(0).getCountryCode();
                        Log.i(TAG, "Location: " + countryName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                finish();
            }
        }
    }
}