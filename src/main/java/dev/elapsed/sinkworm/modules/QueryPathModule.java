package dev.elapsed.sinkworm.modules;

import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.utility.WebhookReport;
import spark.Request;

import java.util.Random;
import java.util.logging.Logger;

public class QueryPathModule implements RouteResponse {

    @Override
    public Object handle(spark.Request request, spark.Response response) throws Exception {

        Logger.getLogger(Configurations.LOGGER_TITLE).info("Query path accessed: " + request.pathInfo() + " from IP: " + request.ip());

        response.type("text/plain; charset=utf-8");
        response.status(200);

        QueryData data = SinkWorm.getInstance().registerQuery(request, response);
        WebhookReport.sendOutAlert("Query Path Access", data, request);

        return new Random().nextBoolean();
    }

    @Override
    public boolean canHandle(Request request) {
        return true;
    }

}
