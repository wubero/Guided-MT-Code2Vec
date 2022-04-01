package com.github.ciselab.program;

import java.io.IOException;

public class MainPipeline {

    public static final String path_bash = "C:/Program Files/Git/bin/bash.exe";

    public static void main(String[] args) {
        System.out.println("Preprocess file");
        //run("source preprocess.sh");
        System.out.println("Eval model with preprocessed data");
        String eval = "python3 code2vec.py --load models/java14_model/saved_model_iter8.release --test data/java-testPipeline/java-testPipeline.test.c2v --logs-path eval_log.txt";
        run(eval);
        System.out.println("Completed evaluation, results are in log.txt");
        // The evaluation writes to the log.txt file
    }

    private static void run(String git_command) {
        // Path to your repository
        String path_repository = "cd ./code2vec/";
        // Git command you want to run

        String command = path_repository + " && " + git_command;

        runBashCommand(command);
    }

    private static void runBashCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(path_bash, "-c", command);

            System.out.println("Started process");
            Process process = processBuilder.start();

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(" --- Command run successfully");

            } else {
                System.out.println(" --- Command run unsuccessfully");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(" --- Interruption in RunCommand: " + e);
            // Restore interrupted state
            Thread.currentThread().interrupt();
        }
    }
}
