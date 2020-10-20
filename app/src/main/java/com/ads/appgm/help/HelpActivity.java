package com.ads.appgm.help;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.appgm.R;
import com.ads.appgm.databinding.ActivityHelpBinding;
import com.synnapps.carouselview.ImageListener;

public class HelpActivity extends AppCompatActivity {

    ActivityHelpBinding binding;

    int[] helpImages = {R.drawable.screenshot_1,
            R.drawable.screenshot_2,
            R.drawable.screenshot_3,
            R.drawable.screenshot_4,
            R.drawable.screenshot_5,
            R.drawable.screenshot_6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.carouselHelp.setPageCount(helpImages.length);

        binding.carouselHelp.setImageListener(imageListener);

        setSupportActionBar(binding.settingsToolbar.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        binding.settingsToolbar.getRoot().setNavigationOnClickListener(view -> onBackPressed());
    }

    ImageListener imageListener = (position, imageView) -> imageView.setImageResource(helpImages[position]);
}