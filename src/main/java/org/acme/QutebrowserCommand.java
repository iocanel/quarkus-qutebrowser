package org.acme;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@TopCommand
@Command(name = "qutebrowser", mixinStandardHelpOptions = true, subcommands = {
        TabsCommand.class,
        SessionCommand.class,
})
public class QutebrowserCommand implements Runnable {

    @Spec
    protected CommandSpec spec;

    @Override
    public void run() {
      spec.subcommands().get("tabs").execute();
    }

}
