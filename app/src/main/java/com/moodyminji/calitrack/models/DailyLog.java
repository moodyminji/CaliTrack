package com.moodyminji.calitrack.models;

import java.util.Date;

// Daily Log Model (represents one day of tracking)
public class DailyLog {
    private String id;
    private String userId;
    private String date; // Format: "yyyy-MM-dd"
    private int totalCalories;
    private int totalBurned;
    private float totalProtein;
    private float totalCarbs;
    private float totalFats;
    private int netCalories;
    private int calorieGoal;
    private long lastUpdated;

    // Empty constructor for Firestore
    public DailyLog() {}

    public DailyLog(String id, String userId, String date, int totalCalories,
                    int totalBurned, float totalProtein, float totalCarbs,
                    float totalFats, int calorieGoal) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalBurned = totalBurned;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFats = totalFats;
        this.netCalories = totalCalories - totalBurned;
        this.calorieGoal = calorieGoal;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getDate() { return date; }
    public int getTotalCalories() { return totalCalories; }
    public int getTotalBurned() { return totalBurned; }
    public float getTotalProtein() { return totalProtein; }
    public float getTotalCarbs() { return totalCarbs; }
    public float getTotalFats() { return totalFats; }
    public int getNetCalories() { return netCalories; }
    public int getCalorieGoal() { return calorieGoal; }
    public long getLastUpdated() { return lastUpdated; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setDate(String date) { this.date = date; }
    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
        this.netCalories = totalCalories - totalBurned;
    }
    public void setTotalBurned(int totalBurned) {
        this.totalBurned = totalBurned;
        this.netCalories = totalCalories - totalBurned;
    }
    public void setTotalProtein(float totalProtein) { this.totalProtein = totalProtein; }
    public void setTotalCarbs(float totalCarbs) { this.totalCarbs = totalCarbs; }
    public void setTotalFats(float totalFats) { this.totalFats = totalFats; }
    public void setNetCalories(int netCalories) { this.netCalories = netCalories; }
    public void setCalorieGoal(int calorieGoal) { this.calorieGoal = calorieGoal; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    // Helper method to get status
    public String getStatus() {
        int netCals = totalCalories - totalBurned;
        if (netCals < calorieGoal * 0.9) {
            return "Under goal";
        } else if (netCals > calorieGoal * 1.1) {
            return "Over goal";
        } else {
            return "On track";
        }
    }

    // Helper method to get progress percentage
    public int getProgressPercentage() {
        if (calorieGoal == 0) return 0;
        return Math.min(100, (int) ((netCalories / (float) calorieGoal) * 100));
    }
}

