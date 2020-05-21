package com.ads.appgm;

import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ads.appgm.clickListeners.ButtonPanic;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;


public class MainActivity extends AppCompatActivity {
    private MaterialButton panicButton;
    private SwitchMaterial panicFunction;

    private TransitionDrawable transAnimButFlash;

    boolean panicButtonStatus = false;
    int btnAnimTime = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);

        panicFunction = findViewById(R.id.panic_function);
        panicButton =  findViewById(R.id.buttonPanic);

        transAnimButFlash = (TransitionDrawable) panicButton.getBackground();
        transAnimButFlash.resetTransition();

        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onStart() {
        Button panic = findViewById(R.id.buttonPanic);

        panic.setOnClickListener(new ButtonPanic());

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPanicQuickServiceRunning()) {
            PanicQuick.getInstance().registerTorchieManagerListener(this);
            if (this.isPanicOn()) {
                this.setPanicButtonStatus(this.isPanicOn());
            }
        }
        panicFunction.setChecked(isPanicQuickServiceRunning());
    }

    @Override
    protected void onPause() {
        if (this.isPanicQuickServiceRunning()) {
            PanicQuick.getInstance().unregisterTorchieManagerListener();
        } else if (this.isPanicOn()) {
            this.toggleTorch(null);
        }
        super.onPause();
        super.onPause();
    }

    private void setPanicButtonStatus(boolean enabled) {
        panicButtonStatus = enabled;
        if (panicButtonStatus) {
            transAnimButFlash.startTransition(btnAnimTime);
        } else {
            transAnimButFlash.reverseTransition(btnAnimTime);
        }
    }

    private boolean isPanicOn() {
        if (isPanicQuickServiceRunning()) {
            return PanicQuick.getInstance().getPanicStatus();
        } else {
            return PanicManager.getInstance(SettingsUtils.getPanicSource(this), true).getStatus();
        }
    }

    private boolean isPanicQuickServiceRunning() {
        return PanicQuick.getInstance() != null;
    }
}
