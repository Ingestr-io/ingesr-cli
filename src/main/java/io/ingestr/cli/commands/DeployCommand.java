package io.ingestr.cli.commands;

import io.ingestr.client.LoaderArtifactClient;
import io.ingestr.client.LoaderClient;
import io.ingestr.client.models.UpdateArtifactRequest;
import io.ingestr.server.models.LoaderDTO;
import io.ingestr.cli.models.Pom;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@CommandLine.Command(name = "deploy")
public class DeployCommand implements Callable<Integer> {
    private LoaderClient loaderClient;
    private LoaderArtifactClient loaderArtifactClient;

    @Inject
    public DeployCommand(LoaderClient loaderClient, LoaderArtifactClient loaderArtifactClient) {
        this.loaderClient = loaderClient;
        this.loaderArtifactClient = loaderArtifactClient;
    }

    @Override
    public Integer call() throws IOException {
        File file = new File("pom.xml");
        if (!file.exists()) {
            log.error("Could not locate the 'pom.xml' file of the Loader!");
            return 1;
        }
        Pom pom;
        try {
            pom = Pom.load();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        log.info("Initiating Deployment of loader '{}'...", pom.getArtifactId());

        Optional<LoaderDTO> loaderDTO = loaderClient.get(pom.getArtifactId());
        if (loaderDTO.isEmpty()) {
            log.info("Performing Loader registration for new Loader with Ingestr Server - {}", pom.getArtifactId());
            loaderDTO = Optional.of(loaderClient.create(LoaderCreateCommand.builder()
                    .name(pom.getArtifactId())
                    .desiredCapacity(1)
                    .identifier(pom.getArtifactId())
                    .build())
            );
        }

        Path jarFile = Files.list(Path.of("target"))
                .filter(f -> f.toString().endsWith("jar"))
                .filter(f -> !f.toString().startsWith("original"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find a deployable 'jar' in the 'target' folder.  Make sure to build before deploy."));

        log.info("Uploading Loader Artifact '{}' to Ingestr Server...",
                jarFile.getFileName());

        try (
                FileInputStream fis = new FileInputStream(jarFile.toFile());
                BufferedInputStream bis = new BufferedInputStream(fis)
        ) {
            loaderArtifactClient.updateArtifact(UpdateArtifactRequest.builder()
                    .version(pom.getVersion())
                    .loaderIdentifier(loaderDTO.get().getIdentifier())
                    .inputStream(bis)
                    .filename("loader.jar")
                    .size(jarFile.toFile().length())
                    .build());
        } catch (IOException ioException) {
            log.error(ioException.getMessage(), ioException);
            throw new RuntimeException(ioException.getMessage(), ioException);
        }

        return 0;
    }
}
