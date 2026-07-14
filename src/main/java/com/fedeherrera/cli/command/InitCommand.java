package com.fedeherrera.cli.command;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@Command(name = "init", description = "Inicializa un nuevo proyecto de API pidiendo nombre y package base.", mixinStandardHelpOptions = true)
public class InitCommand implements Runnable {

    @Option(names = { "-n", "--name" }, description = "Nombre del proyecto")
    private String projectName;

    @Option(names = { "-p", "--package" }, description = "Package base del proyecto (ej: com.fedeherrera.miapi)")
    private String basePackage;

    @Option(names = { "-db", "--db-name" }, description = "Nombre de la base de datos")
    private String dbName;

    @Option(names = { "-t", "--db-type" }, description = "Tipo de base de datos (postgres, mysql, etc.)")
    private String dbType;

    @Option(names = { "-o", "--output" }, description = "Directorio de salida (default: carpeta padre)")
    private String outputDir;

    private static final String OLD_PACKAGE = "com.fedeherrera.spring_secure_api_starter";
    private static final String TEMPLATE_REPO = "https://github.com/FedeHerrera10/spring-secure-api-starter.git";

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("-----------------------------------------------------------");
        System.out.println("""
                ###  ####  ###    #   # ### #####  ###  ####  ####
                #   # #   #  #     #   #  #     #  #   # #   # #   #
                ##### ####   #     # # #  #    #   ##### ####  #   #
                #   # #      #     ## ##  #   #    #   # #  #  #   #
                #   # #     ###    #   # ### ##### #   # #   # ####
                """);
        System.out.println("---------------------by fede herrera-----------------------------");

        String calculatedArtifactId = resolveProjectName(scanner);
        resolveBasePackage(scanner, calculatedArtifactId);
        resolveDbType(scanner);
        resolveDbName(scanner, calculatedArtifactId);

        Path projectDir = resolveOutputDir().resolve(calculatedArtifactId).normalize();
        String projectPath = projectDir.toString();

        System.out.println("[Info:]Configuracion aceptada:");
        System.out.println("[Info:]Ruta:                 " + projectPath);
        System.out.println("[Info:]Package Destino:      " + basePackage);
        System.out.println("[Info:]Tipo de Base de Datos:" + dbType);
        System.out.println("[Info:]Base de Datos:        " + dbName + "\n");

        if (cloneTemplate(projectPath)) {
            GitUtils.cleanGitMetadata(projectPath);

            String newGroupId = NamingUtils.extractGroupId(basePackage);
            ProjectConfigurator.refactorProjectConfiguration(projectPath, calculatedArtifactId, newGroupId);
            PackageRefactorer.refactorJavaPackages(projectPath, OLD_PACKAGE, basePackage);
            ProjectConfigurator.refactorApplicationProperties(projectPath, calculatedArtifactId);

            String newMainClass = NamingUtils.toPascalCase(calculatedArtifactId) + "Application";
            ProjectConfigurator.refactorMainClassName(projectPath, newMainClass);
            ProjectConfigurator.refactorDatabaseName(projectPath, dbName);
            ProjectConfigurator.refactorEnvFile(projectPath, calculatedArtifactId, dbType, dbName);
            System.out.println("---------------------------------------------------------------------------");
            System.out.println("\n[+] Tu API REST ha sido configurada con exito en '" + projectPath + "'!\n");
            System.out.println("---------------------------------------------------------------------------");
            System.out.println("[1] Para iniciar el proyecto, ejecuta:");
            System.out.println("[2] cd " + projectPath);
            System.out.println("[3] modifica tu archivo .env");
            System.out.println("[4] mvn clean install");
            System.out.println("[5] mvn spring-boot:run");
            System.out.println("---------------------------------------------------------------------------");
        }
    }

    private String resolveProjectName(Scanner scanner) {
        if (projectName == null || projectName.trim().isEmpty()) {
            System.out.print("Nombre del proyecto [my-api]: ");
            projectName = scanner.nextLine().trim();
            if (projectName.isEmpty())
                projectName = "my-api";
        }
        return projectName.toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim()
                .replace(" ", "-");
    }

    private void resolveBasePackage(Scanner scanner, String artifactId) {
        if (basePackage == null || basePackage.trim().isEmpty()) {
            String defaultPackage = "com.fedeherrera." + artifactId.replace("-", "");
            System.out.print("Package Base [" + defaultPackage + "]: ");
            String input = scanner.nextLine().trim();
            basePackage = input.isEmpty() ? defaultPackage : input;
        }
    }

    private void resolveDbType(Scanner scanner) {
        if (dbType == null || dbType.trim().isEmpty()) {
            System.out.print("Tipo de base de datos [postgres]: ");
            String input = scanner.nextLine().trim();
            dbType = input.isEmpty() ? "postgres" : input;
        }
    }

    private void resolveDbName(Scanner scanner, String artifactId) {
        if (dbName == null || dbName.trim().isEmpty()) {
            String defaultDbName = NamingUtils.toSnakeCase(artifactId);
            System.out.print("Nombre de la Base de Datos [" + defaultDbName + "]: ");
            String input = scanner.nextLine().trim();
            dbName = input.isEmpty() ? defaultDbName : input;
        }
    }

    private Path resolveOutputDir() {
        if (outputDir != null) {
            return Paths.get(outputDir).toAbsolutePath();
        }
        return Paths.get("..").toAbsolutePath().normalize();
    }

    private boolean cloneTemplate(String projectPath) {
        try {
            System.out.println("[Info:] Clonando el template de Git (shallow clone)...");
            ProcessBuilder pb = new ProcessBuilder("git", "clone", "--depth", "1", TEMPLATE_REPO, projectPath);
            pb.inheritIO();
            int exitCode = pb.start().waitFor();

            if (exitCode == 0) {
                System.out.println("\nTemplate clonado con exito!");
                return true;
            }
            System.err.println("[Error:] Hubo un error al ejecutar 'git clone'.");
            return false;
        } catch (IOException | InterruptedException e) {
            System.err.println("[Error:] Error del sistema: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
