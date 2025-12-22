package com.moodyminji.calitrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    // Profile info views
    private TextView profileInitial;
    private TextView userName;
    private TextView userEmail;
    private TextView daysActive;
    private TextView currentWeight;
    private TextView goalWeight;

    // Health stats views
    private TextView userAge;
    private TextView userHeight;
    private TextView weightGoalText;
    private TextView calorieGoalText;

    // Settings views
    private SwitchMaterial notificationsSwitch;
    private LinearLayout languageOption;
    private LinearLayout goalsOption;
    private LinearLayout privacyOption;
    private LinearLayout helpOption;

    // Account views
    private MaterialButton logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        profileInitial = view.findViewById(R.id.profileInitial);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        daysActive = view.findViewById(R.id.daysActive);
        currentWeight = view.findViewById(R.id.currentWeight);
        goalWeight = view.findViewById(R.id.goalWeight);

        userAge = view.findViewById(R.id.userAge);
        userHeight = view.findViewById(R.id.userHeight);
        weightGoalText = view.findViewById(R.id.weightGoalText);
        calorieGoalText = view.findViewById(R.id.calorieGoalText);

        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
        languageOption = view.findViewById(R.id.languageOption);
        goalsOption = view.findViewById(R.id.goalsOption);
        privacyOption = view.findViewById(R.id.privacyOption);
        helpOption = view.findViewById(R.id.helpOption);

        logoutButton = view.findViewById(R.id.logoutButton);

        // Load user data from Firestore
        loadUserProfile();

        // Set up click listeners
        setupClickListeners();
    }

    private void loadUserProfile() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set basic info
        userName.setText(currentUser.getDisplayName() != null ?
                currentUser.getDisplayName() : "User");
        userEmail.setText(currentUser.getEmail());

        String initial = currentUser.getDisplayName() != null &&
                !currentUser.getDisplayName().isEmpty() ?
                currentUser.getDisplayName().substring(0, 1).toUpperCase() : "U";
        profileInitial.setText(initial);

        // Load profile from Firestore
        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        updateUIFromFirestore(document);
                        saveToSharedPreferences(document);
                    } else {
                        loadFromSharedPreferences();
                    }
                })
                .addOnFailureListener(e -> {
                    loadFromSharedPreferences();
                });

        // Load days active
        loadDaysActive();
    }

    private void updateUIFromFirestore(DocumentSnapshot document) {
        try {
            // Age
            if (document.contains("age")) {
                int age = document.getLong("age").intValue();
                userAge.setText(age + " years");
            }

            // Height
            if (document.contains("height")) {
                float height = document.getDouble("height").floatValue();
                userHeight.setText((int) height + " cm");
            }

            // Current Weight
            if (document.contains("currentWeight")) {
                float weight = document.getDouble("currentWeight").floatValue();
                currentWeight.setText(String.format("%.1f", weight));
            }

            // Goal Weight
            if (document.contains("goalWeight")) {
                float gWeight = document.getDouble("goalWeight").floatValue();
                goalWeight.setText(String.format("%.1f", gWeight));

                // Calculate weight goal text
                float current = document.getDouble("currentWeight").floatValue();
                float diff = Math.abs(current - gWeight);
                String goalType = current > gWeight ? "Lose" : "Gain";
                weightGoalText.setText(String.format("%s %.1f kg", goalType, diff));
            }

            // Calorie Goal
            if (document.contains("calorieGoal")) {
                int calGoal = document.getLong("calorieGoal").intValue();
                calorieGoalText.setText(calGoal + " kcal");
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToSharedPreferences(DocumentSnapshot document) {
        try {
            SharedPreferences.Editor editor = requireContext().getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE).edit();
            if (document.contains("calorieGoal")) {
                editor.putInt("calorie_goal", document.getLong("calorieGoal").intValue());
            }
            if (document.contains("currentWeight")) {
                editor.putFloat("user_current_weight", document.getDouble("currentWeight").floatValue());
            }
            if (document.contains("goalWeight")) {
                editor.putFloat("user_goal_weight", document.getDouble("goalWeight").floatValue());
            }
            editor.apply();
        } catch (Exception e) {
            Log.e("ProfileFragment", "Error saving to SharedPreferences", e);
        }
    }

    private void loadFromSharedPreferences() {
        SharedPreferences prefs = requireContext().getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);

        int age = prefs.getInt("user_age", 0);
        if (age > 0) {
            userAge.setText(age + " years");
        }

        float height = prefs.getFloat("user_height", 0);
        if (height > 0) {
            userHeight.setText((int) height + " cm");
        }

        float current = prefs.getFloat("user_current_weight", 0);
        if (current > 0) {
            currentWeight.setText(String.format("%.1f", current));
        }

        float goal = prefs.getFloat("user_goal_weight", 0);
        if (goal > 0) {
            goalWeight.setText(String.format("%.1f", goal));

            float diff = Math.abs(current - goal);
            String goalType = current > goal ? "Lose" : "Gain";
            weightGoalText.setText(String.format("%s %.1f kg", goalType, diff));
        }

        int calGoal = prefs.getInt("calorie_goal", 2000);
        calorieGoalText.setText(calGoal + " kcal");
    }

    private void loadDaysActive() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("dailyLogs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int days = queryDocumentSnapshots.size();
                    daysActive.setText(String.valueOf(days));
                });
    }

    private void setupClickListeners() {
        // Notifications switch
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();

            String message = isChecked ? "Notifications enabled" : "Notifications disabled";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        // Load notification preference
        SharedPreferences prefs = requireContext().getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        notificationsSwitch.setChecked(prefs.getBoolean("notifications_enabled", true));

        // Language & Region
        languageOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Language settings coming soon", Toast.LENGTH_SHORT).show();
        });

        // Goals & Targets - Edit goals dialog
        goalsOption.setOnClickListener(v -> {
            showEditGoalsDialog();
        });

        // Privacy & Security
        privacyOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Privacy settings coming soon", Toast.LENGTH_SHORT).show();
        });

        // Help & Support
        helpOption.setOnClickListener(v -> {
            showHelpDialog();
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {
            showLogoutConfirmation();
        });
    }

    private void showEditGoalsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_goals, null);

        TextInputEditText goalWeightInput = dialogView.findViewById(R.id.goalWeightInput);
        TextInputEditText calorieGoalInput = dialogView.findViewById(R.id.calorieGoalInput);

        // Pre-fill current values
        SharedPreferences prefs = requireContext().getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        float currentGoalWeight = prefs.getFloat("user_goal_weight", 0);
        int currentCalorieGoal = prefs.getInt("calorie_goal", 2000);

        if (currentGoalWeight > 0) {
            goalWeightInput.setText(String.format("%.1f", currentGoalWeight));
        }
        calorieGoalInput.setText(String.valueOf(currentCalorieGoal));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Goals")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        float newGoalWeight = Float.parseFloat(goalWeightInput.getText().toString());
                        int newCalorieGoal = Integer.parseInt(calorieGoalInput.getText().toString());

                        // Save to SharedPreferences
                        prefs.edit()
                                .putFloat("user_goal_weight", newGoalWeight)
                                .putInt("calorie_goal", newCalorieGoal)
                                .apply();

                        // Update Firestore
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("goalWeight", newGoalWeight);
                        updates.put("calorieGoal", newCalorieGoal);

                        db.collection("users")
                                .document(currentUser.getUid())
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Goals updated!", Toast.LENGTH_SHORT).show();
                                    loadUserProfile();
                                });

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showHelpDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Help & Support")
                .setMessage("For questions or support, please contact us at 22f22893@mec.edu.om")
                .setPositiveButton("Sure!", null)
                .show();
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear login state
                    SharedPreferences prefs = requireContext().getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("is_logged_in", false).apply();

                    // Sign out from Firebase
                    mAuth.signOut();

                    // Go to Login Activity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
