package org.acme;

import org.acme.tabs.ListCommand;
import org.acme.tabs.ReadCommand;
import org.acme.tabs.SelectCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "tabs", mixinStandardHelpOptions = true, subcommands = {
        ListCommand.class,
        ReadCommand.class,
        SelectCommand.class
})
public class TabsCommand implements Runnable {

    @Spec
    protected CommandSpec spec;

    @Override
    public void run() {
        spec.subcommands().get("list").execute();
    }

}
