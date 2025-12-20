package com.moodyminji.calitrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.moodyminji.calitrack.models.DailyLog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.moodyminji.calitrack.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private RecyclerView historyRecyclerView;
    private LinearLayout emptyStateLayout;
    private MaterialButton startTrackingButton;
    private ProgressBar loadingProgress;
    private TextView daysTrackedText;

    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyItems;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        startTrackingButton = view.findViewById(R.id.startTrackingButton);
        daysTrackedText = view.findViewById(R.id.daysTrackedText);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Set up RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyItems = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyItems);
        historyRecyclerView.setAdapter(historyAdapter);

        // Start tracking button
        startTrackingButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTrack();
            }
        });

        // Load history from Firestore
        if (currentUser != null) {
            loadHistoryFromFirestore();
        } else {
            showEmptyState();
        }
    }

    private void loadHistoryFromFirestore() {
        showLoading();

        // Get last 30 days of logs
        db.collection("users")
                .document(currentUser.getUid())
                .collection("dailyLogs")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyItems.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String date = document.getString("date");
                            int totalCalories = document.getLong("totalCalories").intValue();
                            int netCalories = document.getLong("netCalories").intValue();
                            int totalBurned = document.getLong("totalBurned").intValue();
                            int calorieGoal = document.getLong("calorieGoal").intValue();

                            // Calculate progress
                            int progress = calorieGoal > 0 ?
                                    Math.min(100, (int) ((netCalories / (float) calorieGoal) * 100)) : 0;

                            // Determine status
                            String status;
                            if (netCalories < calorieGoal * 0.9) {
                                status = "Under goal";
                            } else if (netCalories > calorieGoal * 1.1) {
                                status = "Over goal";
                            } else {
                                status = "On track";
                            }

                            // Format date label
                            String dateLabel = formatDateLabel(date);

                            historyItems.add(new HistoryItem(
                                    dateLabel,
                                    date,
                                    totalCalories,
                                    netCalories,
                                    totalBurned,
                                    status
                            ));
                        } catch (Exception e) {
                            // Skip invalid entries
                        }
                    }

                    if (historyItems.isEmpty()) {
                        showEmptyState();
                    } else {
                        showHistoryList();
                        daysTrackedText.setText(historyItems.size() + " days tracked");
                        historyAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error loading history: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    showEmptyState();
                });
    }

    private String formatDateLabel(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(sdf.parse(date));

            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);

            if (isSameDay(dateCalendar, today)) {
                return "Today";
            } else if (isSameDay(dateCalendar, yesterday)) {
                return "Yesterday";
            } else {
                // Format as "Monday, Jan 15"
                SimpleDateFormat labelFormat = new SimpleDateFormat("EEEE, MMM d", Locale.US);
                return labelFormat.format(dateCalendar.getTime());
            }
        } catch (Exception e) {
            return date;
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void showLoading() {
        emptyStateLayout.setVisibility(View.GONE);
        historyRecyclerView.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        historyRecyclerView.setVisibility(View.GONE);
    }

    private void showHistoryList() {
        emptyStateLayout.setVisibility(View.GONE);
        historyRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh history when user comes back to this tab
        if (currentUser != null) {
            loadHistoryFromFirestore();
        }
    }
}