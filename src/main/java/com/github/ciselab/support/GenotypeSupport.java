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
    public static final String resultFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/log.txt";
    public static final String configFile = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/src/main/resources/config.properties";
    public static final String dataDir = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/data/";
    private static String currentDataset = "spoon_test";

    private static long seed = 200;
    private static boolean removeAllComments = false;
    private static Engine.TransformationScope transformationScope = Engine.TransformationScope.global;
    private static long transformations = 1;
    private static final Properties prop = new Properties();
    private static List<Metric> metricList = new ArrayList<>();
    private static List<Double> metricWeights = new ArrayList<>();

    public static Map<List<Pair<Integer, Integer>>, String> fileLookup = new HashMap<>();
    public static Map<List<Pair<Integer, Integer>>, Double> metricLookup = new HashMap<>();

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

    private static String getNextDataSet() {
        String[] temp = currentDataset.split("_");
        int version = Integer.parseInt(temp[1])+1;
        return temp[0] + "_" + version;
    }

    public Optional<String> getDir(List<Pair<Integer, Integer>> genotype) {
        String file = fileLookup.get(genotype);
        return Optional.ofNullable(file);
    }

    public void storeFiles(List<Pair<Integer, Integer>> genotype, String fileName, Double score) {
        fileLookup.put(genotype, fileName);
        metricLookup.put(genotype, score);
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
     * @param keys the genotype.
     * @param input the input directory.
     * @return the directory which the transformation .java files are in.
     */
    public static String runTransformations(List<BaseTransformer> transformers, List<Pair<Integer, Integer>> keys, String input) {
        TransformerRegistry registry = new TransformerRegistry("fromGA");
        for(BaseTransformer i: transformers) {
            registry.registerTransformer(i);
        }

        String outputSet = generateRandomString();
        fileLookup.put(keys, outputSet);

        Engine engine = new Engine(dataDir + input, dataDir + outputSet + "/test", registry);
        engine.setNumberOfTransformationsPerScope(transformers.size(), transformationScope);
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
     * Save and print the current results + get everything in the correct directories.
     * @param phenotype best phenotype of the generation.
     */
    public static void saveAndPrint(Phenotype<BitGene, Double> phenotype) {
        System.out.println("Starting save and print");
        // get linked file directory
        String file = fileLookup.get(phenotype.genotype().gene());

        // replace files to test_"version"
        File dir = new File(dataDir + file + "/test");
        String targetDir = dataDir + getNextDataSet();
        new File(targetDir).mkdir();
        if(dir.isDirectory()) {
            File[] content = dir.listFiles();
            if(content != null) {
                for (File value : content) {
                    value.renameTo(new File(targetDir, value.getName()));
                }
            }
        }
        // delete other files & clear map
        setCurrentDataset(getNextDataSet());
        removeOtherDirs();
        // write the best fitness to file for plotting
        Double result = phenotype.fitness();
        try {
            FileWriter myWriter = new FileWriter("GA_results.txt");
            myWriter.write("Generation: " + phenotype.generation() + ", result: " + result + "\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    /**
     * Run all scripts from the code2vec project.
     * This includes first preprocessing the code files and then evaluating the model.
     * @param dataset The name of the dataset.
     */
    public static void runCode2vec(String dataset) {
        String path = dataDir + dataset;
        createDirs(path);
        // Preprocessing file.
        String preprocess = "source preprocess.sh " + path + " " + dataset;
        runCode2VecCommand(preprocess);
        // Evaluating code2vec model with preprocessed files.
        String testData = "data/" + dataset + "/" + dataset + ".test.c2v";
        String eval = "python3 code2vec.py --load models/java14_model/saved_model_iter8.release --test " + testData +  " --logs-path eval_log.txt";
        runCode2VecCommand(eval);
        // The evaluation writes to the result.txt file
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
