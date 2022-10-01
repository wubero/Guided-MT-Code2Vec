package com.github.ciselab.lampion.guided.support;

import com.github.ciselab.lampion.core.program.EngineResult;
import com.github.ciselab.lampion.core.transformations.Transformer;
import com.github.ciselab.lampion.core.transformations.TransformerRegistry;
import com.github.ciselab.lampion.core.transformations.transformers.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.random.RandomGenerator;

import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.program.Engine;
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
    private final String initialDataset = "initialDataset";

    private Configuration config;
    private final BashRunner bashRunner;
    private final MetricCache metricCache;

    private final Logger logger = LogManager.getLogger(GenotypeSupport.class);

    private long totalCode2vecTime = 0;
    private long totalTransformationTime = 0;

    public GenotypeSupport(MetricCache cache, Configuration config) {
        metricCache = cache;
        this.config = config;
        this.bashRunner = new BashRunner(config.program);
    }

    public MetricCache getMetricCache() {
        return metricCache;
    }

    public String getInitialDataset() {
        return initialDataset;
    }

    public long getTotalCode2vevTime(){
        return totalCode2vecTime;
    }

    public long getTotalTransformationTime() {
        return totalTransformationTime;
    }

    /**
     * Create the transformer based on the key and seed specified.
     * @param key the transformer key.
     * @param seed the transformer seed.
     * @return a transformer that extends the BaseTransformer.
     */
    public Transformer createTransformers(Integer key, Integer seed) {
        switch (key) {
            case 0:
                return new IfTrueTransformer(seed);
            case 1:
                return new IfFalseElseTransformer(seed);
            case 2:
                return new RenameVariableTransformer(seed);
            case 3:
                return new AddNeutralElementTransformer(seed);
            case 4:
                return new AddUnusedVariableTransformer(seed);
            case 5:
                return new RandomParameterNameTransformer(seed);
            case 6:
                return new LambdaIdentityTransformer(seed);
            default:
                logger.error("The key provided does not match a transformer");
                throw new IllegalArgumentException("The key provided does not match a transformer.");
        }
    }

    public Transformer createRandomTransformer(RandomGenerator r){
        var constructors = config.lampion.getAvailableTransformerConstructors();

        long seed = r.nextLong();
        int index = r.nextInt(0,constructors.size());

        return constructors.get(index).apply(seed);
    }

    /**
     * Write the ast to file.
     * @param engineResult the engine result that we write to file.
     * @param launcher the launcher.
     */
    private void writeAST(EngineResult engineResult, Launcher launcher) {
        if (engineResult.getWriteJavaOutput()) {
            logger.debug("Starting to pretty-print the altered files to " + engineResult.getOutputDirectory());
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
     * @param individual a genotype, used here mostly for the represented transformers
     * @param input the input directory.
     * @return the directory which the transformation .java files are in.
     */
    public String runTransformations(MetamorphicIndividual individual, String input) {
        long start = System.currentTimeMillis();
        TransformerRegistry registry = new TransformerRegistry("fromGA");
        for(Transformer i: individual.getTransformers()) {
            if (i instanceof BaseTransformer bt)
                bt.setTryingToCompile(false);

            registry.registerTransformer(i);
        }
        String dir = individual.getGeneration() == -1 ? "initialGen" : "gen" + individual.getGeneration();
        Path generationDirectory = Path.of(config.program.getDataDirectoryPath().toAbsolutePath().toString(),dir);
        try {
            if (!Files.isDirectory(generationDirectory)){
                logger.debug("There was no Folder for Generation " + individual.getGeneration() + " creating one at " + generationDirectory.toString());
                Files.createDirectory(generationDirectory);
            }
        } catch (IOException e) {
            logger.error(e);
        }
        String individualHash = Integer.toHexString(individual.hashCode()).substring(0,6);
        Path outputDir = Path.of(generationDirectory.toAbsolutePath().toString(),individualHash);

        logger.debug("Received an Individual (" + individualHash + ") from Generation " + individual.getGeneration());
        logger.debug("Writing changed files to " + outputDir.toAbsolutePath());
        metricCache.putFileCombination(individual, outputDir.toAbsolutePath().toString());
        individual.setJavaPath(outputDir.toAbsolutePath().toString());

        Path engineInputPath = Path.of(config.program.getDataDirectoryPath().toAbsolutePath().toString(), input);
        logger.debug("Path engine input path: " + engineInputPath);
        Path engineOutputPath = Path.of( outputDir.toAbsolutePath().toString() ,"test");
        Engine engine = new Engine(engineInputPath.toAbsolutePath().toString(),engineOutputPath.toAbsolutePath().toString() , registry);

        engine.setNumberOfTransformationsPerScope(individual.getTransformers().size(), config.lampion.getTransformationScope());
        engine.setRandomSeed(config.program.getSeed());
        engine.setRemoveAllComments(config.lampion.isRemoveAllComments());

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
        return outputDir.toAbsolutePath().toString();
    }

    /**
     * Run all scripts from the code2vec project.
     * This includes first preprocessing the code files and then evaluating the model.
     *
     * Due to Code2Vec Logic, always the same file-names are re-used.
     * Invoking this method twice will overwrite existing results.
     * The result-files are copied to a "safe" location, which is (later) stored in the individual.
     *
     * @param dataset The name of the dataset
     * @param destination the path to where the results will be stored
     * @return the path to the directory containing the copied result-files
     */
    public String runCode2vec(String dataset, String destination) {
        logger.info("Starting code2vec inference");
        logger.debug("Dataset " + dataset + " -> " + destination);
        long start = System.currentTimeMillis();
        Path path = Path.of(dataset);
        logger.debug("Creating directory at " + path.toAbsolutePath());
        FileManagement.removeSubDirs(new File(path.toAbsolutePath() + "/test"), new File(path.toAbsolutePath() + "/test"));
        FileManagement.createDirs(path.toAbsolutePath().toString());
        // Preprocessing file.
        String[] datasetArray = dataset.split("/");
        String data = datasetArray[datasetArray.length-1];
        String preprocess = "source preprocess.sh " + path.toAbsolutePath() + " " + data;
        bashRunner.runCommand(preprocess);

        // move preprocessed files to correct folder
        String move = "mv  "
                + config.program.getDataDirectoryPath().toAbsolutePath() + "/" + data + "/* "
                + dataset + "/";
        bashRunner.runCommand(move);
        String del = "rmdir " + config.program.getDataDirectoryPath() + "/" + data;
        bashRunner.runCommand(del);

        // Evaluating code2vec model with preprocessed files.
        Path testDataPath = Path.of(dataset + "/" + data + ".test.c2v");
        String eval = "python3 code2vec.py --load " + config.program.getModelPath() + " --test " + testDataPath + " --logs-path eval_log.txt";
        bashRunner.runCommand(eval);
        // The evaluation writes to the result.txt file

        long diff = (System.currentTimeMillis() - start) / 1000;
        totalCode2vecTime += diff;
        logger.info("Code2vec inference of this generation took: " + diff + " seconds");

        Path resolvedDestination = Path.of(destination);

        bashRunner.runCommand("mkdir -p " + resolvedDestination);

        String[] resultFiles =
                new String[]{"./predicted_words.txt","./F1_score_log.txt","./results.txt"};
        for(String file : resultFiles){
            String copy = "cp  " + file + " " + resolvedDestination;
            bashRunner.runCommand(copy);
        }

        logger.debug("Copied results from code2vec to " + resolvedDestination.toAbsolutePath());

        return path.toAbsolutePath().toString();
    }

}
