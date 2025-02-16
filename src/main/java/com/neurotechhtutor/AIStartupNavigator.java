package com.neurotechhtutor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class AIStartupNavigator {
    private static final String API_KEY = "CyoG8Oxf6sfWePGWd7EUByzabLIFw1Sv";
    private static final String API_URL = "https://api.ai21.com/studio/v1/chat/completions";

    // ANSI escape codes for colorful output 🎨
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED = "\u001B[31m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(BLUE + "=======================================" + RESET);
        System.out.println("\uD83D\uDE80 " + YELLOW + "Welcome to AI Startup Navigator!" + RESET + " \uD83E\uDD16");
        System.out.println(BLUE + "=======================================" + RESET);
        System.out.println("\u2728 " + GREEN + "Get AI-driven insights for your startup journey!" + RESET);
        System.out.println("\uD83D\uDCCC Type " + RED + "'exit'" + RESET + " to quit anytime.\n");

        while (true) {
            System.out.print("\n\uD83E\uDDD1‍\uD83D\uDCBB " + CYAN + "Founder: " + RESET);
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("\n\uD83C\uDF89 " + GREEN + "Thank you for using AI Startup Navigator! See you next time! \uD83D\uDC4B" + RESET);
                break;
            }

            String aiResponse = getAIResponse(userInput);
            System.out.println("\n\uD83E\uDD16 " + YELLOW + "AI Advisor: " + RESET + aiResponse);
        }

        scanner.close();
    }

    private static String getAIResponse(String userInput) {
        try {
            JsonObject requestBody = Json.createObjectBuilder()
                .add("model", "jamba-instruct")
                .add("messages", Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("role", "user")
                        .add("content", "You are an AI startup mentor. Provide strategic advice on: " + userInput)))
                .add("max_tokens", 1000)
                .add("temperature", 0.7)
                .build();

            String json = requestBody.toString();
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    JsonReader jsonReader = Json.createReader(reader);
                    JsonObject responseJson = jsonReader.readObject();

                    JsonArray choices = responseJson.getJsonArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        JsonObject messageObj = choices.getJsonObject(0).getJsonObject("message");
                        if (messageObj != null && messageObj.containsKey("content")) {
                            return GREEN + "\n\u2705 " + messageObj.getString("content").trim() + RESET;
                        }
                    }
                }
            } else {
                return "\u26A1 " + RED + "Sorry, I couldn't process your request. Please try again." + RESET;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "\u26A0 " + RED + "An error occurred. Please try again." + RESET;
        }
        return "❌ " + RED + "No response received." + RESET;
    }
}