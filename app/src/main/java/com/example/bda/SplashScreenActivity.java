package com.example.bda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView logo;
    private TextView tittle,slogon;
    Animation topAnimation,bottomAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Splash Screen Will Cover Entire Screen;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
        logo = findViewById(R.id.logo);
        tittle = findViewById(R.id.title);
        slogon = findViewById(R.id.slogan);
        // Setting-up Animation To Image Views And TextViews On Splash Screen;
        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animmation);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animmation);
        logo.setAnimation(topAnimation);
        tittle.setAnimation(bottomAnimation);
        slogon.setAnimation(bottomAnimation);
        // jump to Another Activity Now;
        int SPLASH_SCREEN = 4300;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}