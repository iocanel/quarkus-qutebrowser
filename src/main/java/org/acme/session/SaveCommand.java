package org.acme.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Command(name = "save", description = "Save qutebrowser session (windows and tabs) and output them to a file", mixinStandardHelpOptions = true)
public class SaveCommand implements Runnable {

    private static final String SESSION_NAME = "quarkus";
    private static final String SESSION_FILE_NAME = "quarkus.yml";
    private static final Path SESSION_FILE = Paths.get(System.getProperty("user.home"), ".local", "share", "qutebrowser", "sessions", SESSION_FILE_NAME);
    private static final Path DEFAULT_OUTPUT_FILE = Paths.get(System.getProperty("java.io.tmpdir"), "qutebrowser", "tabs");

    @Option(names = {"-o", "--output"}, description = "Output file path (default: ${DEFAULT-VALUE})")
    Path outputFile = DEFAULT_OUTPUT_FILE;

    @Override
    public void run() {
        try {
            Files.createDirectories(DEFAULT_OUTPUT_FILE.getParent());

            System.out.println("Saving session using qutebrowser...");
            ProcessBuilder pb = new ProcessBuilder("qutebrowser", ":session-save " + SESSION_NAME);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            Thread.sleep(200);

            if (!Files.exists(SESSION_FILE)) {
                System.err.println("Session file not found: " + SESSION_FILE);
                return;
            }

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<?, ?> sessionData = mapper.readValue(SESSION_FILE.toFile(), Map.class);

            try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
                List<Map<String, Object>> windows = (List<Map<String, Object>>) sessionData.get("windows");
                for (int winIndex = 0; winIndex < windows.size(); winIndex++) {
                    List<Map<String, Object>> tabs = (List<Map<String, Object>>) windows.get(winIndex).get("tabs");
                    for (int tabIndex = 0; tabIndex < tabs.size(); tabIndex++) {
                        List<Map<String, Object>> history = (List<Map<String, Object>>) tabs.get(tabIndex).get("history");
                        if (!history.isEmpty()) {
                            String url = (String) history.get(history.size() - 1).get("url");
                            writer.write(String.format("Window %d - Tab %d: %s%n", winIndex + 1, tabIndex + 1, url));
                            System.out.printf("Window %d - Tab %d: %s%n", winIndex + 1, tabIndex + 1, url);
                        }
                    }
                }
            }
            System.out.println("Tabs written to: " + outputFile.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
