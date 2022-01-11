package io.ingestr.cli.models;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class Config {
    private Properties properties = new Properties();

    private Config(Properties properties) {
        this.properties = properties;
    }

    public boolean hasIngestrServerUrl() {
        return properties.containsKey("ingestr.server.url");
    }

    public String getIngestrServerUrl() {
        return properties.getProperty("ingestr.server.url");
    }

    public void setIngestrServerUrl(String url) {
        properties.setProperty("ingestr.server.url", url);
    }

    public static Config load() throws IOException {
        File file = new File(System.getProperty("user.home") +
                "/.ingestr/cli.config");

        Properties properties = new Properties();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            }
        }

        return new Config(properties);
    }

    public static void save(Config config) throws IOException {
        File dir = new File(System.getProperty("user.home") + "/.ingestr");
        if (!dir.exists()) {
            log.info("Creating new ingestr directory in {}", dir);
            dir.mkdirs();
        }
        File file = new File(System.getProperty("user.home") +
                "/.ingestr/cli.config");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            config.properties.store(fos, "Ingestr configuration properties");
        }
    }
}
