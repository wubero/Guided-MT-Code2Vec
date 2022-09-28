package com.github.ciselab.lampion.guided.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class does all the file management for this project.
 */
public class FileManagement {

    private static final Logger logger = LogManager.getLogger(FileManagement.class);

    public static final String dataDir = "DEPRECATED";


    /**
     * Due to Code2Vec Logic, all Code2Vec Results are produced in the same place.
     * This means, to persist them over multiple files, we have to save them somewhere else.
     * This method copies the contents of Code2Vec (placed under FileManagent.dataDir) to the folder passed as argument.
     */
    /*
    public static void copyWorkingDirectoryToOtherPath(String origin, String target) {
        logger.debug("Trying to copy " + origin + " to " + target);
        try {
            //String path = Path.of(origin,"gen0").toAbsolutePath().toString();
            String path = Path.of(origin).toAbsolutePath().toString();
            File dir = new File(path);
            if(!dir.exists()) {
                if(dir.mkdirs())
                    logger.debug("Created directory ("+path+")necessary for the data.");
            }
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            File newDir = new File(target);
            File[] newFiles = newDir.listFiles();
            if (newFiles != null) {
                for (File f : newFiles) {
                    Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(path + "/" + f.getName()));
                }
            }
        } catch (IOException e) {
            logger.warn("Files couldn't be moved to data directory ("+origin+"->"+target+")");
            e.printStackTrace();
        }
    }

     */
    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        logger.debug("Copying " + sourceDirectoryLocation + " to " + destinationDirectoryLocation);
        //String path = Path.of(sourceDirectoryLocation).toAbsolutePath().toString();
        File dir = new File(sourceDirectoryLocation);
        if(!dir.exists()) {
            if(dir.mkdirs())
                logger.debug("Created directory ("+sourceDirectoryLocation+")necessary for the data.");
        }

        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        logger.error(e);
                    }
                });
    }

    /**
     * Create the correct directories for the code2vec application.
     * @param path path to the dataset.
     */
    public static boolean createDirs(String path) {
        File valDir = new File(path + "/validation");
        File trainingDir = new File(path + "/training");
        boolean val = valDir.mkdir();
        boolean train = trainingDir.mkdir();
        return val && train;
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
