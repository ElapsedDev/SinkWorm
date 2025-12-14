package dev.elapsed.sinkworm.utility;

import dev.elapsed.sinkworm.database.Configurations;
import spark.QueryParamsMap;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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

    public static String translateQueryParam(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        params.forEach((key, value) -> builder.append("").append(key).append("=").append(value).append(", "));
        return builder.toString();
    }

    public static String translateQueryParam(QueryParamsMap params) {

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String[]> entry : params.toMap().entrySet()) {
            builder.append(entry.getKey()).append("=");
            String[] values = entry.getValue();
            if (values.length == 1) {
                builder.append(values[0]);
            } else {
                builder.append("[");
                for (int i = 0; i < values.length; i++) {
                    builder.append(values[i]);
                    if (i < values.length - 1) {
                        builder.append(", ");
                    }
                }
                builder.append("]");
            }
            builder.append(", ");
        }

        return builder.toString();
    }
}
