package com.fedeherrera.cli;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ApiWizardCommand())
                .execute(args);

        System.exit(exitCode);
    }
}