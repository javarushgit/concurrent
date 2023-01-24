package org.example;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ReadFileTask {

  public Map<String, Double> start(String path) {
    Map<String, Double> statistics = new HashMap<>();
    String fullPath = "src/main/resources/" + path;
    try (InputStream inputStream = Files.newInputStream(Path.of(fullPath));
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream))) {
      jsonReader.beginArray();
      while (jsonReader.hasNext()) {
        Fine fine = new Gson().fromJson(jsonReader, Fine.class);
        calculateStatistics(statistics, fine.getType(), fine.getFineAmount());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return statistics;
  }

  public void calculateStatistics(Map<String, Double> statistics, String type, Double amount) {
    if(statistics.containsKey(type)) {
      statistics.put(type, statistics.get(type) + amount);
    } else {
      statistics.put(type, amount);
    }
  }
}