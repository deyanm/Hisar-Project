package com.example.mig.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.mig.databinding.VideoDialogBinding;

public class VideoDialog extends AppCompatDialogFragment {
    private VideoDialogBinding binding;
    private String videoId;

    public VideoDialog(String videoId) {
        this.videoId = videoId;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = VideoDialogBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(binding.getRoot());

//        getLifecycle().addObserver(binding.youtubePlayer);
//        binding.youtubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error) {
//                super.onError(youTubePlayer, error);
//                Toast.makeText(getContext(),"Some error occurred!",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onReady(YouTubePlayer youTubePlayer) {
//                super.onReady(youTubePlayer);
//                youTubePlayer.loadVideo(videoId, 0);
//
//            }
//        });

        //binding.youtubePlayer.enterFullScreen();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
//        binding.youtubePlayer.release();
    }
}