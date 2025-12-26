package dev.elapsed.sinkworm.modules.honepot;

import spark.Request;
import spark.Route;

public interface RouteResponse extends Route {

    boolean canHandle(Request request);

}
