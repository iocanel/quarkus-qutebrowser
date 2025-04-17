package org.acme.tabs;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import static org.acme.utils.ExecUtil.qutebrowserCommand;
import static org.acme.utils.WaitUtil.waitForFile;

import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Command(name = "list", description = "List qutebrowser windows and tabs", mixinStandardHelpOptions = true)
public class ListCommand implements Callable<Integer> {

  private static final String SESSION_NAME = "quarkus";
  private static final String SESSION_FILE_NAME = "quarkus.yml";

  @Spec
  protected CommandSpec spec;

  @SuppressWarnings("unchecked")
  @Override
  public Integer call() throws Exception {

    Path outputFile = Paths.get(System.getProperty("java.io.tmpdir"), "qutebrowser", "tabs");
    Path sessionFile = getDataHome().resolve("qutebrowser").resolve("sessions").resolve(SESSION_FILE_NAME);
    try {
      Files.createDirectories(outputFile.getParent());
      System.out.println(qutebrowserCommand(":session-save " + SESSION_NAME));
      waitForFile(sessionFile, 5, TimeUnit.SECONDS);

      if (!Files.exists(sessionFile)) {
        System.err.println("Session file not found: " + sessionFile);
        return ExitCode.SOFTWARE;
      }

      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      Map<?, ?> sessionData = mapper.readValue(sessionFile.toFile(), Map.class);

      List<Map<String, Object>> windows = (List<Map<String, Object>>) sessionData.get("windows");
      for (int winIndex = 0; winIndex < windows.size(); winIndex++) {
        List<Map<String, Object>> tabs = (List<Map<String, Object>>) windows.get(winIndex).get("tabs");
        for (int tabIndex = 0; tabIndex < tabs.size(); tabIndex++) {
          List<Map<String, Object>> history = (List<Map<String, Object>>) tabs.get(tabIndex).get("history");
          if (!history.isEmpty()) {
            String url = (String) history.get(history.size() - 1).get("url");
            System.out.printf("Window %d - Tab %d: %s%n", winIndex + 1, tabIndex + 1, url);
          }
        }
      }
      return ExitCode.OK;
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      return ExitCode.SOFTWARE;
    }
  }

  Path getDataHome() {
      String xdg = System.getenv("XDG_DATA_HOME");
      if (xdg != null && !xdg.isEmpty()) {
          return Paths.get(xdg);
      }
      return getUserHome().resolve(".local").resolve("share");
  } 

  Path getUserHome() {
    String home = System.getProperty("user.home");
    if (home == null) {
      home = System.getenv("HOME");           // Linux, macOS
    }
    if (home == null) {
      home = System.getenv("USERPROFILE");       // Windows
    }
    return Paths.get(home);
  }
}
