package com.example.mig.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mig.R;
import com.example.mig.databinding.FragmentSettingsBinding;
import com.example.mig.viewmodel.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivityFragment extends Fragment {

    private static final String TAG = SettingsActivityFragment.class.getSimpleName();
    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        binding.notificationsLayout.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.settingsLanguageFragment);
        });
        binding.favouritesLayout.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.settingsFavouritesFragment);
        });
        binding.exitBtn.setOnClickListener(v -> {
            getActivity().setResult(Activity.RESULT_FIRST_USER);
            getActivity().finish();
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_settings);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
