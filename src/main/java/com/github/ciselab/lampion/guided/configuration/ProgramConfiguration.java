package com.github.ciselab.lampion.guided.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * This class covers the general program configurations of the Guided-MT-Program.
 * This covers paths, seeds and general termination.
 * An additional overview of paths can be found in the
 */
public class ProgramConfiguration {

    private static Logger logger = LogManager.getLogger(ProgramConfiguration.class);
    private int maxTimeInMin = 480;
    private boolean useGA = true;

    private long seed = 2022;

    private Path directoryPath = Path.of(System.getProperty("user.dir"));
    private Path dataPath = Path.of(directoryPath.toString(),"/code2vec/data/");
    private Path bashPath = Path.of("C:/Program Files/Git/bin/bash.exe");

    private Path basePath = Path.of(System.getProperty("user.dir"));
    private Path code2vecDirectory = Path.of(directoryPath.toString(),"code2vec");
    private String modelPath = "models/java14_model/saved_model_iter8.release";
    private Integer bashRetries = 3;

    public void setModelPath(String arg) {
        if (arg == null || arg.isEmpty() || arg.isBlank()){
            throw new IllegalArgumentException("Model Path cannot be null or empty");
        }
        this.modelPath = arg;
    }

    /**
     * The path at which the Code2Vec Model is stored.
     * This refers to the .bin and .release files, not the working directory of Code2Vec
     * @return the path to the model
     */
    public String getModelPath(){
        return this.modelPath;
    }

    /**
     * The time until search algorithm finishes, in minutes.
     * Genetic Search first checks for steady generations, before checking time.
     * @return The time until search algorithm finishes, in minutes.
     */
    public int getMaxTimeInMin() {
        return maxTimeInMin;
    }

    public void setMaxTimeInMin(int maxTimeInMin) {
        if (maxTimeInMin <= 0){
            throw new IllegalArgumentException("Max Time of Experiment must be more than 0 Minutes");
        }
        this.maxTimeInMin = maxTimeInMin;
    }

    /**
     * Whether or not to use Genetic Algorithms.
     * True  = Genetic Algorithm
     * False = Random Search
     * @return true if genetic algorithm, false for random search
     */
    public boolean useGA() {
        return useGA;
    }

    public void setUseGA(boolean useGA) {
        this.useGA = useGA;
    }

    /**
     * The path at which this program runs.
     * Forms the baseline for the individuals path of saving results.
     * @return  The path at which this program runs.
     */
    public Path getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        if(directoryPath == null || directoryPath.isEmpty())
            throw new IllegalArgumentException("Data Directory cannot be null or empty");
        this.directoryPath = Path.of(directoryPath);
        logger.debug("Directory Path is set to:" + this.directoryPath.toString());
    }

    /**
     * The path to the codevec/data directory.
     * This is the primary working directory. Any element needs to be put here before Code2Vec can run inference on it.
     * As files in this directory are overwritten, we copy the results per individual to the individuals path.
     * @return the path pointing to Code2Vec data for inference
     */
    public Path getDataDirectoryPath() {
        return dataPath;
    }

    public void setDataDirectoryPath(String dataDirectoryPath) {
        if(dataDirectoryPath == null || dataDirectoryPath.isEmpty())
            throw new IllegalArgumentException("Data Directory cannot be null or empty");

        this.dataPath = Path.of(dataDirectoryPath);

        logger.debug("Data Directory set to:" + this.dataPath);
    }

    /**
     * Path to the bash.exe
     * Usually just "/bin/bash".
     * @return Path to the bash.exe
     */
    public Path getBashPath() {
        return bashPath;
    }

    public void setBashPath(String bashPath) {
        if (bashPath == null || bashPath.isEmpty())
            throw new IllegalArgumentException("BashPath cannot be null or empty");
        this.bashPath = Path.of(bashPath);
    }

    /**
     * The general seed used for engine-setup.
     * The seed is only used in these java-parts and does not affect Code2Vec.
     * @return seed used for data creation
     */
    public long getSeed(){
        return this.seed;
    }

    public void setSeed(long seed){
        this.seed = seed;
    }

    public Integer getBashRetries() {
        return this.bashRetries;
    }

    /**
     * Path to the Code2Vec Directory.
     * @return Path to the Code2Vec Directory.
     */
    public Path getCode2vecDirectory() {
        return code2vecDirectory;
    }

    public void setCode2vecDirectory(String code2vecDirectory) {
        if (code2vecDirectory == null || code2vecDirectory.isEmpty()){
            throw new IllegalArgumentException("Model Directory cannot be null or empty");
        }
        this.code2vecDirectory = Path.of(code2vecDirectory);

        logger.debug("Code2Vec Directory set to:" + this.code2vecDirectory);
    }

    /**
     * The path at which the program is run.
     * Default: System.getProperty("user.dir")
     * @return
     */
    public Path getBasePath(){
        return this.basePath;
    }

}
