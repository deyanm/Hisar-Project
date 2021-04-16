package com.deyanm.hisar.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.deyanm.hisar.databinding.FragmentLanguageBinding;
import com.deyanm.hisar.viewmodel.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsLanguageFragment extends Fragment {

    private FragmentLanguageBinding binding;
    private SettingsViewModel mViewModel;

    public static SettingsLanguageFragment newInstance() {
        return new SettingsLanguageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLanguageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
//        mViewModel.updateActionBarTitle(getString(R.string.title_language));
    }

}