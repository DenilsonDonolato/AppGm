package com.ads.appgm;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.appgm.databinding.ActivityLoginBinding;
import com.ads.appgm.util.MaskEditUtil;

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
            setResult(RESULT_OK);
            finish();
        });
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
