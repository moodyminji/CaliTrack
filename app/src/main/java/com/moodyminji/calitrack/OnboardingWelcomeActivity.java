package com.moodyminji.calitrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class OnboardingWelcomeActivity extends AppCompatActivity {

    private TextView skipButton;
    private MaterialButton getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_welcome);

        // Initialize views
        skipButton = findViewById(R.id.skipButton);
        getStartedButton = findViewById(R.id.getStartedButton);

        // Skip button click
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mark onboarding as skipped and go to login
                skipOnboarding();
            }
        });

        // Get Started button click
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to first step
                Intent intent = new Intent(OnboardingWelcomeActivity.this,
                        OnboardingPersonalActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
        });
    }

    private void skipOnboarding() {
        // Save default values
        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("onboarding_complete", true);
        editor.putInt("calorie_goal", 2000); // Default calorie goal
        editor.apply();

        // Go to login
        Intent intent = new Intent(OnboardingWelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Exit app from welcome screen
        finishAffinity();
    }
}