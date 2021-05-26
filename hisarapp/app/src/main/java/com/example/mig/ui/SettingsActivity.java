package com.example.mig.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mig.R;
import com.example.mig.databinding.ActivitySettingsBinding;
import com.example.mig.utils.Utils;
import com.example.mig.viewmodel.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    ActivitySettingsBinding binding;
    SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        Utils.checkLocale(this, viewModel.getLangLocale());
        setContentView(binding.getRoot());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "Back pressed activity");
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        AppBarConfiguration.Builder appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph());
        NavigationUI.navigateUp(navController, appBarConfiguration.build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "back pressed");
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        AppBarConfiguration.Builder appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph());
        return NavigationUI.navigateUp(navController, appBarConfiguration.build()) || super.onSupportNavigateUp();
    }
}