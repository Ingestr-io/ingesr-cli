package io.ingestr.cli.commands;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(name = "build")
public class BuildCommand implements Callable<Integer> {

    @Override
    public Integer call() throws IOException, InterruptedException {
        boolean isValid = true;

        File file = new File("pom.xml");
        if (!file.exists()) {
            isValid = false;
        }


        if (!isValid) {
            log.error("Could not find a valid Ingestr Loader installation");
            System.exit(-1);
        }


        Runtime runtime = Runtime.getRuntime();

        String mvnCommand = "mvnw";
        if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), "win")) {
            mvnCommand = "mvnw.cmd";
        }
        Process proc = runtime.exec("./" + mvnCommand + " clean package");


        new Thread(() -> {
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;

            try {
                while ((line = input.readLine()) != null)
                    if (line.startsWith("[INFO]")) {
                        log.info(line.substring("[INFO] ".length()));
                    } else if (line.startsWith("[WARNING]")) {
                        log.warn(line.substring("[WARNING] ".length()));
                    } else if (line.startsWith("[ERROR]")) {
                        log.error(line.substring("[ERROR] ".length()));
                    } else {
                        log.info(line);
                    }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }).start();

        proc.waitFor();

        return 0;
    }
}
