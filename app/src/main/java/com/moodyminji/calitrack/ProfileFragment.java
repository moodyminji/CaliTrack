package com.moodyminji.calitrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

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

    // Sample user data - replace with actual data from database/API
    private String name = "Salim Ahmed";
    private String email = "salim@example.com";
    private int days = 24;
    private int current = 75;
    private int goal = 70;
    private int age = 28;
    private int height = 175;
    private int calorieGoal = 1500;

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

        // Initialize profile info views
        profileInitial = view.findViewById(R.id.profileInitial);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        daysActive = view.findViewById(R.id.daysActive);
        currentWeight = view.findViewById(R.id.currentWeight);
        goalWeight = view.findViewById(R.id.goalWeight);

        // Initialize health stats views
        userAge = view.findViewById(R.id.userAge);
        userHeight = view.findViewById(R.id.userHeight);
        weightGoalText = view.findViewById(R.id.weightGoalText);
        calorieGoalText = view.findViewById(R.id.calorieGoalText);

        // Initialize settings views
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
        languageOption = view.findViewById(R.id.languageOption);
        goalsOption = view.findViewById(R.id.goalsOption);
        privacyOption = view.findViewById(R.id.privacyOption);
        helpOption = view.findViewById(R.id.helpOption);

        // Initialize account views
        logoutButton = view.findViewById(R.id.logoutButton);

        // Load user data
        loadUserData();

        // Set up click listeners
        setupClickListeners();
    }

    private void loadUserData() {
        // TODO: Load from Firebase or local database
        // For now, use sample data

        // Set profile info
        profileInitial.setText(name.substring(0, 1).toUpperCase());
        userName.setText(name);
        userEmail.setText(email);
        daysActive.setText(String.valueOf(days));
        currentWeight.setText(String.valueOf(current));
        goalWeight.setText(String.valueOf(goal));

        // Set health stats
        userAge.setText(age + " years");
        userHeight.setText(height + " cm");

        int weightDiff = current - goal;
        String goalType = weightDiff > 0 ? "Lose" : "Gain";
        weightGoalText.setText(goalType + " " + Math.abs(weightDiff) + " kg");

        calorieGoalText.setText(calorieGoal + " kcal");
    }

    private void setupClickListeners() {
        // Notifications switch
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Save notification preference
            String message = isChecked ? "Notifications enabled" : "Notifications disabled";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        // Language & Region
        languageOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Language settings coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Open language settings dialog or activity
        });

        // Goals & Targets
        goalsOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Goals settings coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Open goals settings dialog or activity
        });

        // Privacy & Security
        privacyOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Privacy settings coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Open privacy settings dialog or activity
        });

        // Help & Support
        helpOption.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Help & Support coming soon", Toast.LENGTH_SHORT).show();
            // TODO: Open help center or support chat
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {
            handleLogout();
        });
    }

    private void handleLogout() {
        // TODO: Implement Firebase logout
        // TODO: Clear local data
        // TODO: Navigate to login screen

        Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();

        // For now, just show a message
        // Later, you'll add:
        // FirebaseAuth.getInstance().signOut();
        // Intent intent = new Intent(getActivity(), LoginActivity.class);
        // startActivity(intent);
        // getActivity().finish();
    }
}