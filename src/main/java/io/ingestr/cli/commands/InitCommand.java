package io.ingestr.cli.commands;

import io.ingestr.cli.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import picocli.CommandLine;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@CommandLine.Command(name = "init")
public class InitCommand implements Callable<Integer> {
    @CommandLine.Option(names = {"-t", "--type"}, defaultValue = "java-maven")
    private String type;

    @CommandLine.Option(names = {"-n", "--name"})
    private String name;

    @CommandLine.Option(names = {"-g", "--group"})
    private String group;

    @CommandLine.Option(names = {"-d", "--description"})
    private String description;

    @Override
    public Integer call() {
        log.info("Initializing new {} loader...", type);
        try (Scanner sc = new Scanner(System.in)) {

            while (!validateName(name)) {
                System.out.print("Enter name for the loader (e.g. my-loader) : ");
                name = sc.nextLine();
            }

            while (!validateGroup(group)) {
                System.out.print("Enter group for the loader (e.g. com.company.domain) : ");
                group = sc.nextLine();
            }
            if (StringUtils.equalsIgnoreCase("java-maven", type)) {
                try {
                    initJavaMaven(name);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return 1;
                }
            }
        }

        return 0;
    }

    void initJavaMaven(String name) throws IOException {
        unzipTemplate(name, Application.class.getResourceAsStream("/loader-templates/bootstrap-java-maven.zip"));
    }

    void unzipTemplate(String loaderName, InputStream templateZip) throws IOException {
        File destDir = new File(loaderName);
        if (destDir.exists()) {
            throw new IllegalArgumentException("Cannot create loader '" + loaderName + "' as the path " + destDir.getPath() + " already exists");
        }
        if (!destDir.mkdir()) {
            throw new IllegalStateException("Could not create directory " + destDir.getPath());
        }


        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(templateZip);) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    try (FileOutputStream fos = new FileOutputStream(newFile);) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        Runtime.getRuntime().exec("chmod +x " + destDir.getPath() + "/mvnw");

        //update the  pom to substitute the variables
        String pom = IOUtils.toString(new FileInputStream(new File(destDir, "pom.xml")), Charset.defaultCharset());

        Map<String, String> props = new HashMap<>();
        props.put("loaderName", loaderName);
        props.put("groupName", group);
        props.put("description", description == null ? "Ingestr Loader - " + loaderName : description);

        StrSubstitutor strSubstitutor = new StrSubstitutor(
                props
        );
        pom = strSubstitutor.replace(pom);
        IOUtils.write(pom, new FileOutputStream(new File(destDir, "pom.xml")), Charset.defaultCharset());
    }

    File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }


    boolean validateName(String name) {
        if (StringUtils.isBlank(name)) {
            log.warn("Loader name cannot be blank");
            return false;
        }
        if (!name.matches("[a-zA-Z0-9-._]+")) {
            log.warn("Loader name must contain only a-z, 0-9 and characters '.', '-' or '_'");
            return false;
        }
        return true;
    }


    boolean validateGroup(String name) {
        if (StringUtils.isBlank(name)) {
            log.warn("Group name cannot be blank");
            return false;
        }
        if (!name.matches("[a-zA-Z0-9-._]+")) {
            log.warn("Group name must contain only a-z, 0-9 and characters '-' or '_'");
            return false;
        }
        return true;
    }
}
