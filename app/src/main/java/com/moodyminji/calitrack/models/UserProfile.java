package com.moodyminji.calitrack.models;

class UserProfile {
    private String uid;
    private String name;
    private String email;
    private int age;
    private String gender;
    private float height; // cm
    private float currentWeight; // kg
    private float goalWeight; // kg
    private String goal; // "lose", "maintain", "gain"
    private int activityLevel; // 0-4
    private int calorieGoal;
    private long createdAt;
    private int daysActive;

    // Empty constructor for Firestore
    public UserProfile() {}

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(float currentWeight) { this.currentWeight = currentWeight; }

    public float getGoalWeight() { return goalWeight; }
    public void setGoalWeight(float goalWeight) { this.goalWeight = goalWeight; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public int getActivityLevel() { return activityLevel; }
    public void setActivityLevel(int activityLevel) { this.activityLevel = activityLevel; }

    public int getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(int calorieGoal) { this.calorieGoal = calorieGoal; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getDaysActive() { return daysActive; }
    public void setDaysActive(int daysActive) { this.daysActive = daysActive; }
}