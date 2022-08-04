package com.example.mig.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mig.BuildConfig;
import com.example.mig.databinding.FragmentPlaceBookingBinding;
import com.example.mig.model.Poi;
import com.example.mig.utils.Constants;
import com.example.mig.viewmodel.SettingsViewModel;
import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaceBookingFragment extends Fragment {

    private static final String TAG = PlaceBookingFragment.class.getSimpleName();
    private FragmentPlaceBookingBinding binding;
    private SettingsViewModel viewModel;
    private MapView mMapView;
    private MapElementLayer mPinLayer;

    public static PlaceBookingFragment newInstance(Poi poi) {
        Bundle args = new Bundle();
        args.putSerializable("POI", poi);
        PlaceBookingFragment fragment = new PlaceBookingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceBookingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        Poi poi = (Poi) getArguments().getSerializable("POI");
        binding.placeNameTv.setText(poi.getName());
        binding.locTv.setText(poi.getAddress());
        binding.descTv.setText(poi.getShortDescription());
        binding.phoneTv.setText(poi.getPhone());
        binding.phoneTv.setOnClickListener(v -> {
            if (poi.getPhone() != null) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + poi.getPhone()));
                startActivity(callIntent);
            }
        });
        binding.websiteTv.setText(poi.getUrl());
        binding.websiteTv.setOnClickListener(v -> {
            if (poi.getUrl() != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(poi.getUrl()));
                startActivity(browserIntent);
            }
        });
        binding.directionsBtn.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + poi.getLocation().getLat() + "," + poi.getLocation().getLon());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
        if (poi.getType().equals(Constants.SIGHTS_KEY)) {
            binding.ratingLayout.setVisibility(View.GONE);
        }

        if (poi.getLocation() != null) {
            binding.locationLayout.setVisibility(View.VISIBLE);
            mMapView = new MapView(getActivity(), MapRenderMode.RASTER);
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
