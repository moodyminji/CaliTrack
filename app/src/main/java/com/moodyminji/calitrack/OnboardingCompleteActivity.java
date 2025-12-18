package com.moodyminji.calitrack;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class OnboardingCompleteActivity extends AppCompatActivity {

    private TextView calorieGoalText;
    private TextView currentWeightText;
    private TextView goalWeightText;
    private MaterialButton startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_complete);

        // Initialize views
        calorieGoalText = findViewById(R.id.calorieGoalText);
        currentWeightText = findViewById(R.id.currentWeightText);
        goalWeightText = findViewById(R.id.goalWeightText);
        startButton = findViewById(R.id.startButton);

        // Calculate and display calorie goal
        calculateAndDisplayCalorieGoal();

        // Start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeOnboarding();
            }
        });
    }

    private void calculateAndDisplayCalorieGoal() {
        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);

        // Get user data
        int age = prefs.getInt("user_age", 25);
        String gender = prefs.getString("user_gender", "Male");
        float height = prefs.getFloat("user_height", 170); // cm
        float currentWeight = prefs.getFloat("user_current_weight", 70); // kg
        float goalWeight = prefs.getFloat("user_goal_weight", 70); // kg
        String goal = prefs.getString("user_goal", "maintain");
        int activityLevel = prefs.getInt("user_activity_level", 2);

        // Calculate BMR using Mifflin-St Jeor Equation
        float bmr;
        if (gender.equals("Male")) {
            bmr = (10 * currentWeight) + (6.25f * height) - (5 * age) + 5;
        } else {
            bmr = (10 * currentWeight) + (6.25f * height) - (5 * age) - 161;
        }

        // Apply activity multiplier
        float activityMultiplier;
        switch (activityLevel) {
            case 0: activityMultiplier = 1.2f; break;   // Sedentary
            case 1: activityMultiplier = 1.375f; break; // Lightly Active
            case 2: activityMultiplier = 1.55f; break;  // Moderately Active
            case 3: activityMultiplier = 1.725f; break; // Very Active
            case 4: activityMultiplier = 1.9f; break;   // Extra Active
            default: activityMultiplier = 1.55f;
        }

        float tdee = bmr * activityMultiplier;

        // Adjust for goal
        int calorieGoal;
        switch (goal) {
            case "lose":
                calorieGoal = Math.round(tdee - 500); // 500 cal deficit for ~1 lb/week loss
                break;
            case "gain":
                calorieGoal = Math.round(tdee + 500); // 500 cal surplus for ~1 lb/week gain
                break;
            case "maintain":
            default:
                calorieGoal = Math.round(tdee);
                break;
        }

        // Ensure calorie goal is reasonable
        if (calorieGoal < 1200) calorieGoal = 1200;
        if (calorieGoal > 4000) calorieGoal = 4000;

        // Save calorie goal
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("calorie_goal", calorieGoal);
        editor.apply();

        // Display values
        calorieGoalText.setText(String.format("%,d", calorieGoal));
        currentWeightText.setText(String.format("%.1f kg", currentWeight));
        goalWeightText.setText(String.format("%.1f kg", goalWeight));
    }

    private void completeOnboarding() {
        // Mark onboarding as complete
        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("onboarding_complete", true);
        editor.apply();

        // Navigate to login
        Intent intent = new Intent(OnboardingCompleteActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent going back from completion screen
        // User must complete onboarding
    }
}