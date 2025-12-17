package com.moodyminji.calitrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.moodyminji.calitrack.MainActivity;
import com.moodyminji.calitrack.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView historyRecyclerView;
    private LinearLayout emptyStateLayout;
    private MaterialButton startTrackingButton;
    private TextView daysTrackedText;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyItems;

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

        // Set up RecyclerView
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load history data
        loadHistoryData();

        // Set up adapter
        historyAdapter = new HistoryAdapter(historyItems);
        historyRecyclerView.setAdapter(historyAdapter);

        // Update days tracked text
        daysTrackedText.setText(historyItems.size() + " days tracked");

        // Handle empty state
        if (historyItems.isEmpty()) {
            showEmptyState();
        } else {
            showHistoryList();
        }

        // Start tracking button click
        startTrackingButton.setOnClickListener(v -> {
            // Navigate to Track tab
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTrack();
            }
        });
    }

    private void loadHistoryData() {
        // TODO: Load from database or API
        // For now, create sample data matching the design
        historyItems = new ArrayList<>();

        historyItems.add(new HistoryItem(
                "Thursday, Nov 13",
                1234,
                889,
                345,
                "Under goal"
        ));

        historyItems.add(new HistoryItem(
                "Wednesday, Nov 12",
                1456,
                1176,
                280,
                "Under goal"
        ));

        historyItems.add(new HistoryItem(
                "Tuesday, Nov 11",
                1598,
                1186,
                412,
                "Under goal"
        ));

        historyItems.add(new HistoryItem(
                "Monday, Nov 10",
                1345,
                1047,
                298,
                "Under goal"
        ));

        historyItems.add(new HistoryItem(
                "Sunday, Nov 9",
                1423,
                1058,
                365,
                "Under goal"
        ));
    }

    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        historyRecyclerView.setVisibility(View.GONE);
    }

    private void showHistoryList() {
        emptyStateLayout.setVisibility(View.GONE);
        historyRecyclerView.setVisibility(View.VISIBLE);
    }
}