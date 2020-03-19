package com.ads.appgm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int LOGIN_INTENT_REQUEST = 1;
    boolean validLogin;
    private final long splashTimeOut = 500L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //TODO validacao de Login
        validLogin = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Handler myHandler = new Handler();
        myHandler.postDelayed(this::startApp, splashTimeOut);
    }

    private void startApp() {
        if(validLogin){
            goToMainActivity();
        } else {
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent,LOGIN_INTENT_REQUEST);
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
            case LOGIN_INTENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    goToMainActivity();
                } else {
                    finish();
                }
                break;
            default:
                goToLoginActivity();
                break;
        }
    }
}
