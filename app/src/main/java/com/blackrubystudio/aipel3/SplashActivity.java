package com.blackrubystudio.aipel3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by jaewoo on 2017. 1. 20..
 */

public class SplashActivity extends AppCompatActivity {

    private static final String PREFERENCE = "preference";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check auth on Activity start
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            checkHasInfo();
        }else{
            Intent intent = new Intent(SplashActivity.this, WizardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkHasInfo(){
        SharedPreferences pref = this.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        boolean hasInfo = pref.getBoolean("hasInfo", false);

        Intent intent;

        if(hasInfo) {
            intent = new Intent(SplashActivity.this, ChatActivity.class);
        }else{
            intent = new Intent(SplashActivity.this, SurveyActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
