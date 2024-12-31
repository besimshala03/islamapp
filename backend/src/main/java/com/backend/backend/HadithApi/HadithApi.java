package com.backend.backend.HadithApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HadithApi {

    private static final int TIMEOUT = 5000;

    // Holt den API-Schlüssel aus den Umgebungsvariablen
    private String getApiKey() {
        String apiKey = System.getenv("HADITH_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API Key not found. Set HADITH_API_KEY in environment variables.");
        }
        return apiKey;
    }

    // API-Anfrage durchführen und die Antwort als String zurückgeben
    private String fetchApiResponse(String apiUrl) throws IOException {
        HttpURLConnection connection = null;
        StringBuilder responseContent = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            int status = connection.getResponseCode();
            if (status != 200) {
                throw new IOException("Error: Received HTTP status code " + status);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseContent.toString();
    }

    // Bücher aus der API abrufen
    public String getBooks() {
        String apiUrl = "https://www.hadithapi.com/api/books?apiKey=" + getApiKey();

        try {
            String response = fetchApiResponse(apiUrl);
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("books")) {
                JSONArray books = jsonResponse.getJSONArray("books");
                return formatBooks(books);
            } else {
                return "Error: Books not found in API response.";
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    // Kapitel eines Buches abrufen
    public String getChapters(String bookSlug) {
        String apiUrl = "https://www.hadithapi.com/api/" + bookSlug + "/chapters?apiKey=" + getApiKey();

        try {
            String response = fetchApiResponse(apiUrl);
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("chapters")) {
                JSONArray chapters = jsonResponse.getJSONArray("chapters");
                return formatChapters(chapters);
            } else {
                return "Error: Chapters not found in API response.";
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    // Hadith mit Narrator und Text abrufen
    public String getHadith(String hadithEnglish, String book, String chapter) {
        String apiUrl = "https://www.hadithapi.com/api/hadiths/?apiKey=" + getApiKey()
                + "&hadithEnglish=" + hadithEnglish
                + "&book=" + book
                + "&chapter=" + chapter;

        try {
            String response = fetchApiResponse(apiUrl);
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("hadiths")) {
                JSONObject hadiths = jsonResponse.getJSONObject("hadiths");
                JSONArray hadithData = hadiths.getJSONArray("data");
                return formatHadiths(hadithData);
            } else {
                return "Error: Hadiths not found in API response.";
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    // Hilfsmethode zum Formatieren der Bücher
    private String formatBooks(JSONArray books) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < books.length(); i++) {
            JSONObject book = books.getJSONObject(i);
            String bookName = book.getString("bookName");
            result.append("Book: ").append(bookName).append("\n");
        }
        return result.toString().trim();
    }

    // Hilfsmethode zum Formatieren der Kapitel
    private String formatChapters(JSONArray chapters) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < chapters.length(); i++) {
            JSONObject chapter = chapters.getJSONObject(i);
            String chapterEnglish = chapter.getString("chapterEnglish");
            result.append("Chapter: ").append(chapterEnglish).append("\n");
        }
        return result.toString().trim();
    }

    // Hilfsmethode zum Formatieren der Hadiths
    private String formatHadiths(JSONArray hadithData) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < hadithData.length(); i++) {
            JSONObject hadith = hadithData.getJSONObject(i);
            String narrator = hadith.getString("englishNarrator");
            String text = hadith.getString("hadithEnglish");

            result.append("Narrator: ").append(narrator).append("\n");
            result.append("Hadith: ").append(text).append("\n\n");
        }
        return result.toString().trim();
    }

}