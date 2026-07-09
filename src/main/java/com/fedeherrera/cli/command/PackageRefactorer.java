package com.fedeherrera.cli.command;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

final class PackageRefactorer {

    private PackageRefactorer() {}

    static void refactorJavaPackages(String projectFolder, String oldPackage, String newPackage) {
        System.out.println("Refactorizando codigo fuente y directorios fisicos...");

        replacePackageText(projectFolder, oldPackage, newPackage);

        relocateSourceSet(Paths.get(projectFolder, "src", "main", "java"), oldPackage, newPackage);
        relocateSourceSet(Paths.get(projectFolder, "src", "test", "java"), oldPackage, newPackage);

        System.out.println("Estructura de paquetes reubicada y carpetas viejas eliminadas.");
    }

    private static void replacePackageText(String projectFolder, String oldPackage, String newPackage) {
        try (Stream<Path> walk = Files.walk(Paths.get(projectFolder, "src"))) {
            walk.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java")
                            || path.toString().endsWith(".properties")
                            || path.toString().endsWith(".yml"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path, StandardCharsets.UTF_8);
                            if (content.contains(oldPackage)) {
                                String updatedContent = content.replace(oldPackage, newPackage);
                                Files.writeString(path, updatedContent, StandardCharsets.UTF_8);
                            }
                        } catch (IOException e) {
                            System.err.println("No se pudo actualizar: " + path.getFileName());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error al procesar contenidos de texto: " + e.getMessage());
        }
    }

    private static void relocateSourceSet(Path javaRoot, String oldPackage, String newPackage) {
        String oldPathStructure = oldPackage.replace(".", File.separator);
        String newPathStructure = newPackage.replace(".", File.separator);

        Path sourceDir = javaRoot.resolve(oldPathStructure);
        Path targetDir = javaRoot.resolve(newPathStructure);

        if (!Files.exists(sourceDir) || sourceDir.equals(targetDir)) {
            return;
        }

        try {
            Files.createDirectories(targetDir);

            List<Path> sourceFiles;
            try (Stream<Path> walk = Files.walk(sourceDir)) {
                sourceFiles = walk.filter(Files::isRegularFile).toList();
            }

            for (Path sourceFile : sourceFiles) {
                Path relativePath = sourceDir.relativize(sourceFile);
                Path destinationFile = targetDir.resolve(relativePath);
                Files.createDirectories(destinationFile.getParent());
                try {
                    Files.move(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.err.println("move fallo, intentando copy+delete: " + sourceFile + " - " + e.getMessage());
                    try {
                        Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                        Files.delete(sourceFile);
                    } catch (IOException e2) {
                        System.err.println("Error al copiar archivo: " + sourceFile + " - " + e2.getMessage());
                    }
                }
            }

            try (Stream<Path> walk = Files.walk(sourceDir)) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            Path parent = sourceDir.getParent();
            while (parent != null && !parent.equals(javaRoot)) {
                try (Stream<Path> entries = Files.list(parent)) {
                    if (entries.findAny().isEmpty()) {
                        Files.delete(parent);
                        parent = parent.getParent();
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error critico en reubicacion fisica (" + javaRoot + "): " + e.getMessage());
        }
    }
}
