package io.ingestr.cli.commands;

import io.ingestr.cli.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(name = "configure")
public class ConfigCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-u", "--url"},
            description = "The full url of the Ingestr server e.g. (https://ingestr.corp.com)")
    private String ingestrUrl;

    @Override
    public Integer call() {
        log.info("Setting configuration parameters for Ingestr CLI");
        Config config = null;
        try {
            config = Config.load();
        } catch (IOException ioException) {
            log.error(ioException.getMessage());
            return 1;
        }

        if (StringUtils.isBlank(ingestrUrl)) {
            String help = "";
            if (StringUtils.isNotBlank(config.getIngestrServerUrl())) {
                help += " [" + config.getIngestrServerUrl() + "]";
            }

            while (StringUtils.isBlank(ingestrUrl)) {
                System.out.print("Enter url for the Ingestr server (e.g. https://ingestr.corp.com)" + help + " : ");
                try (Scanner sc = new Scanner(System.in)) {
                    if (sc.hasNextLine()) {
                        ingestrUrl = sc.nextLine();
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(ingestrUrl)) {
            config.setIngestrServerUrl(ingestrUrl);
        }

        try {
            Config.save(config);
        } catch (IOException ioException) {
            log.error(ioException.getMessage());
            return 1;
        }

        return 0;
    }
}
