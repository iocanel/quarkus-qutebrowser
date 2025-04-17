package org.acme.utils;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public final class WaitUtil {

  public static void waitForFile(Path path, long amount, TimeUnit timeUnit) {
    long waitTime = timeUnit.toMillis(amount);
    long startTime = System.currentTimeMillis();
    long endTime = startTime + waitTime;

    while (System.currentTimeMillis() < endTime) {
      if (path.toFile().exists()) {
        return;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Thread interrupted while waiting for file: " + path, e);
      }
    }
    throw new RuntimeException("Timeout waiting for file: " + path);
  }
}

