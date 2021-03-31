package me.videogame.recaf.http;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static String getGithubRepositoryLatestTag(String repo) {
        String REPO_URL = "https://api.github.com/repos/" + repo + "/tags";
        String json = get(REPO_URL);
        JsonArray array = Json.parse(json).asArray();
        JsonObject latest = array.get(0).asObject();
        return latest.get("name").asString();
    }
}
