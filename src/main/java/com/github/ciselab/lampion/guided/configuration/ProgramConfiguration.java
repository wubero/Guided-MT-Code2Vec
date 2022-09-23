package com.github.ciselab.lampion.guided.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

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

    public String getModelPath(){
        return this.modelPath;
    }

    public int getMaxTimeInMin() {
        return maxTimeInMin;
    }

    public void setMaxTimeInMin(int maxTimeInMin) {
        if (maxTimeInMin <= 0){
            throw new IllegalArgumentException("Max Time of Experiment must be more than 0 Minutes");
        }
        this.maxTimeInMin = maxTimeInMin;
    }

    public boolean useGA() {
        return useGA;
    }

    public void setUseGA(boolean useGA) {
        this.useGA = useGA;
    }

    public Path getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        if(directoryPath == null || directoryPath.isEmpty())
            throw new IllegalArgumentException("Data Directory cannot be null or empty");
        this.directoryPath = Path.of(directoryPath);
        logger.debug("Directory Path is set to:" + this.directoryPath.toString());
    }

    public Path getDataDirectoryPath() {
        return dataPath;
    }

    public void setDataDirectoryPath(String dataDirectoryPath) {
        if(dataDirectoryPath == null || dataDirectoryPath.isEmpty())
            throw new IllegalArgumentException("Data Directory cannot be null or empty");

        this.dataPath = Path.of(dataDirectoryPath);

        logger.debug("Data Directory set to:" + this.dataPath);
    }

    public Path getBashPath() {
        return bashPath;
    }

    public void setBashPath(String bashPath) {
        this.bashPath = Path.of(bashPath);
    }

    public long getSeed(){
        return this.seed;
    }

    public void setSeed(long seed){
        this.seed = seed;
    }

    public Integer getBashRetries() {
        return this.bashRetries;
    }

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

    public Path getBasePath(){
        return this.basePath;
    }

}
