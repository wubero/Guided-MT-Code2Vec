package com.github.ciselab.support;

import com.github.ciselab.lampion.core.program.EngineResult;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.Launcher;

/**
 * This class does all the file management for this project.
 */
public class FileManagement {

    private static final Logger logger = LogManager.getLogger(FileManagement.class);

    /**
     * Write the ast to file.
     * @param engineResult the engine result that we write to file.
     * @param launcher the launcher.
     */
    public static void writeAST(EngineResult engineResult, Launcher launcher) {
        if (engineResult.getWriteJavaOutput()) {
            logger.debug("Starting to pretty-print  altered files to " + engineResult.getOutputDirectory());
            launcher.setSourceOutputDirectory(engineResult.getOutputDirectory());
            launcher.prettyprint();
        } else {
            logger.info("Writing the java files has been disabled for this run.");
        }
    }

    /**
     * Create the correct directories for the code2vec application.
     * @param path path to the dataset.
     */
    public static void createDirs(String path) {
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
    public static void removeSubDirs(File toDir, File currDir) {
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
     * Remove previously used directories.
     */
    public static void removeOtherDirs(String dataDir) {
        File toDelete = new File(dataDir);
        File[] entries = toDelete.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (!entry.getName().equals("generation_0")) {
                    deleteDirectory(entry);
                }
            }
        }
    }

    /**
     * Delete directory and all its contents.
     * @param directoryToBeDeleted the directory to be deleted.
     */
    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
