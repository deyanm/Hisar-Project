package com.example.mig.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mig.R;
import com.example.mig.adapters.PlacesAdapter;
import com.example.mig.databinding.ActivityPlacesBinding;
import com.example.mig.model.Poi;
import com.example.mig.utils.Constants;
import com.example.mig.viewmodel.PlacesViewModel;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlacesActivity extends AppCompatActivity {

    private ActivityPlacesBinding binding;
    private PlacesViewModel viewModel;
    private ArrayList<Poi> pois;
    private PlacesAdapter placesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlacesBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
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

        pois = new ArrayList<>();
        setUpRecyclerViews();
        viewModel.getPoisByType(getIntent().getStringExtra("TYPE"));
        viewModel.getPois().observe(this, pois -> {
            placesAdapter.setPois(pois);
        });

    }

    private void setUpRecyclerViews() {
        binding.placesRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        placesAdapter = new PlacesAdapter(this, pois);
        binding.placesRecycler.setAdapter(placesAdapter);
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