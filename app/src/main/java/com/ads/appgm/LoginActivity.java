package com.ads.appgm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ads.appgm.databinding.ActivityLoginBinding;
import com.ads.appgm.model.Login;
import com.ads.appgm.model.LoginResponse;
import com.ads.appgm.model.MyLocation;
import com.ads.appgm.model.TokenBody;
import com.ads.appgm.service.BackEndService;
import com.ads.appgm.service.HttpClient;
import com.ads.appgm.util.Animations;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MaskEditUtil;
import com.ads.appgm.util.MyNotification;
import com.ads.appgm.util.MyPermission;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private BackEndService backEndService;
    private MyNotification myNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.getRoot());

        binding.editTextCPF.addTextChangedListener(MaskEditUtil.mask(binding.editTextCPF, MaskEditUtil.FORMAT_CPF));
        binding.editTextCPF.setOnFocusChangeListener(this::onFocusChange);

        binding.editTextPassword.setOnFocusChangeListener(this::onFocusChange);

        myNotification = MyNotification.getInstance(getApplicationContext());
        myNotification.createNotificationChannel();

        binding.buttonLogin.setOnClickListener(v -> {
            hideKeyboard(v);
            Animations.animateView(binding.loginProgress.getRoot(), View.VISIBLE, 0.5f, 250);
            sendLoginRequest();
        });
    }

    private void sendLoginRequest() {
        if (binding.editTextCPF.getText() == null ||
                binding.editTextPassword.getText() == null ||
                binding.editTextCPF.getText().toString().isEmpty() ||
                binding.editTextPassword.getText().toString().isEmpty()) {
            Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 100);
            Toast.makeText(getApplicationContext(), "Favor preencher CPF e senha", Toast.LENGTH_LONG).show();
            return;
        }
        backEndService = HttpClient.getInstance();
        Login login = new Login(
                binding.editTextCPF.getText().toString().replace(".", "").replace("-", ""),
                binding.editTextPassword.getText().toString()
        );
        Call<LoginResponse> loginResponse = backEndService.loginRequest(login);
        loginResponse.enqueue(loginResponseCallback);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private final Callback<LoginResponse> loginResponseCallback = new Callback<LoginResponse>() {
        @Override
        public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
            if (!response.isSuccessful()) {
                if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Erro de Autenticação", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Erro de Servidor", Toast.LENGTH_LONG).show();
                }
                Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 150);
                return;
            }
            LoginResponse loginResponse = response.body();
            if (loginResponse == null) {
                Toast.makeText(getApplicationContext(), "Erro de Cadastro", Toast.LENGTH_LONG).show();
                Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 150);
                return;
            }
            SharedPreferences sp = SharedPreferenceUtil.getSharedPreferences();
            sp.edit().putString(Constants.USER_TOKEN, loginResponse.getToken())
                    .putLong(Constants.USER_ID, loginResponse.getId())
                    .putString(Constants.USER_NAME, loginResponse.getName())
                    .putLong(Constants.MEASURE_ID, loginResponse.getMeasureId())
                    .apply();
            int bodyStartIndex = loginResponse.getToken().indexOf(".") + 1;
            int bodyEndIndex = loginResponse.getToken().indexOf(".", bodyStartIndex);
            String bodyBase64 = loginResponse.getToken().substring(bodyStartIndex, bodyEndIndex);
            byte[] bodyBytes = android.util.Base64.decode(bodyBase64, Base64.DEFAULT);
            String bodyJson = new String(bodyBytes);
            TokenBody tokenBody;
            Log.e("LOGIN", "body:" + bodyJson);
            try {
                tokenBody = new ObjectMapper().readValue(bodyJson, TokenBody.class);
                Log.e("LOGIN", tokenBody.getExpirationTime().toString());
                Date date = new Date(tokenBody.getExpirationTime().longValue()*1000);
                Log.e("LOGIN", date.toString());
            } catch (JsonProcessingException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
            requestLocationOnSuccessfulLogin(loginResponse);
        }

        @Override
        public void onFailure(Call<LoginResponse> call, @NotNull Throwable t) {
            if (!call.isCanceled()) {
                call.cancel();
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 100);
            Toast.makeText(getApplicationContext(), "Erro de rede", Toast.LENGTH_LONG).show();
        }
    };

    private void requestLocationOnSuccessfulLogin(LoginResponse loginResponse) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MyPermission myPermission = MyPermission.getInstance();
            myPermission.requestGPS(getApplicationContext(), permissionResponse -> {
                if (permissionResponse.isGranted()) {
                    requestPosition(loginResponse,fusedLocationProviderClient);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_name).setMessage("O App será encerrado")
                            .setNeutralButton(android.R.string.ok, cancelListener)
                            .setCancelable(false)
                            .show();
                }
            });
        } else {
            requestPosition(loginResponse, fusedLocationProviderClient);
        }
    }

    private final DialogInterface.OnClickListener cancelListener = (dialog, which) -> {
        dialog.dismiss();
        finishAffinity();
    };

    @SuppressLint("MissingPermission")
    private void requestPosition(LoginResponse loginResponse, FusedLocationProviderClient fusedLocationProviderClient) {
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        List<Double> position = new ArrayList<>(2);
                        position.add(location.getLatitude());
                        position.add(location.getLongitude());
                        MyLocation myLocation = new MyLocation(position, false);
                        Call<Void> call = backEndService.postLocation(myLocation, loginResponse.getToken());
                        call.enqueue(positionCallback);
                    } else {
                        Toast.makeText(getApplicationContext(), "Não foi possível usar GPS", Toast.LENGTH_LONG).show();
                        goToMainActivity();
                    }
                });
    }

    private void goToMainActivity() {
        Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 150);
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private final Callback<Void> positionCallback = new Callback<Void>() {
        @Override
        public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
//            if (response.isSuccessful()) {
//                Toast.makeText(getApplicationContext(), "Enviou GPS", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Erro ao enviar GPS", Toast.LENGTH_LONG).show();
//            }
            goToMainActivity();
        }

        @Override
        public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
//            Toast.makeText(getApplicationContext(), "Erro ao enviar GPS", Toast.LENGTH_LONG).show();
            goToMainActivity();
        }
    };
}
