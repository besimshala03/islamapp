package com.backend.backend.QuranApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class QuranApi {

    // Methode zum Abrufen des Ayah-Texts
    public String getQuranAyah(String reference, String edition) {
        String apiUrl = "http://api.alquran.cloud/v1/ayah/" + reference + "/" + edition;
        HttpURLConnection connection = null;
        StringBuilder responseContent = new StringBuilder();

        try {
            // API-URL initialisieren
            URL url = new URL(apiUrl);

            // Verbindung öffnen
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Überprüfen des Response-Codes
            int status = connection.getResponseCode();
            if (status == 200) { // Erfolgreiche Antwort
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                return "Error: Received HTTP status code " + status;
            }

            // JSON-Parsing
            JSONObject jsonResponse = new JSONObject(responseContent.toString());
            if (jsonResponse.has("data")) {
                return jsonResponse.getJSONObject("data").getString("text");
            } else {
                return "Error: Unable to retrieve text from API response.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Hauptmethode zum Testen
    public static void main(String[] args) {
        QuranApi api = new QuranApi();
        String response = api.getQuranAyah("1:3", "de"); // Beispiel: Ayat Al Kursi in englischer Übersetzung
        System.out.println(response); // Gibt nur den Text der Ayah aus
    }
}