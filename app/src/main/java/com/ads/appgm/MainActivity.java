package com.ads.appgm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.ads.appgm.clickListeners.ButtonPanic;
import com.ads.appgm.databinding.ActivityMainBinding;
import com.ads.appgm.dialog.PermissionDialog;
import com.ads.appgm.manager.PanicManager;
import com.ads.appgm.manager.PaniqueManagerListener;
import com.ads.appgm.manager.device.output.OutputDeviceListener;
import com.ads.appgm.service.PaniqueQuick;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SettingsUtils;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements PaniqueManagerListener, NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    public static MainActivity instance;

    private TransitionDrawable transAnimButFlash;

    boolean panicButtonStatus = false;
    int btnAnimTime = 200;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setAppTheme();

        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        String message = "Olá";
        String nome = sp.getString(Constants.USER_NAME, null);
        if (nome != null) {
            message += ", " + nome + "!";
        } else {
            message += "!";
        }
        binding.textViewName.setText(message);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        binding.panicFunction.setOnClickListener(this::openAccessibilitySettings);

        //transAnimButFlash = (TransitionDrawable) panicButton.getBackground();
        //transAnimButFlash.resetTransition();

        setSupportActionBar(binding.toolbar.getRoot());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.getRoot(), binding.toolbar.getRoot(),
                R.string.open_drawer,R.string.close_drawer);
        binding.getRoot().addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.textColor));
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

        boolean isActive = sp.getBoolean("panicActive", false);
        if (isActive){
            binding.buttonPanic.setBackground(getDrawable(R.drawable.custom_button_active));
        }else{
            binding.buttonPanic.setBackground(getDrawable(R.drawable.custom_button_inactive));
        }
    }

    private void setAppTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        View logo = binding.navView.getHeaderView(0);
        binding.navView.removeHeaderView(logo);
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                binding.navView.inflateHeaderView(R.layout.nav_header);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                binding.navView.inflateHeaderView(R.layout.nav_header_dark);
                break;
        }
    }

    @Override
    protected void onStart() {
        binding.buttonPanic.setOnClickListener(new ButtonPanic(fusedLocationProviderClient, this));

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaniqueQuickServiceRunning()) {
            PaniqueQuick.getInstance().registerPaniqueManagerListener(this);
            if (this.isPanicOn()) {
                this.setPanicButtonStatus(this.isPanicOn());
            }
        }
        binding.panicFunction.setChecked(isPaniqueQuickServiceRunning());
        instance=this;
    }

    @Override
    protected void onPause() {
        if (this.isPaniqueQuickServiceRunning()) {
            PaniqueQuick.getInstance().unregisterPaniqueManagerListener();
        } else if (this.isPanicOn()) {
            this.togglePanic(null);
        }
        super.onPause();
        instance=null;
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPanicStatusChanged(final boolean status) {
        runOnUiThread(() -> setPanicButtonStatus(status));
    }

    public void openAccessibilitySettings(View v) {
        if (binding.panicFunction.isChecked()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        } else {
            this.showDialogPermission();
        }
    }

    private void showDialogPermission() {
        PermissionDialog permissionDialog = new PermissionDialog();
        permissionDialog.show(getFragmentManager(), "Permission Dialog");
    }

    private void setPanicButtonStatus(boolean enabled) {
        panicButtonStatus = enabled;
        if (panicButtonStatus) {
            //transAnimButFlash.startTransition(btnAnimTime);
        } else {
            //transAnimButFlash.reverseTransition(btnAnimTime);
        }
    }

    private boolean isPaniqueQuickServiceRunning() {
        return PaniqueQuick.getInstance() != null;
    }

    public void togglePanic(View v) {
        if (isPaniqueQuickServiceRunning()) {
            PaniqueQuick.getInstance().togglePanic();
        } else {
            PanicManager.getInstance(SettingsUtils.getPanicSource(this), true).setListener(new OutputDeviceListener() {
                @Override
                public void onStatusChanged(String deviceType, final boolean status) {
                    runOnUiThread(() -> setPanicButtonStatus(status));
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
            PanicManager.getInstance(SettingsUtils.getPanicSource(this), true).toggle(this);
        }
    }

    private boolean isPanicOn() {
        if (isPaniqueQuickServiceRunning()) {
            return PaniqueQuick.getInstance().getPanicStatus();
        } else {
            return PanicManager.getInstance(SettingsUtils.getPanicSource(this), true).getStatus();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                Toast.makeText(this,"TODO: Mostrar tela de ajuda.",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_recordings:
                Toast.makeText(this,"Clicou no Gravações",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
        }
        binding.getRoot().closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public void onBackPressed() {
        if (binding.getRoot().isDrawerOpen(GravityCompat.START)) {
            binding.getRoot().closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
