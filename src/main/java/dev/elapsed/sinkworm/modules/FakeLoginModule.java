package dev.elapsed.sinkworm.modules;

import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.utility.ResourceTools;
import dev.elapsed.sinkworm.utility.WebhookReport;
import spark.Request;
import spark.Response;

import java.util.Set;
import java.util.logging.Logger;

public class FakeLoginModule implements RouteResponse {

    private static final Set<String> HONEYPOT_KEYWORDS = Set.of("admin", "login", "panel", "auth", "wp-admin", "manager");

    @Override
    public Object handle(Request request, Response response) throws Exception {

        Logger.getLogger(Configurations.LOGGER_TITLE).info("Honeypot login attempt detected from IP: " + request.ip());

        response.type("text/html; charset=utf-8");
        response.status(200);

        QueryData data = SinkWorm.getInstance().registerQuery(request, response);
        WebhookReport.sendOutAlert("Honeypot Login Attempt", data, request);

        // Add the ability to add random page templates from public CVEs in the future.

        return ResourceTools.readResource("public/login.html");
    }

    @Override
    public boolean canHandle(Request request) {
        String path = request.pathInfo().toLowerCase();

        return HONEYPOT_KEYWORDS
                .stream()
                .anyMatch(path::contains);
    }

}
