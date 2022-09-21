package com.github.ciselab.lampion.guided.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class does all the file management for this project.
 */
public class FileManagement {

    private static final Logger logger = LogManager.getLogger(FileManagement.class);
    public static String dataDir;
    //public static String dataDir = GenotypeSupport.dir_path + "/code2vec/data/";

    /**
     * Replace contents in dataDir with the contents in data.
     * @param data the new data.
     */
    public static void setDataDir(String data) {
        try {
            String path = dataDir + "generation_0";
            File dir = new File(path);
            if(!dir.exists()) {
                if(dir.mkdirs())
                    logger.info("Created directory necessary for the data.");
            }
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
            e.printStackTrace();
        }
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
