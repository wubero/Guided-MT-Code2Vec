package com.github.ciselab.lampion.guided.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;

public class ProgramConfiguration {

    private static Logger logger = LogManager.getLogger(ProgramConfiguration.class);
    private int maxTimeInMin = 480;
    private boolean useGA = true;

    private long seed = 2022;

    private String directoryPath = System.getProperty("user.dir").replace("\\", "/");
    private String dataDirectoryPath = directoryPath + "/code2vec/data/";
    private String bashPath = "C:/Program Files/Git/bin/bash.exe";

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

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        if(directoryPath == null || directoryPath.isEmpty())
            throw new IllegalArgumentException("Data Directory cannot be null or empty");

        if(directoryPath.endsWith("/") || directoryPath.endsWith("\\"))
            this.directoryPath = dataDirectoryPath;
        else{
            this.directoryPath = dataDirectoryPath + File.separator;
        }
        this.directoryPath = Path.of(this.directoryPath).toAbsolutePath().toString();
        logger.debug("Directory Path is set to:" + this.directoryPath);
    }

    public String getDataDirectoryPath() {
        return dataDirectoryPath;
    }

    public void setDataDirectoryPath(String dataDirectoryPath) {
        if(dataDirectoryPath == null || dataDirectoryPath.isEmpty())
            throw new IllegalArgumentException("Data Directory cannot be null or empty");

        if(dataDirectoryPath.endsWith("/") || dataDirectoryPath.endsWith("\\"))
            this.dataDirectoryPath = dataDirectoryPath;
        else{
            this.dataDirectoryPath = dataDirectoryPath + File.separator;
        }
        // add data directory if it is not there yet
        if (! dataDirectoryPath.contains(this.directoryPath)){
            this.dataDirectoryPath = Path.of(getDirectoryPath(),this.dataDirectoryPath).toAbsolutePath().toString();
        }
        logger.debug("Data Directory set to:" + this.dataDirectoryPath);
    }

    public String getBashPath() {
        return bashPath;
    }

    public void setBashPath(String bashPath) {
        this.bashPath = bashPath;
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
}
