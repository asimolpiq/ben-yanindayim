package com.asimolpiq.benyanindayim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.asimolpiq.benyanindayim.databinding.ActivityInfoBinding;

public class Info_Activity extends AppCompatActivity {
    ActivityInfoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.resim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyWebSite("https://alpayguroglu.info");
            }
        });
    }
    public void goToMyWebSite(String url){
        Uri uri= Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}