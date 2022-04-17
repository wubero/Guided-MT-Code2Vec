package com.github.ciselab.support;

import com.github.ciselab.lampion.cli.program.App;
import com.github.ciselab.lampion.core.program.Engine;
import com.github.ciselab.lampion.core.program.EngineResult;
import com.github.ciselab.lampion.core.transformations.TransformerRegistry;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.metric.Metric;
import com.github.ciselab.metric.MetricCategory;
import com.github.ciselab.metric.metrics.F1_score;
import com.github.ciselab.metric.metrics.MRR;
import com.github.ciselab.metric.metrics.Percentage_MRR;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 * This class supports the project with all access to the Transformer and code2vec projects.
 *
 * In more detail, it performs all the transformations on the java code,
 * and then evaluates the pre-trained code2vec model.
 */
public class PipelineSupport {

    public static final String path_bash = "C:/Program Files/Git/bin/bash.exe";
    public static final String resultFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/log.txt";
    public static final String configFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/src/main/resources/config.properties";
    public static final String dataDir = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/data/";
    private static String currentDataset = "test_0";

    private static long seed = 200;
    private static boolean removeAllComments = false;
    private static Engine.TransformationScope transformationScope = Engine.TransformationScope.global;
    private static long transformations = 1;
    private static final Properties prop = new Properties();
    private static List<Metric> metricList = new ArrayList<>();
    private static List<Double> metricWeights = new ArrayList<>();

    public static long getSeed() {
        return seed;
    }

    public static String getCurrentDataset() {
        return currentDataset;
    }

    public static void setCurrentDataset(String dataset) {
        currentDataset = dataset;
    }

    public static List<Metric> getMetrics() {
        return metricList;
    }

    public static List<Double> getWeights() {
        return metricWeights;
    }

    /**
     * Remove previously used directories.
     */
    public static void removeOldDir() {
        String[] temp = currentDataset.split("_");
        int version = Integer.parseInt(temp[1]);
        if(version > 2) {
            String directoryName = temp[0] + "_" + (version-2);
            File toDelete = new File(dataDir + directoryName);
            File[] entries = toDelete.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    entry.delete();
                }
            }
            toDelete.delete();
        }
    }

    /**
     * Initialize global fields with config file data.
     */
    public static void initializeFields() {
        try (InputStream input = new FileInputStream(configFile)) {

            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(prop.get("seed") != null)
            seed = Long.parseLong((String) prop.get("seed"));
        if(prop.get("removeAllComments")!=null){
            removeAllComments = Boolean.parseBoolean((String) prop.get("removeAllComments"));
        }
        if(prop.get("transformationscope") != null){
            transformationScope = Engine.TransformationScope.valueOf(prop.getProperty("transformationscope"));
        }
        if(prop.get("transformations") != null) {
            transformations = Long.parseLong((String)prop.get("transformations"));
        }
        for(MetricCategory metricCategory: MetricCategory.values()) {
            metricList.add(createMetric(metricCategory.name()));
        }
        for(MetricCategory i: MetricCategory.values()) {
            metricWeights.add(Double.parseDouble(prop.getProperty(i.name())));
        }
    }

    /**
     * Create a new metric from the specified name.
     * @param name the metric name.
     * @return The new metric.
     */
    private static Metric createMetric(String name) {
        switch (name) {
            case "MRR":
                return new MRR();
            case "F1_score":
                return new F1_score();
            case "Percentage_MRR":
                return new Percentage_MRR();
            default:
                throw new IllegalArgumentException("Metric name not a correct metric.");
        }
    }

    /**
     * This method creates an engine and runs the transformations on the input directory.
     * This is done by first creating all transformers in a TransformerRegistry and then creating a new Engine.
     * With this engine we can our CtModel that is created with a spoon launcher.
     * @param transformers the list of transformers.
     * @param input the input directory.
     * @return the directory which the transformation .java files are in.
     */
    public static String runTransformations(List<BaseTransformer> transformers, String input) {
        TransformerRegistry registry = new TransformerRegistry("fromGA");
        for(BaseTransformer i: transformers) {
            registry.registerTransformer(i);
        }
        String[] temp = input.split("_");

        String outputSet = temp[0] + "_" + (Integer.parseInt(temp[1])+1);
        Engine engine = new Engine(dataDir + input, dataDir + outputSet, registry);
        engine.setNumberOfTransformationsPerScope(transformations, transformationScope);
        engine.setRandomSeed(seed);
        engine.setRemoveAllComments(removeAllComments);

        Launcher launcher = new spoon.Launcher();
        launcher.addInputResource(engine.getCodeDirectory());
        // The CodeRoot is the highest level of available information regarding the AST
        CtModel codeRoot = launcher.buildModel();
        // With the imports set to true, on second application the import will disappear, making Lambdas uncompilable.
        launcher.getFactory().getEnvironment().setAutoImports(false);
        //Further steps are in the method below.
        EngineResult result = engine.run(codeRoot);
        App.WriteAST(result, launcher);

        return outputSet;
    }

    /**
     * Run all scripts from the code2vec project.
     * This includes first preprocessing the code files and then evaluating the model.
     * @param dataset The name of the dataset.
     */
    public static void runCode2vec(String dataset) {
        // Preprocessing file.
        String preprocess = "source preprocess.sh " + dataDir + dataset + " " + dataset;
        runCode2VecCommand(preprocess);
        // Evaluating code2vec model with preprocessed files.
        String testData = "data/" + dataset + "/" + dataset + ".test.c2v";
        String eval = "python3 code2vec.py --load models/java14_model/saved_model_iter8.release --test" + testData +  " --logs-path eval_log.txt";
        runCode2VecCommand(eval);
        // The evaluation writes to the result.txt file
    }

    /**
     * This method runs a command from the code2vec directory.
     * @param comm the command to be run.
     */
    private static void runCode2VecCommand(String comm) {
        String command = "cd code2vec/" + " && " + comm + " && exit";
        runBashCommand(command, 0);
    }

    /**
     * This method runs a given command in git bash and prints the results.
     * @param command the command to be run in git bash.
     */
    private static void runBashCommand(String command, Integer countFailed) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(path_bash, "-c", command);

            Process process = processBuilder.start();

            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(" --- Command run successfully");
            } else {
                if(countFailed < 3) {
                    System.out.println(" --- Command run , trying again");
                    runBashCommand(command, countFailed+1);
                } else {
                    System.out.println("The command: " + command + "\n Will not run, quiting the system.");
                    System.exit(0);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(" --- Interruption in RunCommand: " + e);
            // Restore interrupted state
            Thread.currentThread().interrupt();
        }
    }
}
