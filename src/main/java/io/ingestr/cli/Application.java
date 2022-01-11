package io.ingestr.cli;

import io.ingestr.cli.commands.BuildCommand;
import io.ingestr.cli.commands.ConfigCommand;
import io.ingestr.cli.commands.DeployCommand;
import io.ingestr.cli.commands.InitCommand;
import io.ingestr.cli.models.Config;
import io.micronaut.configuration.picocli.PicocliRunner;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.Properties;

@CommandLine.Command(name = "main",
        description = "...",
        subcommands = {
                InitCommand.class,
                ConfigCommand.class,
                DeployCommand.class,
                BuildCommand.class
        },
        mixinStandardHelpOptions = true)
@Slf4j
public class Application implements Runnable {

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        Properties buildProps = new Properties();
        try {
            buildProps.load(Application.class.getResourceAsStream("/build.properties"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("\n" +
                "  ___                       _                   ____ _     ___ \n" +
                " |_ _|_ __   __ _  ___  ___| |_ _ __           / ___| |   |_ _|\n" +
                "  | || '_ \\ / _` |/ _ \\/ __| __| '__|  _____  | |   | |    | | \n" +
                "  | || | | | (_| |  __/\\__ \\ |_| |    |_____| | |___| |___ | | \n" +
                " |___|_| |_|\\__, |\\___||___/\\__|_|             \\____|_____|___|\n" +
                "            |___/                                              \n");
        System.out.println("   (Ingestr CLI v" + buildProps.getProperty("version") + ") " + buildProps.getProperty("build.date"));
        System.out.println("==============================================================");

        Config config = Config.load();

        if (config.hasIngestrServerUrl()) {
            System.setProperty("ingestr.server.url", config.getIngestrServerUrl());
        }

        if (args.length == 0) {
            args = new String[]{"-h"};
        }
        PicocliRunner.run(Application.class,  args);
    }

    public void run() {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
