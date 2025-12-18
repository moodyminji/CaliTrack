package com.moodyminji.calitrack;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingMetricsActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextInputEditText heightInput;
    private TextInputEditText currentWeightInput;
    private MaterialButton heightUnitToggle;
    private MaterialButton weightUnitToggle;
    private MaterialButton continueButton;

    private boolean isHeightInCm = true;
    private boolean isWeightInKg = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_metrics);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        heightInput = findViewById(R.id.heightInput);
        currentWeightInput = findViewById(R.id.currentWeightInput);
        heightUnitToggle = findViewById(R.id.heightUnitToggle);
        weightUnitToggle = findViewById(R.id.weightUnitToggle);
        continueButton = findViewById(R.id.continueButton);

        // Load saved data
        loadSavedData();

        // Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Height unit toggle
        heightUnitToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleHeightUnit();
            }
        });

        // Weight unit toggle
        weightUnitToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWeightUnit();
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

    private void toggleHeightUnit() {
        String currentValue = heightInput.getText().toString();

        if (!currentValue.isEmpty()) {
            float height = Float.parseFloat(currentValue);

            if (isHeightInCm) {
                // Convert cm to feet
                float feet = height / 30.48f;
                heightInput.setText(String.format("%.1f", feet));
                heightUnitToggle.setText("ft");
            } else {
                // Convert feet to cm
                float cm = height * 30.48f;
                heightInput.setText(String.format("%.0f", cm));
                heightUnitToggle.setText("cm");
            }
        } else {
            heightUnitToggle.setText(isHeightInCm ? "ft" : "cm");
        }

        isHeightInCm = !isHeightInCm;
    }

    private void toggleWeightUnit() {
        String currentValue = currentWeightInput.getText().toString();

        if (!currentValue.isEmpty()) {
            float weight = Float.parseFloat(currentValue);

            if (isWeightInKg) {
                // Convert kg to lbs
                float lbs = weight * 2.20462f;
                currentWeightInput.setText(String.format("%.1f", lbs));
                weightUnitToggle.setText("lbs");
            } else {
                // Convert lbs to kg
                float kg = weight / 2.20462f;
                currentWeightInput.setText(String.format("%.1f", kg));
                weightUnitToggle.setText("kg");
            }
        } else {
            weightUnitToggle.setText(isWeightInKg ? "lbs" : "kg");
        }

        isWeightInKg = !isWeightInKg;
    }

    private boolean validateInputs() {
        String heightStr = heightInput.getText().toString().trim();
        String weightStr = currentWeightInput.getText().toString().trim();

        if (heightStr.isEmpty()) {
            heightInput.setError("Please enter your height");
            heightInput.requestFocus();
            return false;
        }

        float height = Float.parseFloat(heightStr);
        if (isHeightInCm) {
            if (height < 100 || height > 250) {
                heightInput.setError("Please enter a valid height (100-250 cm)");
                return false;
            }
        } else {
            if (height < 3 || height > 8.5) {
                heightInput.setError("Please enter a valid height (3-8.5 ft)");
                return false;
            }
        }

        if (weightStr.isEmpty()) {
            currentWeightInput.setError("Please enter your weight");
            currentWeightInput.requestFocus();
            return false;
        }

        float weight = Float.parseFloat(weightStr);
        if (isWeightInKg) {
            if (weight < 30 || weight > 300) {
                currentWeightInput.setError("Please enter a valid weight (30-300 kg)");
                return false;
            }
        } else {
            if (weight < 66 || weight > 660) {
                currentWeightInput.setError("Please enter a valid weight (66-660 lbs)");
                return false;
            }
        }

        return true;
    }

    private void saveData() {
        float height = Float.parseFloat(heightInput.getText().toString().trim());
        float weight = Float.parseFloat(currentWeightInput.getText().toString().trim());

        // Convert to metric (cm and kg) for storage
        if (!isHeightInCm) {
            height = height * 30.48f; // feet to cm
        }
        if (!isWeightInKg) {
            weight = weight / 2.20462f; // lbs to kg
        }

        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("user_height", height);
        editor.putFloat("user_current_weight", weight);
        editor.apply();
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);

        float height = prefs.getFloat("user_height", 0);
        float weight = prefs.getFloat("user_current_weight", 0);

        if (height > 0) {
            heightInput.setText(String.format("%.0f", height));
        }

        if (weight > 0) {
            currentWeightInput.setText(String.format("%.1f", weight));
        }
    }

    private void navigateNext() {
        Intent intent = new Intent(OnboardingMetricsActivity.this,
                OnboardingGoalsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }
}