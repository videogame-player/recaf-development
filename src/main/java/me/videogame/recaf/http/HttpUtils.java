package me.videogame.recaf.http;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    public static String get(String url) {
        StringBuilder builder = new StringBuilder();
        try (InputStream in = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String inputLine;
            while ((inputLine = rd.readLine()) != null) {
                builder.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
