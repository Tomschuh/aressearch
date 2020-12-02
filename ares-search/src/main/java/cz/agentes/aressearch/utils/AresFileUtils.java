package cz.agentes.aressearch.utils;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class AresFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    // TODO: Assert
    public static boolean deleteFile(String path, String filename) {
        File file = new File(path + "/" + filename);

        // Check if file exist
        if (!file.exists()) {
            logger.warn("File at path '" + path + "/" + filename + "' was not present during delete file operation!" );
            return false;
        }

        // Delete file
        return FileUtils.deleteQuietly(file);
    }

    public static void moveFile(String originalPath, String originalFilename, String newPath) throws IOException {
        File file = new File(originalPath + "/" + originalFilename);

        // Check if file exist
        if(!file.exists()) {
            throw new FileExistsException("Original file doesnt exist!");
        }

        // Create new Dir
        File newDir = new File(newPath);
        // Move the physical file
        FileUtils.moveFileToDirectory(file, newDir, true);
    }
}
