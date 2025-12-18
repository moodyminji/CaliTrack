package com.moodyminji.calitrack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds
    private MaterialCardView logoCard;
    private View appName;
    private View tagline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        logoCard = findViewById(R.id.logoCard);
        appName = findViewById(R.id.appName);
        tagline = findViewById(R.id.tagline);

        // Start animations
        startAnimations();

        // Navigate after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, SPLASH_DURATION);
    }

    private void startAnimations() {
        // Logo scale animation
        logoCard.setScaleX(0f);
        logoCard.setScaleY(0f);
        logoCard.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // App name fade in
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appName.animate()
                        .alpha(1f)
                        .setDuration(600)
                        .start();
            }
        }, 400);

        // Tagline fade in
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tagline.animate()
                        .alpha(1f)
                        .setDuration(600)
                        .start();
            }
        }, 800);
    }

    private void navigateToNextScreen() {
        // Check if user has completed onboarding
        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        boolean isOnboardingComplete = prefs.getBoolean("onboarding_complete", false);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        Intent intent;

        if (!isOnboardingComplete) {
            // First time user - go to onboarding
            intent = new Intent(SplashActivity.this, OnboardingWelcomeActivity.class);
        } else if (!isLoggedIn) {
            // Onboarding done but not logged in - go to login
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        } else {
            // User is logged in - go to main app
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finish(); // Close splash screen

        // Smooth transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}