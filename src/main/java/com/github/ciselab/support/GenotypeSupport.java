package com.github.ciselab.support;

import com.github.ciselab.lampion.core.program.Engine;
import com.github.ciselab.lampion.core.program.EngineResult;
import com.github.ciselab.lampion.core.transformations.EmptyTransformationResult;
import com.github.ciselab.lampion.core.transformations.TransformationResult;
import com.github.ciselab.lampion.core.transformations.TransformerRegistry;
import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.metric.Metric;
import com.github.ciselab.metric.MetricCategory;
import com.github.ciselab.metric.SecondaryMetrics;
import com.github.ciselab.metric.metrics.EditDistance;
import com.github.ciselab.metric.metrics.F1_score;
import com.github.ciselab.metric.metrics.InputLength;
import com.github.ciselab.metric.metrics.MRR;
import com.github.ciselab.metric.metrics.Percentage_MRR;
import com.github.ciselab.metric.metrics.Precision;
import com.github.ciselab.metric.metrics.PredictionLength;
import com.github.ciselab.metric.metrics.Recall;
import com.github.ciselab.metric.metrics.Transformations;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.CtModel;

/**
 * This class supports the project with all access to the Transformer and code2vec projects.
 *
 * In more detail, it performs all the transformations on the java code,
 * and then evaluates the pre-trained code2vec model.
 */
public class GenotypeSupport {

    public String path_bash = "C:/Program Files/Git/bin/bash.exe";
    public static final String dir_path = System.getProperty("user.dir").replace("\\", "/");
    public String configFile = dir_path + "/src/main/resources/config.properties";
    public String dataDir = dir_path + "/code2vec/data/";
    private final String currentDataset = "generation_0";

    private boolean maximize = true;
    private long seed = 200;
    private boolean removeAllComments = false;
    private Engine.TransformationScope transformationScope = Engine.TransformationScope.global;
    private final Properties prop = new Properties();
    private final List<Metric> metricList = new ArrayList<>();
    private final List<Metric> secondaryMetrics = new ArrayList<>();
    private final List<Float> metricWeights = new ArrayList<>();
    private Boolean[] objectives;

    public Map<List<BaseTransformer>, String> fileLookup = new HashMap<>();
    public Map<List<BaseTransformer>, double[]> metricLookup = new HashMap<>();
    private Set<double[]> pareto = new HashSet<>();
    private int activeMetrics = 0;

    private final Logger logger;

    private long totalCode2vecTime = 0;
    private long totalTransformationTime = 0;

    public GenotypeSupport(){
        logger = LogManager.getLogger(GenotypeSupport.class);
    }

    public Map<List<BaseTransformer>, double[]> getMetricLookup() {
        return metricLookup;
    }

    public boolean getMaximize() {
        return maximize;
    }

    /**
     * Get the base seed used for this run.
     * @return the seed.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Setter for the config file field.
     * @param config the string to set.
     */
    public void setConfigFile(String config) {
        configFile = config;
    }

    /**
     * Get the amount of active metrics.
     * @return the amount of active metrics.
     */
    public int getActiveMetrics(){
        return activeMetrics;
    }

    public String getDataDir() {
        return dataDir;
    }

    /**
     * Get the objectives of all the metrics.
     * @return the objective array.
     */
    public Boolean[] getObjectives() {
        return objectives;
    }

    /**
     * Replace contents in dataDir with the contents in data.
     * @param data the new data.
     */
    public void setDataDir(String data) {
        try {
            String path = dataDir + "generation_0";
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            File newDir = new File(data);
            File[] newFiles = newDir.listFiles();
            if (newFiles != null) {
                for (File f : newFiles) {
                    Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(path + "/" + f.getName()));
                }
            }
        } catch (IOException e) {
            logger.warn("Files couldn't be moved to data directory");
        }
    }

    /**
     * Get secondary metrics.
     * @return a list of secondary metrics.
     */
    public List<Metric> getSecondaryMetrics() {
        return secondaryMetrics;
    }

    /**
     * Getter for the Pareto set.
     * @return the Pareto set.
     */
    public Set<double[]> getPareto() {
        return pareto;
    }

    /**
     * Set the pareto set.
     * @param newPareto the new pareto set.
     */
    public void setPareto(Set<double[]> newPareto) {
        pareto = newPareto;
    }

    /**
     * Setter for the maximize field
     * @param max the boolean to set.
     */
    public void setMaximize(boolean max) {
        maximize = max;
        for(int i = 0; i < metricWeights.size(); i++) {
            objectives[i] = max;
        }
    }

    /**
     * Get the current dataset used as a baseline.
     * @return the dataset.
     */
    public String getCurrentDataset() {
        return currentDataset;
    }

    /**
     * Get total time spent of Coded2vec inference.
     * @return the total time spent.
     */
    public long getTotalCode2vevTime(){
        return totalCode2vecTime;
    }

    /**
     * Get total time spent on transformations operations.
     * @return the total time spent.
     */
    public long getTotalTransformationTime() {
        return totalTransformationTime;
    }

    /**
     * Get the initialized metrics.
     * @return the list of metrics.
     */
    public List<Metric> getMetrics() {
        return metricList;
    }

    /**
     * Get the metric weights.
     * @return the list of metric weights.
     */
    public List<Float> getWeights() {
        return metricWeights;
    }

    /**
     * Get the directory corresponding to the given genotype.
     * @param genotype the list of transformers.
     * @return the directory string if it exists, null otherwise.
     */
    public Optional<String> getDir(List<BaseTransformer> genotype) {
        String file = fileLookup.get(genotype);
        return Optional.ofNullable(file);
    }

    /**
     * Create a key value pair of an individual and the corresponding fitness.
     * @param indiv the individual.
     * @param fitness the fitness score.
     */
    public void fillFitness(List<BaseTransformer> indiv, double[] fitness) {
        metricLookup.put(indiv, fitness);
    }

    /**
     *Get the fitness score corresponding to a given genotype.
     * @param genotype the genotype.
     * @return the fitness score.
     */
    public Optional<double[]> getMetricResult(List<BaseTransformer> genotype) {
        double[] file = metricLookup.get(genotype);
        return Optional.ofNullable(file);
    }

    /**
     * Store the current genotype together with the fitness and filename in the map for later reference.
     * @param genotype the list of transformers.
     * @param fileName the file name.
     * @param score the fitness score.
     */
    public void storeFiles(List<BaseTransformer> genotype, String fileName, double[] score) {
        fileLookup.put(genotype, fileName);
        metricLookup.put(genotype, score);
    }

    /**
     * Remove previously used directories.
     */
    public void removeOtherDirs() {
        File toDelete = new File(dataDir);
        File[] entries = toDelete.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (!entry.getName().equals(currentDataset)) {
                    deleteDirectory(entry);
                }
            }
        }
    }

    /**
     * Delete directory and all its contents.
     * @param directoryToBeDeleted the directory to be deleted.
     */
    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    /**
     * Initialize global fields with config file data.
     */
    public Properties initializeFields() {
        try (InputStream input = new FileInputStream(configFile)) {

            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(prop.get("Optimization_objective") != null)
            maximize = prop.get("Optimization_objective").equals("max");
        if(prop.get("seed") != null)
            if(Long.parseLong(prop.getProperty("seed")) == -1)
                seed = new Random().nextLong();
            else
                seed = Long.parseLong((String) prop.get("seed"));
        if(prop.get("removeAllComments")!=null){
            removeAllComments = Boolean.parseBoolean((String) prop.get("removeAllComments"));
        }
        if(prop.get("transformationscope") != null){
            transformationScope = Engine.TransformationScope.valueOf(prop.getProperty("transformationscope"));
            if(!prop.getProperty("transformationscope").equals("global"))
                logger.warn("Transformation scope is not global, this might not be desired.");
        }
        if(prop.get("bash") != null)
            path_bash = (String)prop.get("bash");
        for(MetricCategory metricCategory: MetricCategory.values()) {
            metricList.add(createMetric(metricCategory.name()));
        }
        for(MetricCategory i: MetricCategory.values()) {
            metricWeights.add(Float.parseFloat(prop.getProperty(i.name())));
        }
        for(SecondaryMetrics metric: SecondaryMetrics.values()) {
            if(prop.getProperty(metric.name()).equals("1")) {
                Metric m = createMetric(metric.name());
                m.setObjective("max");
                secondaryMetrics.add(m);
            } else if(prop.getProperty(metric.name()).equals("-1")) {
                Metric m = createMetric(metric.name());
                m.setObjective("min");
                secondaryMetrics.add(m);
            }
        }
        removeZeroWeights();
        normalizeWeights();
        activeMetrics = metricWeights.size() + secondaryMetrics.size();
        objectives = new Boolean[activeMetrics];
        for(int i = 0; i < activeMetrics; i++) {
            if(i < metricWeights.size())
                objectives[i] = maximize;
            else
                objectives[i] = secondaryMetrics.get(i-metricWeights.size()).getObjective().equals("max");
        }
        return prop;
    }

    /**
     * Clear the lists for the next tests.
     */
    public void clearLists() {
        metricWeights.clear();
        metricList.clear();
    }

    /**
     * Normalizes the weights and ensures that there is at least one metric enabled.
     */
    private void normalizeWeights() {
        float sum = 0;
        for(float i: metricWeights)
            sum += i;
        System.out.println(sum);
        if(sum > 0.0) {
            for (int j = 0; j < metricWeights.size(); j++)
                metricWeights.set(j, metricWeights.get(j) / sum);
        } else {
            logger.error("Combined weight is zero. There should be at least one metric enabled.");
            throw new IllegalArgumentException("There should be at least one metric enabled.");
        }
    }

    /**
     * Add to the Pareto set if no solution dominates the current solution.
     * @param solution the current solution.
     */
    public void addToParetoOptimum(double[] solution) {
        if(isIn(pareto, solution))
            return;
        for(double[] i: pareto) {
            if (paretoDominant(i, solution)) {
                return;
            }
        }
        List<double[]> toRemove = new ArrayList<>();
        for(double[] i: pareto) {
            // if solution is dominant over i then delete i
            if (paretoDominant(solution, i)) {
                toRemove.add(i);
            }
        }
        toRemove.forEach(pareto::remove);
        pareto.add(solution);
    }

    /**
     * Support method to see if a set has a certain element in it.
     * @param set the set.
     * @param find the element.
     * @return Whether the set has the find element in it.
     */
    public boolean isIn(Set<double[]> set, double[] find) {
        Iterator<double[]> i = set.iterator();
        while(i.hasNext()) {
            if(Arrays.equals(i.next(), find)){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if solutionA is Pareto dominant over solutionB.
     * @param solutionA a solution.
     * @param solutionB a solution.
     * @return whether solutionA is pareto dominant over solutionB.
     */
    private boolean paretoDominant(double[] solutionA, double[] solutionB) {
        boolean dominant = false;
        for(int i = 0; i < solutionA.length; i++) {
            if(objectives[i]) {
                if(solutionA[i] < solutionB[i])
                    return false;
                if(solutionA[i] > solutionB[i])
                    dominant = true;
            } else {
                if(solutionA[i] > solutionB[i])
                    return false;
                if(solutionA[i] < solutionB[i])
                    dominant = true;
            }
        }
        return dominant;
    }

    /**
     * Remove all metrics that have a weight of zero.
     * These do not have to be calculated or initialized.
     */
    private void removeZeroWeights() {
        List<Integer> toRemove = new ArrayList<>();
        for(int i = 0; i < metricWeights.size(); i++) {
            if(metricWeights.get(i) <= 0) {
                toRemove.add(i);
            }
        }
        for(int i = toRemove.size()-1; i >= 0; i--){
            metricList.remove((int)toRemove.get(i));
            metricWeights.remove((int)toRemove.get(i));
        }
    }

    /**
     * Create a new metric from the specified name.
     * @param name the metric name.
     * @return The new metric.
     */
    private Metric createMetric(String name) {
        switch (name) {
            case "MRR":
                return new MRR();
            case "F1_score":
                return new F1_score();
            case "Percentage_MRR":
                return new Percentage_MRR();
            case "Precision":
                return new Precision();
            case "Recall":
                return new Recall();
            case "Edit_distance":
                return new EditDistance();
            case "Input_length":
                return new InputLength();
            case "Prediction_length":
                return new PredictionLength();
            case "Number_of_transformations":
                return new Transformations();
            default:
                logger.error("Metric name not a correct metric.");
                throw new IllegalArgumentException("Metric name not a correct metric.");
        }
    }

    /**
     * Generate random string for the intermediate dataset names name.
     * @return the directory name.
     */
    private String generateRandomString() {
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
    public String runTransformations(List<BaseTransformer> transformers, String input) {
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
        writeAST(result, launcher);

        long diff = (System.currentTimeMillis() - start) / 1000;
        totalTransformationTime += diff;
        logger.info("Transformations of this individual took: " + diff + " seconds");
        return outputSet;
    }

    /**
     * Write the ast to file.
     * @param engineResult the engine result that we write to file.
     * @param launcher the launcher.
     */
    public void writeAST(EngineResult engineResult, Launcher launcher) {
        if (engineResult.getWriteJavaOutput()) {
            logger.debug("Starting to pretty-print  altered files to " + engineResult.getOutputDirectory());
            launcher.setSourceOutputDirectory(engineResult.getOutputDirectory());
            launcher.prettyprint();
        } else {
            logger.info("Writing the java files has been disabled for this run.");
        }
    }

    /**
     * Run all scripts from the code2vec project.
     * This includes first preprocessing the code files and then evaluating the model.
     * @param dataset The name of the dataset.
     */
    public void runCode2vec(String dataset) {
        logger.info("Starting code2vec inference");
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
        logger.info("Code2vec inference of this generation took: " + diff + " seconds");
    }

    /**
     * Create the correct directories for the code2vec application.
     * @param path path to the dataset.
     */
    private void createDirs(String path) {
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
    private void removeSubDirs(File toDir, File currDir) {
        File[] files = currDir.listFiles();
        if(files!=null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    removeSubDirs(toDir, file);
                    file.delete();
                } else {
                    file.renameTo(new File(toDir, file.getName()));
                }
            }
        }
    }

    /**
     * This method runs a command from the code2vec directory.
     * @param comm the command to be run.
     */
    private void runCode2VecCommand(String comm) {
        runBashCommand(comm, 0);
    }

    /**
     * This method runs a given command in git bash and prints the results.
     * @param command the command to be run in git bash.
     */
    private void runBashCommand(String command, Integer countFailed) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(dir_path + "/code2vec/"));
            processBuilder.command(path_bash, "-c", command);

            Process process = processBuilder.start();

            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while(reader.ready() && (line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(" --- Command run successfully");
            } else {
                if(countFailed < 5) {
                    TimeUnit.SECONDS.sleep(1);
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
