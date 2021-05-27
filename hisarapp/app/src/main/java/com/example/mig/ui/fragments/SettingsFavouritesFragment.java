package com.example.mig.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mig.R;
import com.example.mig.adapters.FavouritesAdapter;
import com.example.mig.adapters.PlacesAdapter;
import com.example.mig.databinding.FragmentFavouritesBinding;
import com.example.mig.model.Poi;
import com.example.mig.viewmodel.SettingsViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFavouritesFragment extends Fragment {

    private static final String TAG = SettingsFavouritesFragment.class.getSimpleName();
    private FragmentFavouritesBinding binding;
    private SettingsViewModel mViewModel;
    private List<Poi> pois;
    private FavouritesAdapter favouritesAdapter;
    private Toolbar toolbar;

    public static SettingsFavouritesFragment newInstance() {
        return new SettingsFavouritesFragment();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration.Builder appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph());
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration.build());

        pois = new ArrayList<>();
        binding.recyclerFavourites.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        favouritesAdapter = new FavouritesAdapter(view.getContext(), pois, new PlacesAdapter.PlaceClickListener() {
            @Override
            public void onPlaceClick(Poi poi) {
               // not called here
            }

            @Override
            public void onFavClick(Poi poi, int position, boolean isFav) {
                poi.setFav(isFav);
                favouritesAdapter.notifyItemChanged(position);
                mViewModel.setIsPlaceFav(poi.getId(), isFav);
            }
        });
        binding.recyclerFavourites.setAdapter(favouritesAdapter);

        String langCode = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0).getLanguage();
        mViewModel.getAllPlacePois(langCode);
        mViewModel.getPois().observe(getActivity(), pois -> {
            for (Poi poi : pois) {
                poi.setFav(mViewModel.getIsPlaceFav(poi.getId()));
            }
            this.pois.clear();
            this.pois.addAll(pois);
            favouritesAdapter.notifyDataSetChanged();
            if (this.pois.isEmpty()) {
//                binding.nothingShow.setVisibility(View.VISIBLE);
                binding.recyclerFavourites.setVisibility(View.GONE);
            } else {
//                binding.nothingShow.setVisibility(View.GONE);
                binding.recyclerFavourites.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle(getString(R.string.favourites));
    }
}