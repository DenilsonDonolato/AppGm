package com.ads.appgm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.appgm.databinding.ActivityLoginBinding;
import com.ads.appgm.model.Login;
import com.ads.appgm.model.LoginResponse;
import com.ads.appgm.service.HttpClient;
import com.ads.appgm.util.Animations;
import com.ads.appgm.util.Constants;
import com.ads.appgm.util.MaskEditUtil;
import com.ads.appgm.util.SharedPreferenceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
            Animations.animateView(binding.loginProgress.getRoot(),View.VISIBLE,0.5f,250);
            enviarReqLogin();
        });
    }

    private void enviarReqLogin() {

        if (binding.editTextCPF.getText() == null ||
                binding.editTextPassword.getText() == null ||
                binding.editTextCPF.getText().toString().isEmpty() ||
                binding.editTextPassword.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Favor preencher CPF e senha", Toast.LENGTH_LONG).show();
            return;
        }

        OkHttpClient client = HttpClient.getLoginClient();
        Login login = new Login(
                binding.editTextCPF.getText().toString(),
                binding.editTextPassword.getText().toString()
        );

        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(login);
        } catch (JsonProcessingException e) {
            Toast.makeText(getApplicationContext(),"Erro ao ler CPF e senha",Toast.LENGTH_LONG).show();
            return;
        }

        RequestBody body = RequestBody.create(json, MediaType.parse(HttpClient.APPLICATION_JSON));
        Request request = new Request.Builder()
                .url(HttpClient.LOGIN_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(loginCallback);

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

    Callback loginCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            if (!call.isCanceled()) {
                call.cancel();
            }
            runOnUiThread(() -> {
                Animations.animateView(binding.loginProgress.getRoot(),View.GONE,0f,100);
                Toast.makeText(getApplicationContext(), "Erro de rede: "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            });
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            if (!call.isCanceled()) {
                call.cancel();
            }
            if (!response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Erro de Autenticação", Toast.LENGTH_SHORT).show();
                });
                Animations.animateView(binding.loginProgress.getRoot(),View.GONE,0f,150);
                return;
            }
            LoginResponse loginResponse = new ObjectMapper().readValue(response.body().string(), LoginResponse.class);
            SharedPreferences sp = SharedPreferenceUtil.getSharedePreferences();
            sp.edit().putString(Constants.USER_TOKEN, loginResponse.getToken())
                    .putLong(Constants.USER_ID, loginResponse.getId())
                    .putString(Constants.USER_NAME, loginResponse.getNome())
                    .apply();
            setResult(RESULT_OK);
            Animations.animateView(binding.loginProgress.getRoot(),View.GONE,0f,150);
            finish();
        }
    };
}
