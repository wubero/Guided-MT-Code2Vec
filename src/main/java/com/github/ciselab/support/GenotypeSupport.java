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
import io.jenetics.BitGene;
import io.jenetics.EnumGene;
import io.jenetics.Phenotype;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 * This class supports the project with all access to the Transformer and code2vec projects.
 *
 * In more detail, it performs all the transformations on the java code,
 * and then evaluates the pre-trained code2vec model.
 */
public class GenotypeSupport {

    public static final String path_bash = "C:/Program Files/Git/bin/bash.exe";
    public static final String resultFile =  "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/log.txt";
    public static final String configFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/src/main/resources/config.properties";
    public static final String dataDir = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/data/";
    private static String currentDataset = "generation_0";

    public static boolean maximize = true;
    private static long seed = 200;
    private static boolean removeAllComments = false;
    private static Engine.TransformationScope transformationScope = Engine.TransformationScope.global;
    private static long transformations = 1;
    private static final Properties prop = new Properties();
    private static List<Metric> metricList = new ArrayList<>();
    private static List<Double> metricWeights = new ArrayList<>();

    public static Map<List<BaseTransformer>, String> fileLookup = new HashMap<>();
    public static Map<List<BaseTransformer>, Double> metricLookup = new HashMap<>();

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static FileWriter logger;

    private static long totalCode2vecTime = 0;
    private static long totalTransformationTime = 0;

    private static void createLog() {
        try {
            logger = new FileWriter("GA_log");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the base seed used for this run.
     * @return the seed.
     */
    public static long getSeed() {
        return seed;
    }

    /**
     * Get the current dataset used as a baseline.
     * @return the dataset.
     */
    public static String getCurrentDataset() {
        return currentDataset;
    }

    public static long getTotalCode2vevTime(){
        return totalCode2vecTime;
    }

    public static long getTotalTransformationTime() {
        return totalTransformationTime;
    }

    /**
     * Get the initialized metrics.
     * @return the list of metrics.
     */
    public static List<Metric> getMetrics() {
        return metricList;
    }

    /**
     * Get the metric weights.
     * @return the list of metric weights.
     */
    public static List<Double> getWeights() {
        return metricWeights;
    }

    /**
     * Get the directory corresponding to the given genotype.
     * @param genotype the list of transformers.
     * @return the directory string if it exists, null otherwise.
     */
    public static Optional<String> getDir(List<BaseTransformer> genotype) {
        String file = fileLookup.get(genotype);
        return Optional.ofNullable(file);
    }

    /**
     * Create a key value pair of an individual and the corresponding fitness.
     * @param indiv the individual.
     * @param fitness the fitness score.
     */
    public static void fillFitness(List<BaseTransformer> indiv, double fitness) {
        metricLookup.put(indiv, fitness);
    }

    /**
     *Get the fitness score corresponding to a given genotype.
     * @param genotype the genotype.
     * @return the fitness score.
     */
    public static Optional<Double> getMetricResult(List<BaseTransformer> genotype) {
        Double file = metricLookup.get(genotype);
        return Optional.ofNullable(file);
    }

    /**
     * Store the current genotype together with the fitness and filename in the map for later reference.
     * @param genotype the list of transformers.
     * @param fileName the file name.
     * @param score the fitness score.
     */
    public static void storeFiles(List<BaseTransformer> genotype, String fileName, Double score) {
        fileLookup.put(genotype, fileName);
        metricLookup.put(genotype, score);
    }

    /**
     * Log to GA_log file.
     * @param text the string to log.
     */
    public static void log(String text) {
        try {
            LocalDateTime now = LocalDateTime.now();
            logger.write("[" + dtf.format(now) + "] " + text + "\n");
        } catch (IOException e) {
            System.out.println("Couldn't log: " + text);
        }
    }

    /**
     * Remove previously used directories.
     */
    public static boolean removeOtherDirs() {
        String toKeep = currentDataset.split("_")[0];
        File toDelete = new File(dataDir);
        File[] entries = toDelete.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (!entry.getName().contains(toKeep)) {
                    File[] files = entry.listFiles();
                    if (files != null) {
                        for (File i : files) {
                            i.delete();
                        }
                    }
                    entry.delete();
                }
            }
        }
        return toDelete.delete();
    }

    /**
     * Initialize global fields with config file data.
     */
    public static void initializeFields() {
        createLog();
        try (InputStream input = new FileInputStream(configFile)) {

            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(prop.get("Optimization_objective") != null)
            maximize = prop.get("Optimization_objective").equals("max");
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
        removeZeroWeights();
    }

    /**
     * Remove all metrics that have a weight of zero.
     * These do not have to be calculated or initialized.
     */
    private static void removeZeroWeights() {
        List<Integer> toRemove = new ArrayList<>();
        for(int i = 0; i < metricWeights.size(); i++) {
            if(metricWeights.get(i) <= 0) {
                toRemove.add(i);
            }
        }
        for(int i: toRemove){
            metricList.remove(i);
            metricWeights.remove(i);
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
     * Generate random string for the intermediate dataset names name.
     * @return the directory name.
     */
    private static String generateRandomString() {
        String options = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int maxLength = 5;
        Random r =  new Random();
        StringBuilder out = new StringBuilder();

        for(int i = 0; i < maxLength; i++) {
            out.append(options.charAt(r.nextInt(0, options.length())));
        }
        return out.toString();
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
        long start = System.currentTimeMillis();
        TransformerRegistry registry = new TransformerRegistry("fromGA");
        for(BaseTransformer i: transformers) {
            i.setTryingToCompile(false);
            registry.registerTransformer(i);
        }

        String outputSet = generateRandomString();
        fileLookup.put(transformers, outputSet);

        Engine engine = new Engine(dataDir + input, dataDir + outputSet + "/test", registry);
        engine.setNumberOfTransformationsPerScope(transformers.size(), transformationScope);
        engine.setRandomSeed(seed);
        engine.setRemoveAllComments(removeAllComments);

        Launcher launcher = new spoon.Launcher();
        launcher.addInputResource(engine.getCodeDirectory());
        // The CodeRoot is the highest level of available information regarding the AST
        CtModel codeRoot = launcher.buildModel();
        // With the imports set to true, on second application the import will disappear, making Lambdas uncompilable.
        launcher.getFactory().getEnvironment().setAutoImports(true);
        //Further steps are in the method below.
        EngineResult result = engine.run(codeRoot);
        App.WriteAST(result, launcher);

        long diff = (System.currentTimeMillis() - start) / 1000;
        totalTransformationTime += diff;
        log("Transformations of this individual took: " + diff + " seconds");
        return outputSet;
    }

    /**
     * Run all scripts from the code2vec project.
     * This includes first preprocessing the code files and then evaluating the model.
     * @param dataset The name of the dataset.
     */
    public static void runCode2vec(String dataset) {
        long start = System.currentTimeMillis();
        String path = dataDir + dataset;
        removeSubDirs(new File(path + "/test"), new File(path + "/test"));
        createDirs(path);
        // Preprocessing file.
        String preprocess = "source preprocess.sh " + path + " " + dataset;
        runCode2VecCommand(preprocess);
        // Evaluating code2vec model with preprocessed files.
        String testData = "data/" + dataset + "/" + dataset + ".test.c2v";
        String eval = "python3 code2vec.py --load models/java14_model/saved_model_iter8.release --test " + testData + " --logs-path eval_log.txt";
        runCode2VecCommand(eval);
        // The evaluation writes to the result.txt file

        long diff = (System.currentTimeMillis() - start) / 1000;
        totalCode2vecTime += diff;
        log("Code2vec operations of this generation took: " + diff + " seconds");
    }

    /**
     * Create the correct directories for the code2vec application.
     * @param path path to the dataset.
     */
    private static void createDirs(String path) {
        File valDir = new File(path + "/validation");
        File trainingDir = new File(path + "/training");
        valDir.mkdir();
        trainingDir.mkdir();
    }

    /**
     * Moves all files from subdirectories to the main target directory.
     * @param toDir the main target directory.
     * @param currDir the directory we are currently in.
     */
    public static boolean removeSubDirs(File toDir, File currDir) {
        for (File file: currDir.listFiles()) {
            if (file.isDirectory()) {
                removeSubDirs(toDir, file);
                file.delete();
            } else {
                file.renameTo(new File(toDir, file.getName()));
            }
        }
        return true;
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
