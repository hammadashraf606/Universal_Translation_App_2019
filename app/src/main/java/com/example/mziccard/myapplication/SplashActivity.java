package com.example.mziccard.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SaveSharedPreference.getThemeType(this).equals("DARK"))
            setContentView(R.layout.activity_splash_dark);
        else
            setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.getProgressDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);

        Thread thread = new Thread() {

            public void run() {

                try {
                    for (int i = 0; i < 100; i++) {
                        progressBar.setProgress(i);
                        sleep(20);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // InterstitialAdmob();
//                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                    startActivity(intent);
                    Intent n = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(n);
                    finish();
                }
            }
        };
        thread.start();

    }

}

