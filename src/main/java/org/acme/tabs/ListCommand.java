package org.acme.tabs;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Command(name = "list", description = "List qutebrowser windows and tabs", mixinStandardHelpOptions = true)
public class ListCommand implements Callable<Integer> {

    private static final String SESSION_NAME = "quarkus";
    private static final String SESSION_FILE_NAME = "quarkus.yml";
    private static final Path SESSION_FILE = Paths.get(System.getProperty("user.home"), ".local", "share", "qutebrowser", "sessions", SESSION_FILE_NAME);
    private static final Path DEFAULT_OUTPUT_FILE = Paths.get(System.getProperty("java.io.tmpdir"), "qutebrowser", "tabs");

    @Spec
    protected CommandSpec spec;

  @SuppressWarnings("unchecked")
	@Override
    public Integer call() throws Exception {
        try {
            Files.createDirectories(DEFAULT_OUTPUT_FILE.getParent());
            ProcessBuilder pb = new ProcessBuilder("qutebrowser", ":session-save " + SESSION_NAME);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            Thread.sleep(200);
        
            if (!Files.exists(SESSION_FILE)) {
                System.err.println("Session file not found: " + SESSION_FILE);
                return ExitCode.SOFTWARE;
            }

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<?, ?> sessionData = mapper.readValue(SESSION_FILE.toFile(), Map.class);

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
}
