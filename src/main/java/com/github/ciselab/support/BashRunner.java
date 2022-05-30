package com.github.ciselab.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            while(reader.ready() && (line = reader.readLine()) != null) {
                logger.debug(line);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                logger.debug(" --- Command run successfully");
            } else {
                if(countFailed < 5) {
                    TimeUnit.SECONDS.sleep(1);
                    runBashCommand(command, countFailed+1);
                } else {
                    logger.debug("The command: " + command + "\n Will not run, quiting the system.");
                    System.exit(0);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.debug(" --- Interruption in RunCommand: " + e);
            // Restore interrupted state
            Thread.currentThread().interrupt();
        }
    }
}
