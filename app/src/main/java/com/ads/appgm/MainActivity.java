package com.ads.appgm;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ads.appgm.clickListeners.ButtonPanic;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onStart() {
        Button panic = findViewById(R.id.buttonPanic);

        panic.setOnClickListener(new ButtonPanic());

        super.onStart();
    }
}
