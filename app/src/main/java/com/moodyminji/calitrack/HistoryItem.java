package com.moodyminji.calitrack;

public class HistoryItem {
    private String dateLabel;
    private int totalCalories;
    private int netCalories;
    private int burnedCalories;
    private String status;

    // Constructor
    public HistoryItem(String dateLabel, int totalCalories,
                       int netCalories, int burnedCalories, String status) {
        this.dateLabel = dateLabel;
        this.totalCalories = totalCalories;
        this.netCalories = netCalories;
        this.burnedCalories = burnedCalories;
        this.status = status;
    }

    // Getters
    public String getDateLabel() {
        return dateLabel;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public int getNetCalories() {
        return netCalories;
    }

    public int getBurnedCalories() {
        return burnedCalories;
    }

    public String getStatus() {
        return status;
    }

    // Setters (optional, if you need to update values)
    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public void setNetCalories(int netCalories) {
        this.netCalories = netCalories;
    }

    public void setBurnedCalories(int burnedCalories) {
        this.burnedCalories = burnedCalories;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}