package com.ads.appgm;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.appgm.util.MaskEditUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputEditText inputCPF = findViewById(R.id.editTextCPF);
        inputCPF.addTextChangedListener(MaskEditUtil.mask(inputCPF,MaskEditUtil.FORMAT_CPF));
        inputCPF.setOnFocusChangeListener(this::onFocusChange);

        TextInputEditText inputPassword = findViewById(R.id.editTextPassword);
        inputPassword.setOnFocusChangeListener(this::onFocusChange);


        MaterialButton btnLogin = findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(v -> {
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
}
