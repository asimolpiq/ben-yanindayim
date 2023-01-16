package com.asimolpiq.benyanindayim;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.MediaController;

import com.asimolpiq.benyanindayim.databinding.ActivityWalktroughBinding;


public class WalktroughActivity extends AppCompatActivity {

    private ActivityWalktroughBinding binding;
    private SharedPreferences sharedPreferences;
    private Boolean isFirstShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalktroughBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = this.getSharedPreferences("com.asimolpiq.benyanindayim", Context.MODE_PRIVATE);
        isFirstShow = sharedPreferences.getBoolean("first_show",false);
        if (!isFirstShow) {
            videoplay();
        } else {
            goHome();
        }


    }

    public void videoplay(){
        MediaController mediaController = new MediaController(this);
        String videopath =
                "android.resource://"+getPackageName()+"/"+R.raw.app_review;  // if your video is not .mp4 change it your video extension
        Uri uri = Uri.parse(videopath);
        binding.walktroughVideoView.setVideoURI(uri);
        binding.walktroughVideoView.setMediaController(mediaController);
        mediaController.setAnchorView(binding.walktroughVideoView);
        binding.walktroughVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sharedPreferences.edit().putBoolean("first_show",true).apply();
               goHome();
            }
        });
        binding.walktroughVideoView.start();

    }

    public void goHome(){
        Intent intent = new Intent(WalktroughActivity.this,Calculator_Activity.class);
        finish();
        startActivity(intent);
    }
}


