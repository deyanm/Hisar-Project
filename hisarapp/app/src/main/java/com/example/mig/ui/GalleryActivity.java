package com.example.mig.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.ConfigurationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mig.R;
import com.example.mig.adapters.GalleryAdapter;
import com.example.mig.databinding.ActivityGalleryBinding;
import com.example.mig.model.ImageModel;
import com.example.mig.model.Poi;
import com.example.mig.ui.dialog.GalleryDialogFragment;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = GalleryActivity.class.getSimpleName();

    private ActivityGalleryBinding binding;
    private GalleryViewModel viewModel;

    private List<Poi> pois;
    private List<ImageModel> modelsList;

    private GalleryAdapter listViewAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_gallery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialiseAdapters();
        populate();
    }

    private void populate() {
        viewModel.getPlace().observe(this, place -> {

            pois = new ArrayList<>();

            pois.addAll(place.getPois().getSights());
            pois.addAll(place.getPois().getHotels());
            pois.addAll(place.getPois().getRestaurants());

            modelsList.clear();
            for (Poi poi : pois) {
                for (String image : poi.getImages()) {
                    ImageModel imageModel = new ImageModel(poi.getId(), poi.getName(), image);
                    modelsList.add(imageModel);
                }
            }
            listViewAdapter.renewItems(modelsList);
            this.listViewAdapter.notifyDataSetChanged();
            if (modelsList.isEmpty()) {
                binding.nothingShow.setVisibility(View.VISIBLE);
                binding.recyclerViewGallery.setVisibility(View.GONE);
            } else {
                binding.nothingShow.setVisibility(View.GONE);
                binding.recyclerViewGallery.setVisibility(View.VISIBLE);
            }
        });
        String langCode = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
        viewModel.getCurrentPlace(langCode);
    }

    private void initialiseAdapters() {
        modelsList = new ArrayList<>();
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
//        staggeredGridLayoutManager.invalidateSpanAssignments();
        binding.recyclerViewGallery.setLayoutManager(staggeredGridLayoutManager);
        listViewAdapter = new GalleryAdapter(this, model -> {
            GalleryDialogFragment.newInstance(model.getImageUrl(), model.getName()).show(getSupportFragmentManager().beginTransaction(), "MyDialogFragment");
        }, modelsList);
        binding.recyclerViewGallery.setOnTouchListener((v, event) -> {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            return false;
        });
        binding.recyclerViewGallery.setAdapter(listViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                listViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                listViewAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
