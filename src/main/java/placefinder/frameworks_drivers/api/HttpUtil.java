package placefinder.frameworks_drivers.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HttpUtil {

    public static String get(String urlStr) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent", "PlaceFinder/1.0");
            conn.setRequestProperty("Accept", "application/json");

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (is == null) {
                throw new IOException("No response from server, HTTP status " + status);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                if (status >= 200 && status < 300) {
                    return sb.toString();
                } else {
                    throw new IOException("HTTP " + status + ": " + sb);
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String post(String urlStr, String body) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);

            conn.setRequestProperty("User-Agent", "PlaceFinder/1.0");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Write request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input);
                os.flush();
            }

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (is == null) {
                throw new IOException("No response from server, HTTP status " + status);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                if (status >= 200 && status < 300) {
                    return sb.toString();
                } else {
                    throw new IOException("HTTP " + status + ": " + sb);
                }
            }

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}