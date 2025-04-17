package org.acme.tabs;

import static org.acme.utils.ExecUtil.retryInBrowser;
import static org.acme.utils.WaitUtil.waitForFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.acme.utils.ExecUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;

@Command(name = "read", 
  description = "Read the content of a tab in the browser",
  mixinStandardHelpOptions = true)
public class ReadCommand implements Callable<Integer> {

  private static final Path DEFAULT_OUTPUT_FILE = Paths.get(System.getProperty("java.io.tmpdir"), "qutebrowser", "tab-text");

  @Option(names = {"-o", "--output"}, description = "Output file path (default: ${DEFAULT-VALUE})")
  Path outputFile = DEFAULT_OUTPUT_FILE;

  @Spec
  CommandSpec spec; 

  @Parameters(index = "0", description = "A query string to use for fuzzily matching tab by name or url")
  String query;

  @Override
  public Integer call() throws Exception {

    Files.createDirectories(outputFile.getParent());

    if (!ExecUtil.isInBrowser()) {
      ExecUtil.qutebrowserCommand(":tab-select " + query);
      Files.deleteIfExists(outputFile);
      retryInBrowser();
    } else {
      // If file does not exist, we are in the browser so let's create it.
        String pageContentFile = System.getenv("QUTE_TEXT");

        if (pageContentFile == null || pageContentFile.isBlank()) {
          System.err.println("QUTE_TEXT environment variable not set.");
          return ExitCode.SOFTWARE;
        }

        Path inputPath = Paths.get(pageContentFile);

        if (!Files.exists(inputPath)) {
          System.err.println("File does not exist: " + inputPath);
          return 1;
        }

        String content = Files.readString(inputPath);

        Files.writeString(outputFile, content + "\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    waitForFile(outputFile, 5, TimeUnit.SECONDS);
    System.out.println(Files.readString(outputFile));
    return ExitCode.OK;
  }
}
