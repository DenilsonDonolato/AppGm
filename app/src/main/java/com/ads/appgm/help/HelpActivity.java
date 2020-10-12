package com.ads.appgm.help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.ads.appgm.R;
import com.ads.appgm.databinding.ActivityHelpBinding;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;

public class HelpActivity extends AppCompatActivity {

    ActivityHelpBinding binding;

    int[] helpImages     = {R.drawable.screenshot_1,
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

//        binding.carouselHelp.setPageCount(helpImages.length);
//
//        binding.carouselHelp.setImageListener(imageListener);
//
//        setSupportActionBar(binding.settingsToolbar.getRoot());
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//        binding.settingsToolbar.getRoot().setNavigationOnClickListener(view -> onBackPressed());

        binding.carouselView.setSize(helpImages.length);
        binding.carouselView.setResource(R.layout.carousel_image);
        binding.carouselView.setAutoPlay(false);
        binding.carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
        binding.carouselView.setCarouselOffset(OffsetType.CENTER);
        binding.carouselView.setCarouselViewListener((view, position) -> {
            // Example here is setting up a full image carousel
            ImageView imageView = view.findViewById(R.id.carouselImage);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),helpImages[position],getTheme()));
        });
        // After you finish setting up, show the CarouselView
        binding.carouselView.show();
    }

//    ImageListener imageListener = (position, imageView) -> imageView.setImageResource(helpImages[position]);
}