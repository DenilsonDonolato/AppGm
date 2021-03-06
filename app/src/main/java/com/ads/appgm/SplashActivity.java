package com.ads.appgm;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MyNotification;
import com.ads.appgm.util.MyTimestamp;
import com.ads.appgm.util.SharedPreferenceUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SplashActivity extends AppCompatActivity {

    private boolean validLogin;
    private boolean firstLogin;
    private LocationManager lm;
    private AlertDialog permission,rationale,enableGps, expiredLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceUtil.initialize(getApplicationContext());
        SharedPreferences sp = SharedPreferenceUtil.getSharedPreferences();
        lm = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
        MyNotification myNotification = MyNotification.getInstance(getApplicationContext());
        myNotification.createNotificationChannel();

        createDialogs();
        firstLogin = sp.getBoolean(Constants.FIRST_LOGIN, true);
        Calendar calendar = Calendar.getInstance();
        /*
            Timestamp 1970 till now  - 10800000 (Brazilian OffSet)
         */
        long now = calendar.getTimeInMillis() - 10800000L;

        String measureExpiration = sp.getString(Constants.EXPIRATION_DATE, String.valueOf(now));
        long loginExpiration = Long.parseLong(measureExpiration);

        if(!firstLogin) {
            if(loginExpiration <= now) {
                Log.e("LOGIN AUTORIZADO: ", "FALSE");
                validLogin = false;
            } else {
                Log.e("LOGIN AUTORIZADO: ", "TRUE");
                validLogin = true;
            }
        } else {
            // First Login
            validLogin = false;
        }
    }

    private void createDialogs() {
        if(enableGps == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            enableGps = builder.setTitle(R.string.GPS).setMessage(R.string.reason_gps)
                    .setNegativeButton(R.string.close, listenerGpsOn)
                    .setCancelable(false)
                    .setPositiveButton(R.string.turn_on_gps, listenerGpsOn)
                    .create();
        }
        if(rationale == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            rationale = builder.setTitle(R.string.app_name).setMessage(R.string.reason_location_rationale)
                    .setNegativeButton(R.string.close, listenerRationalePermission)
                    .setCancelable(false)
                    .setPositiveButton("Configurações", listenerLocationPermission)
                    .create();
        }
        if (permission == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            permission = builder.setTitle(R.string.GPS).setMessage(R.string.reason_location)
                    .setNegativeButton(R.string.close, listenerLocationPermission)
                    .setCancelable(false)
                    .setPositiveButton(R.string.allow_gps, listenerLocationPermission)
                    .create();
        }
        if(expiredLogin == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            expiredLogin = builder.setTitle("Login expirado").setMessage(R.string.login_expired)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goToLoginActivity();
                        }
                    })
                    .create();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        permissionCheckAndRequest();
    }

    private void permissionCheckAndRequest() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            startApp();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showRationaleDialog();
                // You can directly ask for the permission.
                //Permissoes sao automaticas antes da versão 23
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            Constants.GPS_PERMISSION_REQUEST);
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.GPS_PERMISSION_REQUEST);
                }
            }
        }
    }

    private void showExpiredLogin() {
        expiredLogin.show();
    }


    private void startApp() {
        if (gpsLigado()) {
            if (!firstLogin && validLogin) {
                Log.e("LOGIN STATUS: ", "AINDA ESTA VALIDO");
                goToMainActivity();
            } else if(firstLogin && !validLogin){
                Log.e("LOGIN STATUS: ", "AINDA NAO FOI FEITO LOGIN");
                goToLoginActivity();
            } else if(!firstLogin && !validLogin) {
                // Login expirou
                Log.e("LOGIN STATUS: ", "EXPIRADO");
                showExpiredLogin();
            }
        } else {
            showTurnOnGps();
        }
    }

    private boolean gpsLigado() {
        return lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showTurnOnGps() {
        if (!enableGps.isShowing()) {
            enableGps.show();
            rationale.dismiss();
            permission.dismiss();
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.GPS_PERMISSION_REQUEST:
                if (resultCode == RESULT_OK) {
                    startApp();
                } else {
                    showGPSDialog();
                }
                break;
            case Constants.GPS_TURN_ON:
                if (gpsLigado()) {
                    startApp();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.gps_off_warning, Toast.LENGTH_LONG).show();
                    finishAffinity();
                }
                break;
            case Constants.REQUEST_PERMISSION_SETTING:
                permissionCheckAndRequest();
                break;
            default:
                goToLoginActivity();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.GPS_PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean allPermissions = true;
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allPermissions = false;
                            break;
                        }
                    }
                    if (allPermissions) {
                        startApp();
                    } else {
                        showGPSDialog();
                    }
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            showRationaleDialog();
                        }
                    } else {
                        showGPSDialog();
                    }
                }
                break;
            default:
                Log.e("SPLASH", "case default, request code=" + requestCode);
                break;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    private void showRationaleDialog() {
        if (!rationale.isShowing()) {
            rationale.show();
            permission.dismiss();
            enableGps.dismiss();
        }
    }

    private void showGPSDialog() {
        if (!permission.isShowing()) {
            permission.show();
            rationale.dismiss();
            enableGps.dismiss();
        }
    }

    private final DialogInterface.OnClickListener listenerLocationPermission = (dialog, which) -> {
        if (which == Dialog.BUTTON_POSITIVE) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, Constants.REQUEST_PERMISSION_SETTING);
        } else {
            dialog.dismiss();
            finishAffinity();
        }
    };

    private final DialogInterface.OnClickListener listenerRationalePermission = (dialog, which) -> {
        if (which == Dialog.BUTTON_POSITIVE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            Constants.GPS_PERMISSION_REQUEST);
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.GPS_PERMISSION_REQUEST);
                }
            }
        } else {
            dialog.dismiss();
            finishAffinity();
        }
    };

    private final DialogInterface.OnClickListener listenerGpsOn = (dialog, which) -> {
        if (which == Dialog.BUTTON_POSITIVE) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, Constants.GPS_TURN_ON);
        } else {
            dialog.dismiss();
            finishAffinity();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (enableGps!=null) {
            enableGps.dismiss();
            enableGps = null;
        }
        if (permission != null) {
            permission.dismiss();
            permission = null;
        }
        if (rationale != null) {
            rationale.dismiss();
            rationale = null;
        }
    }
}