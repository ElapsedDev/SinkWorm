package dev.elapsed.sinkworm.utility;

import dev.elapsed.sinkworm.database.Configurations;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ResourceTools {

    public static String readResource(String path) {

        String directory = path.startsWith("/") ? path.substring(1) : path;

        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(directory)) {

            if (stream == null) {
                throw new Exception("Resource not found");
            }

            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {

            Logger.getLogger(Configurations.LOGGER_TITLE).severe("Failed to read resource at path: " + path + " - " + e.getMessage());

            return null;
        }
    }

    public static boolean isNullOrEmpty(String value) {

        return value == null || value.isEmpty();
    }

    public static boolean isNullOrEmpty(Integer value) {
        return value == null;
    }

}
