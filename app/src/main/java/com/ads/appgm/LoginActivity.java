package com.ads.appgm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.appgm.databinding.ActivityLoginBinding;
import com.ads.appgm.model.Login;
import com.ads.appgm.model.LoginResponse;
import com.ads.appgm.service.BackEndService;
import com.ads.appgm.service.HttpClient;
import com.ads.appgm.util.Animations;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MaskEditUtil;
import com.ads.appgm.util.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.getRoot());

        binding.editTextCPF.addTextChangedListener(MaskEditUtil.mask(binding.editTextCPF, MaskEditUtil.FORMAT_CPF));
        binding.editTextCPF.setOnFocusChangeListener(this::onFocusChange);

        binding.editTextPassword.setOnFocusChangeListener(this::onFocusChange);

        binding.buttonLogin.setOnClickListener(v -> {
            hideKeyboard(v);
            Animations.animateView(binding.loginProgress.getRoot(), View.VISIBLE, 0.5f, 250);
            enviarReqLogin();
        });
    }

    private void enviarReqLogin() {
        if (binding.editTextCPF.getText() == null ||
                binding.editTextPassword.getText() == null ||
                binding.editTextCPF.getText().toString().isEmpty() ||
                binding.editTextPassword.getText().toString().isEmpty()) {
            Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 100);
            Toast.makeText(getApplicationContext(), "Favor preencher CPF e senha", Toast.LENGTH_LONG).show();
            return;
        }
        BackEndService client = HttpClient.getInstance();
        Login login = new Login(
                binding.editTextCPF.getText().toString().replace(".", "").replace("-", ""),
                binding.editTextPassword.getText().toString()
        );
        Call<LoginResponse> loginResponse = client.loginRequest(login);
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

    Callback<LoginResponse> loginResponseCallback = new Callback<LoginResponse>() {
        @Override
        public void onResponse(Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
            if (!call.isCanceled()) {
                call.cancel();
            }
            if (!response.isSuccessful()) {
                runOnUiThread(() -> {
                    if (response.code() == 401) {
                        Toast.makeText(getApplicationContext(), "Erro de Autenticação", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Erro de Servidor", Toast.LENGTH_LONG).show();
                    }
                });
                Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 150);
                return;
            }
            LoginResponse loginResponse = response.body();
            if (loginResponse == null) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Erro de Cadastro", Toast.LENGTH_LONG).show();
                    Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 150);
                });
                return;
            }
            SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
            sp.edit().putString(Constants.USER_TOKEN, loginResponse.getToken())
                    .putLong(Constants.USER_ID, loginResponse.getId())
                    .putString(Constants.USER_NAME, loginResponse.getName())
                    .apply();
            Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 150);
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onFailure(Call<LoginResponse> call, Throwable t) {
            if (!call.isCanceled()) {
                call.cancel();
            }
            runOnUiThread(() -> {
                Animations.animateView(binding.loginProgress.getRoot(), View.GONE, 0f, 100);
                if (call.isExecuted()) {
                    Toast.makeText(getApplicationContext(), "Tempo expirado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Erro de rede", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
