package com.example.calitrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.moodyminji.calitrack.R;

public class TrackFragment extends Fragment {

    private TextInputEditText chatInput;
    private FloatingActionButton sendButton;
    private TextView caloriesValue;
    private TextView burnedCalories;
    private TextView proteinValue;
    private TextView carbsValue;
    private TextView fatsValue;
    private ProgressBar consumedProgress;
    private ProgressBar burnedProgress;

    // Sample data - replace with actual data later
    private int totalCalories = 789;
    private int netCalories = 555;
    private int goalCalories = 1500;
    private int burnedCals = 234;
    private int protein = 45;
    private int carbs = 98;
    private int fats = 32;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        chatInput = view.findViewById(R.id.chatInput);
        sendButton = view.findViewById(R.id.sendButton);
        caloriesValue = view.findViewById(R.id.caloriesValue);
        burnedCalories = view.findViewById(R.id.burnedCalories);
        proteinValue = view.findViewById(R.id.proteinValue);
        carbsValue = view.findViewById(R.id.carbsValue);
        fatsValue = view.findViewById(R.id.fatsValue);
        consumedProgress = view.findViewById(R.id.consumedProgress);
        burnedProgress = view.findViewById(R.id.burnedProgress);

        // Set up click listener for send button
        sendButton.setOnClickListener(v -> {
            String message = chatInput.getText().toString().trim();
            if (!message.isEmpty()) {
                handleUserMessage(message);
                chatInput.setText("");
            }
        });

        // Update UI with data
        updateUI();
    }

    private void updateUI() {
        // Update calorie values
        caloriesValue.setText(String.valueOf(totalCalories));
        burnedCalories.setText(String.valueOf(burnedCals));

        // Update macros
        proteinValue.setText(protein + "g");
        carbsValue.setText(carbs + "g");
        fatsValue.setText(fats + "g");

        // Calculate progress percentages
        int consumedPercent = (int) ((totalCalories / (float) goalCalories) * 100);
        int burnedPercent = (int) ((burnedCals / (float) goalCalories) * 100);

        // Update progress bars
        consumedProgress.setProgress(consumedPercent);
        burnedProgress.setProgress(burnedPercent);
    }

    private void handleUserMessage(String message) {
        // TODO: Implement AI assistant logic
        // This will connect to your backend/API for processing
        // For now, just log the message
        System.out.println("User message: " + message);
    }
}