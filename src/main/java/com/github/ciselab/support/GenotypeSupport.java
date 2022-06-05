package com.github.ciselab.support;

import static com.github.ciselab.support.FileManagement.dataDir;

import com.github.ciselab.lampion.core.program.Engine;
import com.github.ciselab.lampion.core.program.EngineResult;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
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

    public static final String dir_path = System.getProperty("user.dir").replace("\\", "/");
    private final String currentDataset = "generation_0";
    private final BashRunner bashRunner = new BashRunner();
    private final MetricCache metricCache;
    private final ConfigManager configManager;

    private Set<double[]> pareto = new HashSet<>();

    private final Logger logger = LogManager.getLogger(GenotypeSupport.class);

    private long totalCode2vecTime = 0;
    private long totalTransformationTime = 0;

    public GenotypeSupport(MetricCache cache) {
        metricCache = cache;
        configManager = new ConfigManager(cache, bashRunner);
    }

    public MetricCache getMetricCache() {
        return metricCache;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Set<double[]> getPareto() {
        return pareto;
    }

    public void setPareto(Set<double[]> newPareto) {
        pareto = newPareto;
    }

    public String getCurrentDataset() {
        return currentDataset;
    }

    public long getTotalCode2vevTime(){
        return totalCode2vecTime;
    }

    public long getTotalTransformationTime() {
        return totalTransformationTime;
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
            if(metricCache.getObjectives()[i]) {
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
     * Write the ast to file.
     * @param engineResult the engine result that we write to file.
     * @param launcher the launcher.
     */
    private void writeAST(EngineResult engineResult, Launcher launcher) {
        if (engineResult.getWriteJavaOutput()) {
            logger.debug("Starting to pretty-print  altered files to " + engineResult.getOutputDirectory());
            launcher.setSourceOutputDirectory(engineResult.getOutputDirectory());
            launcher.prettyprint();
        } else {
            logger.info("Writing the java files has been disabled for this run.");
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
    public String runTransformations(List<BaseTransformer> transformers, String input) {
        long start = System.currentTimeMillis();
        TransformerRegistry registry = new TransformerRegistry("fromGA");
        for(BaseTransformer i: transformers) {
            i.setTryingToCompile(false);
            registry.registerTransformer(i);
        }

        String outputSet = generateRandomString();
        metricCache.putFileCombination(transformers, outputSet);

        Engine engine = new Engine(dataDir + input, dataDir + outputSet + "/test", registry);
        engine.setNumberOfTransformationsPerScope(transformers.size(), configManager.getTransformationScope());
        engine.setRandomSeed(configManager.getSeed());
        engine.setRemoveAllComments(configManager.getRemoveAllComments());

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
     * Run all scripts from the code2vec project.
     * This includes first preprocessing the code files and then evaluating the model.
     * @param dataset The name of the dataset.
     */
    public void runCode2vec(String dataset) {
        logger.info("Starting code2vec inference");
        long start = System.currentTimeMillis();
        String path = dataDir + dataset;
        FileManagement.removeSubDirs(new File(path + "/test"), new File(path + "/test"));
        FileManagement.createDirs(path);
        // Preprocessing file.
        String preprocess = "source preprocess.sh " + path + " " + dataset;
        bashRunner.runCommand(preprocess);
        // Evaluating code2vec model with preprocessed files.
        String testData = "data/" + dataset + "/" + dataset + ".test.c2v";
        String eval = "python3 code2vec.py --load models/java14_model/saved_model_iter8.release --test " + testData + " --logs-path eval_log.txt";
        bashRunner.runCommand(eval);
        // The evaluation writes to the result.txt file

        long diff = (System.currentTimeMillis() - start) / 1000;
        totalCode2vecTime += diff;
        logger.info("Code2vec inference of this generation took: " + diff + " seconds");
    }
}
