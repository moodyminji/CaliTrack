package com.moodyminji.calitrack;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingPersonalActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextInputEditText nameInput;
    private TextInputEditText ageInput;
    private MaterialButtonToggleGroup genderToggle;
    private MaterialButton maleButton;
    private MaterialButton femaleButton;
    private MaterialButton otherButton;
    private MaterialButton continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_personal);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        genderToggle = findViewById(R.id.genderToggle);
        maleButton = findViewById(R.id.maleButton);
        femaleButton = findViewById(R.id.femaleButton);
        otherButton = findViewById(R.id.otherButton);
        continueButton = findViewById(R.id.continueButton);

        // Load saved data if exists
        loadSavedData();

        // Back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Continue button click
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

    private boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Please enter your name");
            nameInput.requestFocus();
            return false;
        }

        if (ageStr.isEmpty()) {
            ageInput.setError("Please enter your age");
            ageInput.requestFocus();
            return false;
        }

        int age = Integer.parseInt(ageStr);
        if (age < 13 || age > 120) {
            ageInput.setError("Please enter a valid age (13-120)");
            ageInput.requestFocus();
            return false;
        }

        if (genderToggle.getCheckedButtonId() == View.NO_ID) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveData() {
        String name = nameInput.getText().toString().trim();
        int age = Integer.parseInt(ageInput.getText().toString().trim());

        String gender = "Other";
        int selectedId = genderToggle.getCheckedButtonId();
        if (selectedId == R.id.maleButton) {
            gender = "Male";
        } else if (selectedId == R.id.femaleButton) {
            gender = "Female";
        }

        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", name);
        editor.putInt("user_age", age);
        editor.putString("user_gender", gender);
        editor.apply();
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);

        String name = prefs.getString("user_name", "");
        int age = prefs.getInt("user_age", 0);
        String gender = prefs.getString("user_gender", "");

        if (!name.isEmpty()) {
            nameInput.setText(name);
        }

        if (age > 0) {
            ageInput.setText(String.valueOf(age));
        }

        if (gender.equals("Male")) {
            genderToggle.check(R.id.maleButton);
        } else if (gender.equals("Female")) {
            genderToggle.check(R.id.femaleButton);
        } else if (gender.equals("Other")) {
            genderToggle.check(R.id.otherButton);
        }
    }

    private void navigateNext() {
        Intent intent = new Intent(OnboardingPersonalActivity.this,
                OnboardingMetricsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }
}