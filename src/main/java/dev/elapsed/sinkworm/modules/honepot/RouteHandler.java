package dev.elapsed.sinkworm.modules.honepot;

import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import spark.Request;

import java.util.EnumMap;
import java.util.Random;
import java.util.logging.Logger;

public class RouteHandler implements RouteResponse {

    @Override
    public Object handle(Request request, spark.Response response) throws Exception {

        String ipAddress = request.ip();

        if (Configurations.TRUSTED_IPS.contains(ipAddress)) {
            Logger.getLogger(Configurations.LOGGER_TITLE).info("Trusted IP accessed: " + request.pathInfo() + " from IP: " + request.ip());
        } else {
            Logger.getLogger(Configurations.LOGGER_TITLE).info("Route accessed: " + request.pathInfo() + " from IP: " + request.ip());
        }

        response.type("text/plain; charset=utf-8");
        response.status(200);

        // Must be in order
        EnumMap<RouteModules, RouteResponse> modules = SinkWorm.getInstance().getModules();

        if (modules.get(RouteModules.ROBOT_TEXT).canHandle(request)) {
            return modules.get(RouteModules.ROBOT_TEXT).handle(request, response);
        }

        if (modules.get(RouteModules.FAKE_LOGIN).canHandle(request)) {
            return modules.get(RouteModules.FAKE_LOGIN).handle(request, response);
        }

        if (modules.get(RouteModules.GENERIC_RESPONSE).canHandle(request)) {
            return modules.get(RouteModules.GENERIC_RESPONSE).handle(request, response);
        }

        return new Random().nextBoolean();
    }

    @Override
    public boolean canHandle(Request request) {
        return true;
    }
}
