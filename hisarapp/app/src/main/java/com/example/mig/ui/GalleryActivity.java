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
import com.example.mig.adapters.GalleryAdapter;
import com.example.mig.databinding.ActivityGalleryBinding;
import com.example.mig.model.ImageModel;
import com.example.mig.model.Poi;
import com.example.mig.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.ItemListener {

    ActivityGalleryBinding binding;
    GalleryViewModel viewModel;

    RecyclerView recyclerGallery;
    String text = "";
    List<Poi> pois;
    List<ImageModel> modelsList, filterModels;

    GalleryAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerGallery = findViewById(R.id.recyclerViewGallery);

        combinePois();

    }

    void combinePois() {
        viewModel.getCurrentPlace();
        viewModel.getPlace().observe(this, place -> {

            pois = new ArrayList<>();

            pois.addAll(place.getPois().getSights());
            pois.addAll(place.getPois().getHotels());
            pois.addAll(place.getPois().getRestaurants());
        });
    }

    private void populateRestaurantsAndCuisines() {
        modelsList = new ArrayList<>();

        for (Poi poi : pois) {
            for (String image : poi.getImages()) {
                ImageModel imageModel = new ImageModel(poi.getId(), poi.getName(), image);
                modelsList.add(imageModel);
            }
        }
        filterModels = new ArrayList<>(modelsList);

        initialiseAdapters();

    }

    private void initialiseAdapters() {
        recyclerGallery.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listViewAdapter = new GalleryAdapter(this, model -> {

        }, filterModels);
        recyclerGallery.setAdapter(listViewAdapter);
    }

    @Override
    public void onItemClick(ImageModel model) {

//        editTextChangedFromClick = true;
//
//        if (model.isCuisine) {
//            edit_text_search.setText(model.name);
//            listViewAdapter.getFilter().filter(model.name);
//
//        } else {
//
//            edit_text_search.setText(model.name);
//            showSearchView.handleToolBar(GalleryActivity.this, card_search, toolbar, view_search, recyclerView, edit_text_search, line_divider);
//            Toast.makeText(getApplicationContext(), model.name + " was selected.", Toast.LENGTH_LONG).show();
//        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

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
