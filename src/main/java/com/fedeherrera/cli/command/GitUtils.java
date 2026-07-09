package com.fedeherrera.cli.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

final class GitUtils {

    private GitUtils() {}

    static void cleanGitMetadata(String projectFolder) {
        Path gitDir = Paths.get(projectFolder, ".git");
        if (Files.exists(gitDir)) {
            try {
                System.out.println("Limpiando metadatos de Git del template...");
                try (Stream<Path> walk = Files.walk(gitDir)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
                System.out.println("Proyecto desvinculado del template.");
            } catch (IOException e) {
                System.err.println("No se pudo eliminar .git: " + e.getMessage());
            }
        }
    }
}
