package com.ads.appgm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.ads.appgm.databinding.SettingsActivityBinding;
import com.ads.appgm.dialog.PermissionDialog;

public class SettingsActivity extends AppCompatActivity {

    private SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.settings.getId(), new SettingsFragment(this))
                .commit();
        setSupportActionBar(binding.settingsToolbar.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        binding.settingsToolbar.getRoot().setNavigationOnClickListener(view -> onBackPressed());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private AppCompatActivity activity;

        public SettingsFragment(SettingsActivity settingsActivity) {
            this.activity = settingsActivity;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            switch (preference.getKey()) {
                case "panic_quick":
                    openAccessibilitySettings(pm.getBoolean("panic_quick", false));

            }
            return super.onPreferenceTreeClick(preference);
        }

        public void openAccessibilitySettings(boolean isChecked) {
            if (isChecked) {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            } else {
                this.showDialogPermission();
            }
        }

        private void showDialogPermission() {
            PermissionDialog permissionDialog = new PermissionDialog();
            permissionDialog.show(activity.getFragmentManager(), "Permission Dialog");
        }
    }
}