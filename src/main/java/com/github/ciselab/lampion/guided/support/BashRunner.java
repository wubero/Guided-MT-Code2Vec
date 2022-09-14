package com.github.ciselab.lampion.guided.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class runs all the bash related actions. This is needed for the interaction with code2vec.
 * If you want to use this class make sure that the path_bash is up to date for your own bash executable.
 */
public class BashRunner {

    private String path_bash = "C:/Program Files/Git/bin/bash.exe";
    private final Logger logger = LogManager.getLogger(BashRunner.class);

    public void setPath_bash(String path) {
        path_bash = path;
    }

    /**
     * This method runs a command from the code2vec directory.
     * @param comm the command to be run.
     */
    public void runCommand(String comm) {
        runBashCommand(comm, 0);
    }

    /**
     * This method runs a given command in git bash and prints the results.
     * @param command the command to be run in git bash.
     */
    private void runBashCommand(String command, Integer countFailed) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(GenotypeSupport.dir_path + "/code2vec/"));
            processBuilder.command(path_bash, "-c", command);

            Process process = processBuilder.start();

            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                // Can be used for debugging the code2vec commands.
                logger.debug(line);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                logger.trace(" --- Command run successfully");
            } else {
                if(countFailed < 5) {
                    TimeUnit.SECONDS.sleep(1);
                    runBashCommand(command, countFailed+1);
                } else {
                    logger.debug("The command: " + command + "\n does not succed after 5 tries, quiting the system.");
                    System.exit(1);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.debug(" --- Interruption in RunCommand: " + e);
            // Restore interrupted state
            Thread.currentThread().interrupt();
        }
    }
}
