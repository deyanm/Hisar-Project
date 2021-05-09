package com.deyanm.hisar.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.deyanm.hisar.R;
import com.deyanm.hisar.databinding.FragmentSettingsBinding;
import com.deyanm.hisar.viewmodel.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivityFragment extends Fragment {

    private static final String TAG = SettingsActivityFragment.class.getSimpleName();
    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

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
            Navigation.findNavController(view).navigate(R.id.settingsNotificationsFragment);
        });
        binding.favouritesLayout.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.settingsFavouritesFragment);
        });
        binding.exitBtn.setOnClickListener(v -> {
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
        });
//        binding.notificationsLayout.setOnClickListener(v -> {
//            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingsNotificationsFragment()).commit();
//        });
    }
}
