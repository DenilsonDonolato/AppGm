package com.ads.appgm;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ads.appgm.util.MaskEditUtil;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText inputCPF = findViewById(R.id.editTextCPF);
        inputCPF.addTextChangedListener(MaskEditUtil.mask(inputCPF,MaskEditUtil.FORMAT_CPF));

        MaterialButton btnLogin = findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }
}
