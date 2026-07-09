package com.fedeherrera.cli;

import com.fedeherrera.cli.command.InitCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "api-wizard", subcommands = InitCommand.class, mixinStandardHelpOptions = true, version = "1.0.0", description = "Asistente interactivo para crear APIs REST desde un template Spring Boot Stater Secure API FULL by Fede Herrera.", headerHeading = "%nUso:%n%n", optionListHeading = "%nOpciones:%n")
public class ApiWizardCommand implements Runnable {
    public static void main(String[] args) {
        CommandLine.run(new ApiWizardCommand(), args);
    }

    @Override
    public void run() {
        System.out.println("The api wizard command");
    }
}