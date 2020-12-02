package cz.agentes.aressearch.utils;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SevenZUtils {

    public static void unzipSevenZFile(String sourceFile, String targetDir) {

        try (SevenZFile sevenZFile = new SevenZFile(new File(sourceFile))) {

            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                File file = new File(targetDir + "/" + entry.getName());
                String dir = file.getPath().toString().substring(0, file.toPath().toString().lastIndexOf("\\"));
                Files.createDirectories(new File(dir).toPath());

                byte[] content = new byte[(int)entry.getSize()];
                sevenZFile.read(content);

                Files.write(file.toPath(), content);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
