package com.ads.appgm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.preference.PreferenceManager;

import com.ads.appgm.clickListeners.ButtonPanic;
import com.ads.appgm.databinding.ActivityMainBinding;
import com.ads.appgm.help.HelpActivity;
import com.ads.appgm.manager.PanicManager;
import com.ads.appgm.manager.PaniqueManagerListener;
import com.ads.appgm.manager.device.output.OutputDeviceListener;
import com.ads.appgm.service.BackgroundLocationService;
import com.ads.appgm.service.ForegroundLocationService;
import com.ads.appgm.service.PaniqueQuick;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.SettingsUtils;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity implements PaniqueManagerListener,
        OutputDeviceListener,
        NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityMainBinding binding;
    public static MainActivity instance;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private ForegroundLocationService foregroundLocationService = null;
    private boolean serviceBound = false;

    public ForegroundLocationService getForegroundLocationService() {
        return foregroundLocationService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setAppTheme();

        SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
        binding.textViewName.setText(getUserName(sp));
        setButtonPanicState(sp);

        setSupportActionBar(binding.toolbar.getRoot());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.getRoot(), binding.toolbar.getRoot(),
                R.string.open_drawer, R.string.close_drawer);
        binding.getRoot().addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.textColor));
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

        if (SettingsUtils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions();
                }
            }
        }
    }

    private void setButtonPanicState(SharedPreferences sp) {
        boolean status = sp.getBoolean("panicActive", false);
        if (isPaniqueQuickServiceRunning() && PaniqueQuick.getInstance() != null) {
            status |= PaniqueQuick.getInstance().getPanicStatus();
        }
        togglePanic(status);
    }

    private void togglePanic(boolean isActive) {
        if (isActive) {
            binding.buttonPanic.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.custom_button_active));
        } else {
            binding.buttonPanic.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.custom_button_inactive));
        }
    }

    private String getUserName(SharedPreferences sp) {
        String message = "Olá";
        String nome = sp.getString(Constants.USER_NAME, null);
        if (nome != null) {
            message += ", " + nome + "!";
        } else {
            message += "!";
        }
        return message;
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
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        bindService(new Intent(this, ForegroundLocationService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
        binding.buttonPanic.setOnClickListener(new ButtonPanic(this));
        super.onStart();
    }

    @Override
    protected void onResume() {
        BackgroundLocationService.iniciarTemporizador(getApplicationContext());
        super.onResume();
        if (this.isPaniqueQuickServiceRunning() && PaniqueQuick.getInstance() != null) {
            PaniqueQuick.getInstance().registerPaniqueManagerListener(this);
        }
        PanicManager.getInstance(true).setListener(this);
        setButtonPanicState(SharedPreferenceUtil.getSharedePreferences());
        instance = this;
    }

    @Override
    protected void onPause() {
        if (this.isPaniqueQuickServiceRunning() && PaniqueQuick.getInstance() != null) {
            PaniqueQuick.getInstance().unregisterPaniqueManagerListener();
        }
        PanicManager.getInstance(true).setListener(null);
        instance = null;
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    "Precisa de GPS",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", view -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSIONS_REQUEST_CODE))
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
//                foregroundLocationService.requestLocationUpdates();
                Log.i(TAG, "Can read location");
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        "Precisa do GPS",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Update the buttons state depending on whether location updates are being requested.
        if (key.equals(SettingsUtils.KEY_REQUESTING_LOCATION_UPDATES)) {
            Log.i(TAG, "Status Changed");
            if (sharedPreferences.getBoolean(key, false)) {
                PanicManager.getInstance(true).turnOn(getApplicationContext());
            } else {
                PanicManager.getInstance(true).turnOff();
            }
        }
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPanicStatusChanged(final boolean status) {
        runOnUiThread(() -> togglePanic(status));
    }

    @Override
    public void onStatusChanged(String deviceType, boolean status) {
        runOnUiThread(() -> togglePanic(status));
    }

    private boolean isPaniqueQuickServiceRunning() {
        return PaniqueQuick.getInstance() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_help) {
            initHelp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initHelp() {
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_help) {
            initHelp();
        } else if (itemId == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
//        } else if (itemId == R.id.nav_recordings) {
//            Toast.makeText(this, "Clicou no Gravações", Toast.LENGTH_SHORT).show();
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

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ForegroundLocationService.LocalBinder binder = (ForegroundLocationService.LocalBinder) service;
            foregroundLocationService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            foregroundLocationService = null;
            serviceBound = false;
        }
    };
}
