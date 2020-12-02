package cz.agentes.aressearch.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DownloadUtils {

    private static int CONNECTION_TIMEOUT = 2600;
    private static int READ_TIMEOUT = 2600;
    private static final Logger logger = LoggerFactory.getLogger(DownloadUtils.class);

    public static void downloadFile(String url, String destination) throws IOException {

        try {
            logger.info("Downloading...");
            FileUtils.copyURLToFile(
                    new URL(url),
                    new File(destination),
                    CONNECTION_TIMEOUT,
                    READ_TIMEOUT);
            logger.info("Downloading finished!");
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
