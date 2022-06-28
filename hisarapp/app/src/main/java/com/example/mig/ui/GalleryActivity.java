package com.example.mig.ui;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.ConfigurationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mig.R;
import com.example.mig.adapters.GalleryAdapter;
import com.example.mig.databinding.ActivityGalleryBinding;
import com.example.mig.model.ImageModel;
import com.example.mig.model.Poi;
import com.example.mig.ui.fragments.SlideShowFragment;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.GalleryAdapterCallBacks {

    private static final String TAG = GalleryActivity.class.getSimpleName();

    private ActivityGalleryBinding binding;
    private GalleryViewModel viewModel;

    private List<Poi> pois;
    public List<ImageModel> imageModels;
//    private List<ImageModel> modelsList;

    private GalleryAdapter mGalleryAdapter;
//    private SearchView searchView;

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

            imageModels.clear();
            for (Poi poi : pois) {
                for (String image : poi.getImages()) {
                    ImageModel imageModel = new ImageModel(poi.getId(), poi.getName(), image);
                    imageModels.add(imageModel);
                }
            }
            mGalleryAdapter.renewItems(imageModels);
            mGalleryAdapter.notifyDataSetChanged();
        });
        String langCode = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
        viewModel.getCurrentPlace(langCode);
    }

    private void initialiseAdapters() {
//        modelsList = new ArrayList<>();
//        listViewAdapter = new GalleryAdapter(this, model -> {
//            GalleryDialogFragment.newInstance(model.getImageUrl(), model.getName()).show(getSupportFragmentManager().beginTransaction(), "MyDialogFragment");
//        }, modelsList);
//        binding.recyclerViewGallery.setOnClickListener(v -> {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        });
//        binding.recyclerViewGallery.setAdapter(listViewAdapter);

        imageModels = new ArrayList<>();
        RecyclerView recyclerViewGallery = findViewById(R.id.recyclerViewGallery);
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 2));
        //Create RecyclerView Adapter
        mGalleryAdapter = new GalleryAdapter(this);
        //set adapter to RecyclerView
        recyclerViewGallery.setAdapter(mGalleryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.about_menu, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        // listening to search query text change
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // filter recycler view when query submitted
//                listViewAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                // filter recycler view when text is changed
//                listViewAdapter.getFilter().filter(query);
//                return false;
//            }
//        });

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

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
//        if (!searchView.isIconified()) {
//            searchView.setIconified(true);
//            return;
//        }
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(int position) {
        //create fullscreen SlideShowFragment dialog
        SlideShowFragment slideShowFragment = SlideShowFragment.newInstance(position);
        //setUp style for slide show fragment
        slideShowFragment.setStyle(STYLE_NORMAL, R.style.DialogFragmentTheme);
        //finally show dialogue
        slideShowFragment.show(getSupportFragmentManager(), null);
    }

}
