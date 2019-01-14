package com.bestoffers.elnaqeltask.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bestoffers.elnaqeltask.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent homeIntent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(homeIntent);
                }
            }
        };

        timer.start();
    }
}
