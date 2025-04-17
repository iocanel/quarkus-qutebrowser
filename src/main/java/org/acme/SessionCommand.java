package org.acme;

import org.acme.session.SaveCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "session", mixinStandardHelpOptions = true, subcommands = {
        SaveCommand.class
})
public class SessionCommand implements Runnable {

    @Spec
    protected CommandSpec spec;

    @Override
    public void run() {
        spec.subcommands().get("save").execute();
    }

}
