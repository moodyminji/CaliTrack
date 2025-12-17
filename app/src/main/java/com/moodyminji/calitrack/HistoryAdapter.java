package com.moodyminji.calitrack;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> historyItems;

    public HistoryAdapter(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView dateText;
        private TextView calorieDetails;
        private TextView totalCalories;
        private TextView statusText;
        private MaterialCardView iconCard;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            calorieDetails = itemView.findViewById(R.id.calorieDetails);
            totalCalories = itemView.findViewById(R.id.totalCalories);
            statusText = itemView.findViewById(R.id.statusText);
            iconCard = itemView.findViewById(R.id.iconCard);
        }

        public void bind(HistoryItem item) {
            dateText.setText(item.getDateLabel());
            totalCalories.setText(String.valueOf(item.getTotalCalories()));
            statusText.setText(item.getStatus());

            // Format calorie details text
            String details = item.getNetCalories() + " net calories • " +
                    item.getBurnedCalories() + " burned";
            calorieDetails.setText(details);

            // Set icon background color based on status
            if (item.getStatus().equals("Under goal")) {
                iconCard.setCardBackgroundColor(Color.parseColor("#ECFDF5"));
            } else if (item.getStatus().equals("Over goal")) {
                iconCard.setCardBackgroundColor(Color.parseColor("#FFF7ED"));
            } else {
                iconCard.setCardBackgroundColor(Color.parseColor("#F0FDFA"));
            }
        }
    }
}