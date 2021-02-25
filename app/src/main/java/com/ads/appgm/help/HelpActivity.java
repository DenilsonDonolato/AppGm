package com.ads.appgm.help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ads.appgm.R;
import com.ads.appgm.databinding.ActivityHelpBinding;
import com.synnapps.carouselview.ViewListener;

public class HelpActivity extends AppCompatActivity {

    ActivityHelpBinding binding;

    int[] helpImages = {R.drawable.screenshot_1,
            R.drawable.screenshot_2,
            R.drawable.screenshot_3,
            R.drawable.screenshot_4,
            R.drawable.screenshot_5,
            R.drawable.screenshot_6,
            R.drawable.screenshot_7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.carouselHelp.setPageCount(helpImages.length);
        binding.carouselHelp.setViewListener(viewListener);

        setSupportActionBar(binding.settingsToolbar.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Manual");
        binding.settingsToolbar.getRoot().setNavigationOnClickListener(view -> onBackPressed());
    }

    ViewListener viewListener = position -> {
        View customView = getLayoutInflater().inflate(R.layout.carousel_image, null);
        ImageView imageView = customView.findViewById(R.id.carouselImage);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, helpImages[position]));
        return customView;
    };
}