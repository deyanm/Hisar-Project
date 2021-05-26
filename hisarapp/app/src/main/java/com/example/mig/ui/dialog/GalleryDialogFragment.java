package com.example.mig.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mig.R;
import com.example.mig.utils.CustomImageView;

import org.jetbrains.annotations.NotNull;

public class GalleryDialogFragment extends DialogFragment {

    public static GalleryDialogFragment newInstance(String imageId, String placeName) {
        GalleryDialogFragment myFragment = new GalleryDialogFragment();

        Bundle args = new Bundle();
        args.putString("placeName", placeName);
        args.putString("imageId", imageId);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CustomImageView photoView = view.findViewById(R.id.customImageView);
        int id = view.getContext().getResources().getIdentifier(getArguments().getString("imageId"), "drawable", view.getContext().getPackageName());
        getArguments().getInt("someInt", 0);
        photoView.setImageResource(id);

        ((TextView) view.findViewById(R.id.placeNameTv)).setText(getArguments().getString("placeName"));
        view.findViewById(R.id.closeBtn).setOnClickListener(v -> {
            dismiss();
        });
    }
}