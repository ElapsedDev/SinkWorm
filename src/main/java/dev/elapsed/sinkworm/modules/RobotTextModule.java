package dev.elapsed.sinkworm.modules;

import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.utility.WebhookReport;
import spark.Request;

import java.util.logging.Logger;

public class RobotTextModule implements RouteResponse {

    @Override
    public Object handle(spark.Request request, spark.Response response) throws Exception {

        Logger.getLogger(Configurations.LOGGER_TITLE).info("robots.txt accessed from IP: " + request.ip());

        String robotsTxt = "User-agent: *\nDisallow: /admin/\nDisallow: /login/\nDisallow: /private/\n";
        response.type("text/plain");

        QueryData data = SinkWorm.getInstance().registerQuery(request, response);
        WebhookReport.sendOutAlert("Robot.txt Access", data, request);

        return robotsTxt;
    }

    @Override
    public boolean canHandle(Request request) {

        String path = request.pathInfo().toLowerCase();

        return path.equals("/robots.txt");
    }

}
