package com.github.ciselab.program;

import com.github.ciselab.metric.MetricCategory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.IOException;

public class MainPipeline {

    public static final String path_bash = "C:/Program Files/Git/bin/bash.exe";
    public static final String outputFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/log.txt";
    public static final String configFile = "./../resources/configuration.properties";

    public static void main(String[] args) {

    }

    public static void runCode2vec() {
        System.out.println("Preprocessing file");
        run("preprocess.sh");
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

    public static List<Double> getWeights() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(configFile)) {

            // load a properties file
            prop.load(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Double> weights = new ArrayList<>();
        for(MetricCategory i: MetricCategory.values()) {
            weights.add(Double.parseDouble(prop.getProperty(i.name())));
        }

        return weights;
    }

    private static void runBashCommand(String command) {
        try {
//            ProcessBuilder processBuilder = new ProcessBuilder();
//            processBuilder.command(path_bash, "-c", command);

            System.out.println("Started process");
//            Process process = processBuilder.start();
            Process process  = Runtime.getRuntime().exec(command);
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
