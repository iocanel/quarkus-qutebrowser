package org.acme.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public final class ExecUtil {

  private ExecUtil() {
    // Utility class
  }

  public static String run(String... cmd) {
    try {
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      pb.redirectOutput();
      Process process = pb.start();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        return reader.lines().collect(Collectors.joining("\n")).trim();
      }
    } catch (Exception e) {
      throw new RuntimeException("Error executing command: " + e.getMessage(), e);
    }
  }

  /**
   * Run the specified qutebrowser command.
   * @param command the command to run
   */
  public static String qutebrowserCommand(String command) {
    return run("qutebrowser", command);
  }

  /**
   * Run the specified qutebrowser userscript or command (as userscript).
   * Commands that are run as userscripts are being passed the qutebrowser environment variables.
   * @param userScriptOrCommand the userscript or command to run
   */
  public static void qutebrowserUserscript(String userScriptOrCommand) {
    qutebrowserCommand(":spawn --userscript " + userScriptOrCommand);
  }


  /**
   * Check if command is run as userscript in qutebrowser.
   */
  public static boolean isInBrowser() {
    return System.getenv("QUTE_TEXT") != null;
  }

  /**
   * Retry the current command inside qutebrowser as a userscript.
   */
  public static void retryInBrowser() {
    qutebrowserUserscript(CommandLineUtil.getFullCommandLine());
  }
}

