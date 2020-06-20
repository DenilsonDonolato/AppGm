package com.ads.appgm;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.appgm.databinding.ActivityLoginBinding;
import com.ads.appgm.model.Login;
import com.ads.appgm.model.LoginResponse;
import com.ads.appgm.service.HttpClient;
import com.ads.appgm.util.MaskEditUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

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

        binding.editTextCPF.addTextChangedListener(MaskEditUtil.mask(binding.editTextCPF,MaskEditUtil.FORMAT_CPF));
        binding.editTextCPF.setOnFocusChangeListener(this::onFocusChange);

        binding.editTextPassword.setOnFocusChangeListener(this::onFocusChange);

        binding.buttonLogin.setOnClickListener(v -> {
            Handler handler = new Handler();
            handler.post(this::enviarReqLogin);
        });
    }

    private void enviarReqLogin() {
        OkHttpClient client = HttpClient.getInstance();

        Login login = new Login(
                binding.editTextCPF.getText().toString(),
                binding.editTextPassword.getText().toString()
        );

        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(login);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json, MediaType.parse(HttpClient.APPLICATION_JSON));
        Request request = new Request.Builder()
                .url(HttpClient.BASE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                String responseJson = response.body().string();
                LoginResponse loginResponse = new ObjectMapper().readValue(responseJson, LoginResponse.class);
                System.out.println(loginResponse.getToken());
                System.out.println(loginResponse.getNome());
                System.out.println(loginResponse.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
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
}
