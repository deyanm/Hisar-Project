package com.example.mig.ui.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
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

import com.example.mig.R;
import com.example.mig.databinding.FragmentLanguageBinding;
import com.example.mig.viewmodel.SettingsViewModel;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsLanguageFragment extends Fragment {

    private static final String TAG = SettingsLanguageFragment.class.getSimpleName();
    private FragmentLanguageBinding binding;
    private SettingsViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLanguageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration.Builder appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph());
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration.build());

        Locale lastLocale;
        if (viewModel.getLangLocale().equals("AUTO")) {
            binding.changeLangSwitch.setChecked(true);
            binding.changeLangGroup.setVisibility(View.GONE);
            lastLocale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        } else {
             binding.changeLangSwitch.setChecked(false);
            binding.changeLangGroup.setVisibility(View.VISIBLE);
            lastLocale = new Locale(viewModel.getLangLocale());
        }

        switch (lastLocale.getLanguage()) {
            case "en":
                binding.changeLangGroup.check(binding.enRadio.getId());
                break;
            case "bg":
                binding.changeLangGroup.check(binding.bgRadio.getId());
                break;
            case "ro":
                binding.changeLangGroup.check(binding.roRadio.getId());
                break;
        }

        binding.changeLangGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String langCode = String.valueOf(view.findViewById(checkedId).getTag());
            viewModel.setLangLocale(langCode);
            Locale locale = new Locale(langCode);
            Locale.setDefault(locale);
            Resources resources = getActivity().getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        });

        binding.changeLangSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setLangLocale("AUTO");
                binding.changeLangGroup.setVisibility(View.GONE);
                Locale systemLocale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
                Locale.setDefault(systemLocale);
                Resources resources = getActivity().getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(systemLocale);
                resources.updateConfiguration(config, resources.getDisplayMetrics());
            } else {
                binding.changeLangGroup.setVisibility(View.VISIBLE);
                if (binding.changeLangGroup.getCheckedRadioButtonId() != -1) {
                    viewModel.setLangLocale(String.valueOf(view.findViewById(binding.changeLangGroup.getCheckedRadioButtonId()).getTag()));
                }
            }
        });

    }
}
