package org.acme.tabs;

import static org.acme.utils.ExecUtil.retryInBrowser;
import static org.acme.utils.WaitUtil.waitForFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

import org.acme.utils.ExecUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

@Command(name = "select", 
  description = "Select a tab by name or URL",
  mixinStandardHelpOptions = true)
public class SelectCommand implements Callable<Integer> {

  @Parameters(index = "0", description = "A query string to use for fuzzily matching tab by name or url")
  String query;

  @Override
  public Integer call() throws Exception {
    ExecUtil.qutebrowserCommand(":tab-select " + query);
    return ExitCode.OK;
  }
}
