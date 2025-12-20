package com.moodyminji.calitrack.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiApiService {

    private static final String TAG = "GeminiApiService";

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent";
    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;

    public GeminiApiService(String apiKey) {
        this.apiKey = apiKey;
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Log for debugging
        Log.d(TAG, "GeminiApiService initialized");
        Log.d(TAG, "API Key length: " + (apiKey != null ? apiKey.length() : 0));
    }

    // Interface for callback
    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(String error);
    }


    public void parseUserMessage(String userMessage, float userWeight, GeminiCallback callback) {
        String systemPrompt = buildEnhancedSystemPrompt(userWeight);
        String fullPrompt = systemPrompt + "\n\nUser message: \"" + userMessage + "\"\n\n" +
                "Analyze this message and respond ONLY with valid JSON (no markdown, no code blocks):\n" +
                "{\n" +
                "  \"type\": \"food\" or \"exercise\" or \"question\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"name\": \"item name\",\n" +
                "      \"quantity\": number,\n" +
                "      \"unit\": \"unit (pieces, grams, cups, etc.)\",\n" +
                "      \"calories\": estimated_calories,\n" +
                "      \"protein\": grams,\n" +
                "      \"carbs\": grams,\n" +
                "      \"fat\": grams\n" +
                "    }\n" +
                "  ],\n" +
                "  \"totalCalories\": total,\n" +
                "  \"totalProtein\": total_protein_grams,\n" +
                "  \"totalCarbs\": total_carbs_grams,\n" +
                "  \"totalFat\": total_fat_grams,\n" +
                "  \"response\": \"friendly confirmation message\"\n" +
                "}";

        sendRequest(fullPrompt, callback);
    }

    /**
     * Get general health advice from Gemini
     */
    public void getHealthAdvice(String question, GeminiCallback callback) {
        String prompt = "You are CaliTrack AI, a friendly and knowledgeable health assistant. " +
                "Provide helpful, evidence-based advice. Keep responses concise (2-4 sentences). " +
                "Be encouraging and supportive. Question: " + question;

        sendRequest(prompt, callback);
    }

    /**
     * Get meal suggestions based on calorie goals
     */
    public void getMealSuggestions(int calorieGoal, String mealType, GeminiCallback callback) {
        String prompt = String.format(
                "Suggest 3 healthy %s meals that are approximately %d calories each. " +
                        "Format as JSON: {\"meals\": [{\"name\": \"meal name\", \"calories\": number, " +
                        "\"description\": \"brief description\"}], \"response\": \"encouraging message\"}",
                mealType, calorieGoal / 3
        );

        sendRequest(prompt, callback);
    }

    /**
     * Analyze daily nutrition and provide insights
     */
    public void analyzeDailyNutrition(int caloriesConsumed, int caloriesBurned,
                                      int calorieGoal, GeminiCallback callback) {
        String prompt = String.format(
                "Analyze this daily nutrition data:\n" +
                        "- Consumed: %d calories\n" +
                        "- Burned: %d calories\n" +
                        "- Goal: %d calories\n" +
                        "- Net: %d calories\n\n" +
                        "Provide brief feedback (2-3 sentences) and one tip to reach the goal. " +
                        "Be encouraging and specific.",
                caloriesConsumed, caloriesBurned, calorieGoal,
                caloriesConsumed - caloriesBurned
        );

        sendRequest(prompt, callback);
    }

    private String buildEnhancedSystemPrompt(float userWeight) {
        return "You are CaliTrack AI, an expert nutrition and fitness assistant with deep knowledge of:\n" +
                "- Food nutrition (calories, macros) based on USDA database standards\n" +
                "- Exercise science and MET (Metabolic Equivalent) values\n" +
                "- Portion sizes and serving measurements\n\n" +

                "User's body weight: " + userWeight + " kg\n\n" +

                "When analyzing user messages:\n\n" +

                "FOR FOOD ITEMS:\n" +
                "1. Identify all food items mentioned\n" +
                "2. Extract quantities (if not specified, assume 1 medium serving)\n" +
                "3. Provide REALISTIC calorie estimates based on:\n" +
                "   - Standard USDA nutrition values\n" +
                "   - Typical portion sizes\n" +
                "   - Cooking methods (grilled vs fried adds 50-100 cal)\n" +
                "4. Include macro breakdown (protein, carbs, fat)\n" +
                "5. Examples:\n" +
                "   - '2 eggs' = 140-160 cal (12g protein, 1g carbs, 10g fat)\n" +
                "   - 'chicken breast 150g' = 165 cal (31g protein, 0g carbs, 3.6g fat)\n" +
                "   - 'rice bowl' = 200-250 cal (4g protein, 45g carbs, 0.5g fat)\n" +
                "   - 'apple' = 95 cal (0.5g protein, 25g carbs, 0.3g fat)\n" +
                "   - 'slice of pizza' = 285 cal (12g protein, 36g carbs, 10g fat)\n\n" +

                "FOR EXERCISE:\n" +
                "1. Calculate calories burned using MET values:\n" +
                "   Calories = MET × weight(kg) × duration(hours)\n" +
                "2. Common MET values:\n" +
                "   - Walking (casual): 3.5 MET\n" +
                "   - Walking (brisk): 4.5 MET\n" +
                "   - Jogging: 7.0 MET\n" +
                "   - Running (6 mph): 10.0 MET\n" +
                "   - Cycling (moderate): 8.0 MET\n" +
                "   - Swimming: 6.0 MET\n" +
                "   - Weight training: 5.0 MET\n" +
                "   - Yoga: 3.0 MET\n" +
                "   - Dancing: 4.5 MET\n" +
                "3. Show calculation in response\n\n" +

                "FOR GENERAL QUESTIONS:\n" +
                "- Provide helpful, evidence-based advice\n" +
                "- Be encouraging and supportive\n" +
                "- Keep responses concise (2-4 sentences)\n\n" +

                "ALWAYS respond with accurate estimates based on nutritional science. " +
                "Be conversational and encouraging!";
    }

    private void sendRequest(String prompt, GeminiCallback callback) {
        try {
            // Build request JSON
            JsonObject requestJson = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();

            part.addProperty("text", prompt);
            parts.add(part);
            content.add("parts", parts);
            contents.add(content);
            requestJson.add("contents", contents);

            // Add generation config for better JSON responses
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.3);
            generationConfig.addProperty("topP", 0.8);
            generationConfig.addProperty("topK", 40);
            generationConfig.addProperty("maxOutputTokens", 2048);
            requestJson.add("generationConfig", generationConfig);

            String jsonBody = gson.toJson(requestJson);

            // IMPORTANT: API key goes in the URL as query parameter
            String url = BASE_URL + "?key=" + apiKey;

            Log.d(TAG, "Request URL: " + BASE_URL + "?key=***");
            Log.d(TAG, "Request body length: " + jsonBody.length());

            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Execute async
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request failed", e);
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Response message: " + response.message());

                    if (!response.isSuccessful()) {
                        String error = "HTTP " + response.code() + ": " + response.message();
                        Log.e(TAG, "Error response: " + responseBody);

                        // Parse error message if available
                        try {
                            JsonObject errorJson = gson.fromJson(responseBody, JsonObject.class);
                            if (errorJson.has("error")) {
                                JsonObject errorObj = errorJson.getAsJsonObject("error");
                                if (errorObj.has("message")) {
                                    error = errorObj.get("message").getAsString();
                                }
                            }
                        } catch (Exception e) {
                            // Use default error
                        }

                        callback.onError(error);
                        return;
                    }

                    try {
                        Log.d(TAG, "Raw response: " + responseBody);

                        // Parse Gemini response
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                        // Check for blocked content
                        if (jsonResponse.has("promptFeedback")) {
                            JsonObject feedback = jsonResponse.getAsJsonObject("promptFeedback");
                            if (feedback.has("blockReason")) {
                                callback.onError("Content was blocked. Please try rephrasing.");
                                return;
                            }
                        }

                        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

                        if (candidates != null && candidates.size() > 0) {
                            JsonObject candidate = candidates.get(0).getAsJsonObject();
                            JsonObject contentObj = candidate.getAsJsonObject("content");
                            JsonArray partsArray = contentObj.getAsJsonArray("parts");

                            if (partsArray != null && partsArray.size() > 0) {
                                JsonObject partObj = partsArray.get(0).getAsJsonObject();
                                String text = partObj.get("text").getAsString();

                                // Clean up response (remove markdown code blocks if present)
                                text = cleanJsonResponse(text);

                                Log.d(TAG, "Cleaned response: " + text);
                                callback.onSuccess(text);
                            } else {
                                callback.onError("Empty response from AI");
                            }
                        } else {
                            callback.onError("No candidates in response");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error building request", e);
            callback.onError("Error: " + e.getMessage());
        }
    }

    /**
     * Clean JSON response from Gemini (remove markdown formatting)
     */
    private String cleanJsonResponse(String text) {
        text = text.trim();

        // Remove ```json and ``` markers
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }

        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }

        return text.trim();
    }
}