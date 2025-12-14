package dev.elapsed.sinkworm.modules;

import spark.Request;
import spark.Route;

public interface RouteResponse extends Route {

    boolean canHandle(Request request);

}
