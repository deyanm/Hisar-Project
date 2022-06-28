package com.example.mig.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mig.R;
import com.example.mig.databinding.FragmentPlaceAboutBinding;
import com.example.mig.model.Poi;
import com.example.mig.viewmodel.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaceAboutFragment extends Fragment {

    private static final String TAG = PlaceAboutFragment.class.getSimpleName();
    private FragmentPlaceAboutBinding binding;
    private SettingsViewModel viewModel;

    public static PlaceAboutFragment newInstance(Poi poi) {
        Bundle args = new Bundle();
        args.putSerializable("POI", poi);
        PlaceAboutFragment fragment = new PlaceAboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        Poi poi = (Poi) getArguments().getSerializable("POI");

        binding.fullDescTv.setText(poi.getDescription());

        if (poi.getOptions() != null) {
            if (poi.getOptions().getWifi()) {
                binding.facWifi.setStrokeColorResource(R.color.colorPrimary);
                binding.facWifi.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facWifi.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getAirport()) {
                binding.facAirport.setStrokeColorResource(R.color.colorPrimary);
                binding.facAirport.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facAirport.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }

            if (poi.getOptions().getBar()) {
                binding.facBar.setStrokeColorResource(R.color.colorPrimary);
                binding.facBar.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facBar.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }

            if (poi.getOptions().getGym()) {
                binding.facGym.setStrokeColorResource(R.color.colorPrimary);
                binding.facGym.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facGym.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }

            if (poi.getOptions().getBeach()) {
                binding.facBeach.setStrokeColorResource(R.color.colorPrimary);
                binding.facBeach.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facBeach.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }

            if (poi.getOptions().getCasino()) {
                binding.facCasino.setStrokeColorResource(R.color.colorPrimary);
                binding.facCasino.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facCasino.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getElevator()) {
                binding.facElevator.setStrokeColorResource(R.color.colorPrimary);
                binding.facElevator.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facElevator.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getBreakfast()) {
                binding.facBreak.setStrokeColorResource(R.color.colorPrimary);
                binding.facBreak.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facBreak.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getLaundry()) {
                binding.facLaundry.setStrokeColorResource(R.color.colorPrimary);
                binding.facLaundry.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facLaundry.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getMeeting()) {
                binding.facMeeting.setStrokeColorResource(R.color.colorPrimary);
                binding.facMeeting.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facMeeting.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getPet()) {
                binding.facPet.setStrokeColorResource(R.color.colorPrimary);
                binding.facPet.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facPet.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getRestaurant()) {
                binding.facRest.setStrokeColorResource(R.color.colorPrimary);
                binding.facRest.setCompoundDrawableTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facRest.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            if (poi.getOptions().getWheelchair()) {
                binding.facWheelchair.setStrokeColorResource(R.color.colorPrimary);
                TextViewCompat.setCompoundDrawableTintList(binding.facWheelchair, ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                binding.facWheelchair.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
        } else {
            binding.facilitiesLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
