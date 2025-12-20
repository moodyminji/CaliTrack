package com.moodyminji.calitrack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.moodyminji.calitrack.BuildConfig;

import com.moodyminji.calitrack.R;
import com.moodyminji.calitrack.api.GeminiApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TrackFragment extends Fragment {

    private TextInputEditText chatInput;
    private FloatingActionButton sendButton;
    private TextView caloriesValue;
    private TextView greetingText;
    private TextView burnedCalories;
    private TextView proteinValue;
    private TextView carbsValue;
    private TextView fatsValue;
    private ProgressBar consumedProgress;
    private ProgressBar burnedProgress;

    private GeminiApiService geminiService;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // Daily totals
    private int totalCaloriesConsumed = 0;
    private int totalCaloriesBurned = 0;
    private float totalProtein = 0;
    private float totalCarbs = 0;
    private float totalFats = 0;
    private int calorieGoal = 2000;
    private float userWeight = 70; // Default weight in kg

    private Gson gson = new Gson();

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
        greetingText = view.findViewById(R.id.greetingText);


        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Gemini API
        // Get API key from BuildConfig
        String geminiKey = BuildConfig.GEMINI_API_KEY;

        if (geminiKey == null || geminiKey.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please add GEMINI_API_KEY to local.properties",
                    Toast.LENGTH_LONG).show();
            return;
        }

        geminiService = new GeminiApiService(geminiKey);

        // Load user data
        loadUserData();

        // Set up send button
        sendButton.setOnClickListener(v -> handleSendMessage());

        // Load today's data
        loadTodayData();

        // Update UI
        updateUI();
    }

    private void loadUserData() {
        SharedPreferences prefs = requireContext().getSharedPreferences("CaliTrackPrefs", MODE_PRIVATE);
        calorieGoal = prefs.getInt("calorie_goal", 2000);
        userWeight = prefs.getFloat("user_current_weight", 70);
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            if (userName != null && !userName.isEmpty()) {
                // If name has spaces, just take the first name
                String firstName = userName.split(" ")[0];
                greetingText.setText("Hello " + firstName + "! I'm your health assistant. How can I help you today?");
            }
        }

    }

    private void loadTodayData() {
        // TODO: Load from Firestore
        // For now, reset daily
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("dailyLogs")
                    .document(today)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            // Load existing data
                            totalCaloriesConsumed = document.getLong("totalCalories").intValue();
                            totalCaloriesBurned = document.getLong("totalBurned").intValue();
                            totalProtein = document.getDouble("totalProtein").floatValue();
                            totalCarbs = document.getDouble("totalCarbs").floatValue();
                            totalFats = document.getDouble("totalFats").floatValue();
                            updateUI();
                        }
                    });
        }
    }

    private void handleSendMessage() {
        String message = chatInput.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        }

        chatInput.setText("");
        chatInput.setEnabled(false);
        sendButton.setEnabled(false);

        // Show loading state
        Toast.makeText(getContext(), "Processing...", Toast.LENGTH_SHORT).show();

        // Send to Gemini AI
        geminiService.parseUserMessage(message, userWeight, new GeminiApiService.GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    chatInput.setEnabled(true);
                    sendButton.setEnabled(true);
                    processGeminiResponse(response);
                });
            }

            @Override
            public void onError(String error) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    chatInput.setEnabled(true);
                    sendButton.setEnabled(true);
                    Toast.makeText(getContext(),
                            "Error: " + error,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void processGeminiResponse(String jsonResponse) {
        try {
            JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);
            String type = response.get("type").getAsString();

            if (type.equals("food")) {
                processFoodResponse(response);
            } else if (type.equals("exercise")) {
                processExerciseResponse(response);
            } else if (type.equals("question")) {
                // General health question
                String answerText = response.get("response").getAsString();
                Toast.makeText(getContext(), answerText, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            // If not JSON, treat as text response
            Toast.makeText(getContext(),
                    jsonResponse.length() > 200 ? jsonResponse.substring(0, 200) + "..." : jsonResponse,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void processFoodResponse(JsonObject response) {
        try {
            int calories = response.get("totalCalories").getAsInt();
            float protein = response.has("totalProtein") ?
                    response.get("totalProtein").getAsFloat() : 0;
            float carbs = response.has("totalCarbs") ?
                    response.get("totalCarbs").getAsFloat() : 0;
            float fats = response.has("totalFat") ?
                    response.get("totalFat").getAsFloat() : 0;
            String responseMessage = response.get("response").getAsString();

            // Update totals
            totalCaloriesConsumed += calories;
            totalProtein += protein;
            totalCarbs += carbs;
            totalFats += fats;

            // Show response
            Toast.makeText(getContext(), responseMessage, Toast.LENGTH_LONG).show();

            // Update UI
            updateUI();

            // Save to Firestore
            saveMealToFirestore(response);

        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Logged successfully!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void processExerciseResponse(JsonObject response) {
        try {
            int caloriesBurned = response.get("totalCalories").getAsInt();
            String responseMessage = response.get("response").getAsString();

            // Update totals
            totalCaloriesBurned += caloriesBurned;

            // Show response
            Toast.makeText(getContext(), responseMessage, Toast.LENGTH_LONG).show();

            // Update UI
            updateUI();

            // Save to Firestore
            saveExerciseToFirestore(response);

        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Exercise logged!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        // Update calorie values
        caloriesValue.setText(String.valueOf(totalCaloriesConsumed));
        burnedCalories.setText(String.valueOf(totalCaloriesBurned));

        // Update macros
        proteinValue.setText(String.format("%.0fg", totalProtein));
        carbsValue.setText(String.format("%.0fg", totalCarbs));
        fatsValue.setText(String.format("%.0fg", totalFats));

        // Calculate progress percentages
        int consumedPercent = (int) ((totalCaloriesConsumed / (float) calorieGoal) * 100);
        int burnedPercent = (int) ((totalCaloriesBurned / (float) calorieGoal) * 100);

        // Update progress bars
        consumedProgress.setProgress(Math.min(consumedPercent, 100));
        burnedProgress.setProgress(Math.min(burnedPercent, 100));
    }

    private void saveMealToFirestore(JsonObject mealData) {
        if (currentUser == null) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        Map<String, Object> meal = new HashMap<>();
        meal.put("timestamp", System.currentTimeMillis());
        meal.put("data", mealData.toString());
        meal.put("type", "food");

        db.collection("users")
                .document(currentUser.getUid())
                .collection("dailyLogs")
                .document(today)
                .collection("meals")
                .add(meal);

        // Update daily totals
        updateDailyTotals(today);
    }

    private void saveExerciseToFirestore(JsonObject exerciseData) {
        if (currentUser == null) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        Map<String, Object> exercise = new HashMap<>();
        exercise.put("timestamp", System.currentTimeMillis());
        exercise.put("data", exerciseData.toString());
        exercise.put("type", "exercise");

        db.collection("users")
                .document(currentUser.getUid())
                .collection("dailyLogs")
                .document(today)
                .collection("exercises")
                .add(exercise);

        // Update daily totals
        updateDailyTotals(today);
    }

    private void updateDailyTotals(String date) {
        if (currentUser == null) return;

        Map<String, Object> totals = new HashMap<>();
        totals.put("totalCalories", totalCaloriesConsumed);
        totals.put("totalBurned", totalCaloriesBurned);
        totals.put("totalProtein", totalProtein);
        totals.put("totalCarbs", totalCarbs);
        totals.put("totalFats", totalFats);
        totals.put("netCalories", totalCaloriesConsumed - totalCaloriesBurned);
        totals.put("calorieGoal", calorieGoal);
        totals.put("date", date);
        totals.put("lastUpdated", System.currentTimeMillis());

        db.collection("users")
                .document(currentUser.getUid())
                .collection("dailyLogs")
                .document(date)
                .set(totals);
    }

    private void testGeminiAPI() {
        geminiService.getHealthAdvice("What is a healthy breakfast?",
                new GeminiApiService.GeminiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("TEST", "SUCCESS: " + response);
                        Toast.makeText(getContext(), "API Works!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("TEST", "ERROR: " + error);
                        Toast.makeText(getContext(), "ERROR: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}