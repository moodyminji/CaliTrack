package com.moodyminji.calitrack;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingGoalsActivity extends AppCompatActivity {

    private ImageView backButton;
    private MaterialCardView loseWeightCard;
    private MaterialCardView maintainWeightCard;
    private MaterialCardView gainWeightCard;
    private MaterialRadioButton loseWeightRadio;
    private MaterialRadioButton maintainWeightRadio;
    private MaterialRadioButton gainWeightRadio;
    private TextInputEditText goalWeightInput;
    private Spinner activityLevelSpinner;
    private MaterialButton continueButton;

    private String selectedGoal = "lose"; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_goals);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        loseWeightCard = findViewById(R.id.loseWeightCard);
        maintainWeightCard = findViewById(R.id.maintainWeightCard);
        gainWeightCard = findViewById(R.id.gainWeightCard);
        loseWeightRadio = findViewById(R.id.loseWeightRadio);
        maintainWeightRadio = findViewById(R.id.maintainWeightRadio);
        gainWeightRadio = findViewById(R.id.gainWeightRadio);
        goalWeightInput = findViewById(R.id.goalWeightInput);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        continueButton = findViewById(R.id.continueButton);

        // Set up activity level spinner
        setupActivitySpinner();

        // Load saved data
        loadSavedData();

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Goal card clicks
        loseWeightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGoal("lose");
            }
        });

        maintainWeightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGoal("maintain");
            }
        });

        gainWeightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGoal("gain");
            }
        });

        // Continue button
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveData();
                    navigateNext();
                }
            }
        });
    }

    private void setupActivitySpinner() {
        String[] activityLevels = {
                "Sedentary - Little to no exercise",
                "Lightly Active - Exercise 1-3 days/week",
                "Moderately Active - Exercise 3-5 days/week",
                "Very Active - Exercise 6-7 days/week",
                "Extra Active - Physical job + exercise"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                activityLevels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(adapter);
    }

    private void selectGoal(String goal) {
        selectedGoal = goal;

        // Update radio buttons
        loseWeightRadio.setChecked(goal.equals("lose"));
        maintainWeightRadio.setChecked(goal.equals("maintain"));
        gainWeightRadio.setChecked(goal.equals("gain"));
    }

    private boolean validateInputs() {
        String goalWeightStr = goalWeightInput.getText().toString().trim();

        if (goalWeightStr.isEmpty()) {
            goalWeightInput.setError("Please enter your goal weight");
            goalWeightInput.requestFocus();
            return false;
        }

        float goalWeight = Float.parseFloat(goalWeightStr);
        if (goalWeight < 30 || goalWeight > 300) {
            goalWeightInput.setError("Please enter a valid weight (30-300 kg)");
            return false;
        }

        return true;
    }

    private void saveData() {
        float goalWeight = Float.parseFloat(goalWeightInput.getText().toString().trim());
        int activityLevel = activityLevelSpinner.getSelectedItemPosition();

        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_goal", selectedGoal);
        editor.putFloat("user_goal_weight", goalWeight);
        editor.putInt("user_activity_level", activityLevel);
        editor.apply();
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);

        String goal = prefs.getString("user_goal", "lose");
        float goalWeight = prefs.getFloat("user_goal_weight", 0);
        int activityLevel = prefs.getInt("user_activity_level", 2); // Default: Moderately Active

        selectGoal(goal);

        if (goalWeight > 0) {
            goalWeightInput.setText(String.format("%.1f", goalWeight));
        }

        activityLevelSpinner.setSelection(activityLevel);
    }

    private void navigateNext() {
        Intent intent = new Intent(OnboardingGoalsActivity.this,
                OnboardingCompleteActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }
}