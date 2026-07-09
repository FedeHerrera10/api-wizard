package com.fedeherrera.cli.command;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

final class ProjectConfigurator {

    private static final String TEMPLATE_ARTIFACT_ID = "spring-secure-api-starter";
    private static final String TEMPLATE_GROUP_ID = "com.fedeherrera";
    private static final String TEMPLATE_APP_NAME = "spring-secure-api";
    private static final String OLD_MAIN_CLASS = "SpringSecureApiStarterApplication";
    private static final String OLD_DB_NAME = "secure_api";

    private ProjectConfigurator() {}

    static void refactorProjectConfiguration(String projectFolder, String newArtifactId, String newGroupId) {
        System.out.println("Ajustando pom.xml (artifactId y groupId)...");
        Path pomPath = Paths.get(projectFolder, "pom.xml");

        if (Files.exists(pomPath)) {
            try {
                String content = Files.readString(pomPath, StandardCharsets.UTF_8);

                if (content.contains(TEMPLATE_ARTIFACT_ID)) {
                    content = content.replace(TEMPLATE_ARTIFACT_ID, newArtifactId);
                }
                if (content.contains(TEMPLATE_GROUP_ID) && !TEMPLATE_GROUP_ID.equals(newGroupId)) {
                    content = content.replace(TEMPLATE_GROUP_ID, newGroupId);
                }

                Files.writeString(pomPath, content, StandardCharsets.UTF_8);
                System.out.println("'pom.xml' actualizado con exito.");
            } catch (IOException e) {
                System.err.println("Error al refactorizar el pom.xml: " + e.getMessage());
            }
        }
    }

    static void refactorApplicationProperties(String projectFolder, String newAppName) {
        System.out.println("Ajustando properties en application.yml/yaml...");

        for (String ext : new String[]{".yml", ".yaml"}) {
            Path appPath = Paths.get(projectFolder, "src", "main", "resources", "application" + ext);
            if (Files.exists(appPath)) {
                try {
                    String content = Files.readString(appPath, StandardCharsets.UTF_8);
                    if (content.contains(TEMPLATE_APP_NAME)) {
                        String updatedContent = content.replace(TEMPLATE_APP_NAME, newAppName);
                        Files.writeString(appPath, updatedContent, StandardCharsets.UTF_8);
                        System.out.println("'application" + ext + "' actualizado con el nuevo nombre de aplicacion.");
                    }
                } catch (IOException e) {
                    System.err.println("No se pudo refactorizar application" + ext + ": " + e.getMessage());
                }
            }
        }
    }

    static void refactorMainClassName(String projectFolder, String newClassName) {
        Path projectDir = Paths.get(projectFolder);

        System.out.println("Renombrando clase principal a '" + newClassName + "'...");

        try (Stream<Path> walk = Files.walk(projectDir)) {
            walk.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java")
                            || path.toString().endsWith(".yml")
                            || path.toString().endsWith(".properties")
                            || path.toString().endsWith(".xml"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path, StandardCharsets.UTF_8);
                            if (content.contains(OLD_MAIN_CLASS)) {
                                String updated = content.replace(OLD_MAIN_CLASS, newClassName);
                                Files.writeString(path, updated, StandardCharsets.UTF_8);
                                if (path.toString().endsWith(OLD_MAIN_CLASS + ".java")) {
                                    Files.move(path, path.resolveSibling(newClassName + ".java"));
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("No se pudo actualizar referencia a " + OLD_MAIN_CLASS + " en " + path);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error al renombrar clase principal: " + e.getMessage());
        }

        System.out.println("Clase principal renombrada a '" + newClassName + "'.");
    }

    static void refactorDatabaseName(String projectFolder, String newDbName) {
        System.out.println("Configurando base de datos como '" + newDbName + "'...");

        Path composePath = Paths.get(projectFolder, "docker-compose.yaml");
        if (Files.exists(composePath)) {
            try {
                String content = Files.readString(composePath, StandardCharsets.UTF_8);
                String updated = content.replace(OLD_DB_NAME, newDbName);
                Files.writeString(composePath, updated, StandardCharsets.UTF_8);
                System.out.println("'docker-compose.yaml' actualizado con el nuevo nombre de BD.");
            } catch (IOException e) {
                System.err.println("No se pudo actualizar docker-compose.yaml: " + e.getMessage());
            }
        }

        for (String ext : new String[]{".yml", ".yaml"}) {
            Path appPath = Paths.get(projectFolder, "src", "main", "resources", "application" + ext);
            if (Files.exists(appPath)) {
                try {
                    String content = Files.readString(appPath, StandardCharsets.UTF_8);
                    String updated = content.replace(OLD_DB_NAME, newDbName);
                    Files.writeString(appPath, updated, StandardCharsets.UTF_8);
                    System.out.println("'application" + ext + "' actualizado con el nuevo nombre de BD.");
                } catch (IOException e) {
                    System.err.println("No se pudo actualizar application" + ext + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Base de datos configurada como '" + newDbName + "'.");
    }
}
