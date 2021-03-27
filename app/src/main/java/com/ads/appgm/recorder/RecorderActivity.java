package com.ads.appgm.recorder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ads.appgm.R;
import com.ads.appgm.databinding.ActivityAboutBinding;
import com.ads.appgm.databinding.ActivityRecorderBinding;

public class RecorderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRecorderBinding binding = ActivityRecorderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.settingsToolbar.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Gravações");
        binding.settingsToolbar.getRoot().setNavigationOnClickListener(view -> onBackPressed());
    }
}