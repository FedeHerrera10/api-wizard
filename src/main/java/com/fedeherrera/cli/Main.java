package com.fedeherrera.cli;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new ApiWizardCommand());

        // Forzar a Picocli a usar UTF-8 para System.out y System.err
        cmd.setOut(new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true));
        cmd.setErr(new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true));

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}