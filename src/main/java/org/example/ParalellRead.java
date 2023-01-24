package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Stream;

public class ParalellRead {

  public static void main(String[] args) {
    ParalellRead paralellRead = new ParalellRead();
    paralellRead.solutionAsync("task", 8);
  }

  public void solutionAsync(String dirName, int coreSize) {
    // Get list of files
    List<String> resourceFileNames;
    resourceFileNames = getResourceFileNames(dirName);
    ConcurrentMap<String, Double> statistics = new ConcurrentHashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(coreSize);

    List<CompletableFuture<Void>> completableFutureList = new ArrayList<>(resourceFileNames.size());
    // Run tasks
    for (String fileName : resourceFileNames) {
      CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(
              () -> new ReadFileTask().start(fileName), executorService)
          .thenAcceptAsync(result -> addToMap(statistics, result), executorService);

      completableFutureList.add(completableFuture);
    }

    // Wait all till futer end
    CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]))
        .join();

    System.out.println(statistics);

  }

  private void addToMap(ConcurrentMap<String, Double> statictcs, Map<String, Double> result) {
    System.out.println("hello");
  }


  private List<String> getResourceFileNames(String path) {
    List<String> fileNames = new ArrayList<>();
    try {
      InputStream in = getClass().getClassLoader().getResourceAsStream(path);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
      String recourse;
      while ((recourse = bufferedReader.readLine()) != null) {
        fileNames.add(path + "/" + recourse);
      }
    }catch (Exception e) {

    }
    return fileNames;
  }
}
