package dev.elapsed.sinkworm.utility;

import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.data.Embed;
import dev.elapsed.sinkworm.database.data.QueryData;
import spark.Request;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public final class WebhookReport {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void sendOutAlert(String moduleTitle, QueryData data, Request request) {

        if (Configurations.TRUSTED_IPS.contains(request.ip())) return;

        Embed embed = new Embed();

        embed.setTitle("IP Address: " + request.ip() + " [ " + moduleTitle + "]");
        embed.setDescription("**Total Queries** [" + data.getConnectionCount() + "] | **Paths Logged** [" + data.getQueryPaths().size() + "]");
        embed.addField("Method", request.requestMethod(), true);
        embed.addField("Param Count", String.valueOf(request.queryParams().size()), true);
        embed.addField("Path", (data.getQueryPaths().containsKey(request.pathInfo()) ? "[NEW] " : "[OLD] ") + request.pathInfo(), false);
        embed.addField("User Agent", request.userAgent(), false);

        if (!request.queryParams().isEmpty()) {
            embed.addField("Query Params - A", ResourceTools.translateQueryParam(request.queryMap()), false);
        }

        if (!request.params().isEmpty()) {
            embed.addField("Query Params - B", ResourceTools.translateQueryParam(request.params()), false);
        }

        if (request.splat().length >= 1) {
            embed.addField("Splat", String.join(", ", request.splat()), false);
        }

        if (!request.headers().isEmpty()) {
            embed.addField("Headers" , String.join(", ", request.headers()), false);
        }

        if (!request.body().isBlank()) {
            embed.addField("Body", request.body(), false);
        }

        embed.setFooterText(new Date().toString());
        embed.setColor(0x808080);

        WebhookReport.sendEmbed(Configurations.WEBHOOK_URL, Configurations.ALERT_BOT_USERNAME, Configurations.ALERT_BOT_AVATAR_URL, embed);
    }

    public static void sendEmbed(String webhookUrl, String username, String avatarUrl, Embed embed) {

        try {

            StringBuilder stringBuilder = new StringBuilder(512);

            stringBuilder.append('{');
            boolean comma = false;

            if (username != null && !username.isBlank()) {
                stringBuilder.append("\"username\":\"").append(extract(username)).append('"');
                comma = true;
            }

            if (avatarUrl != null && !avatarUrl.isBlank()) {

                if (comma) stringBuilder.append(',');

                stringBuilder.append("\"avatar_url\":\"").append(extract(avatarUrl)).append('"');
                comma = true;
            }

            if (comma) stringBuilder.append(',');

            stringBuilder.append("\"embeds\":[{");

            comma = false;

            if (embed.getTitle() != null && !embed.getTitle().isBlank()) {
                stringBuilder.append("\"title\":\"").append(extract(embed.getTitle())).append('"');
                comma = true;
            }

            if (embed.getDescription() != null && !embed.getDescription().isBlank()) {
                if (comma) stringBuilder.append(',');
                stringBuilder.append("\"description\":\"").append(extract(embed.getDescription())).append('"');
                comma = true;
            }

            if (embed.getColor() != 0) {
                if (comma) stringBuilder.append(',');
                stringBuilder.append("\"color\":").append(embed.getColor());
                comma = true;
            }

            if (embed.getTimestampIso() != null && !embed.getTimestampIso().isBlank()) {
                if (comma) stringBuilder.append(',');
                stringBuilder.append("\"timestamp\":\"").append(extract(embed.getTimestampIso())).append('"');
                comma = true;
            }

            if (embed.getFooterText() != null && !embed.getFooterText().isBlank()) {
                if (comma) stringBuilder.append(',');
                stringBuilder.append("\"footer\":{\"text\":\"").append(extract(embed.getFooterText())).append("\"}");
                comma = true;
            }

            List<Embed.Field> fields = embed.getFields();

            if (fields != null && !fields.isEmpty()) {

                if (comma) stringBuilder.append(',');

                stringBuilder.append("\"fields\":[");

                for (int i = 0; i < fields.size(); i++) {

                    Embed.Field field = fields.get(i);

                    if (i > 0) stringBuilder.append(',');

                    stringBuilder.append("{\"name\":\"").append(extract(field.name == null ? "" : field.name))
                            .append("\",\"value\":\"").append(extract(field.value == null ? "" : field.value))
                            .append("\",\"inline\":").append(field.inline).append('}');
                }

                stringBuilder.append(']');
            }

            stringBuilder.append("}]}");

            HttpRequest request = HttpRequest.newBuilder(URI.create(webhookUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(stringBuilder.toString(), StandardCharsets.UTF_8))
                    .build();

            CLIENT.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
        } catch (Exception ex) {
            Logger.getLogger(Configurations.LOGGER_TITLE).severe("Failed to send webhook embed: " + ex.getMessage());
        }
    }

    private static String extract(String key) {

        StringBuilder sb = new StringBuilder(key.length() + 16);

        for (int i = 0; i < key.length(); i++) {

            char c = key.charAt(i);

            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c < 0x20 ? String.format("\\u%04x", (int) c) : c);
            }
        }

        return sb.toString();
    }
}
