package dev.elapsed.sinkworm.modules;

import spark.Filter;
import spark.Spark;

public class CorsFilter {

    public void apply() {

        // Handle preflight requests (OPTIONS)
        Spark.options("/*", (request, response) -> {
            String reqHeaders = request.headers("Access-Control-Request-Headers");
            if (reqHeaders != null) {
                response.raw().setHeader("Access-Control-Allow-Headers", reqHeaders);
            } else {
                response.raw().setHeader(
                        "Access-Control-Allow-Headers",
                        "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin"
                );
            }

            String reqMethod = request.headers("Access-Control-Request-Method");
            if (reqMethod != null) {
                response.raw().setHeader("Access-Control-Allow-Methods", reqMethod);
            } else {
                response.raw().setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            }

            response.raw().setHeader("Access-Control-Allow-Origin", "*");

            return "OK";
        });

        Filter filter = (request, response) -> {

            if (!"OPTIONS".equalsIgnoreCase(request.requestMethod())) {

                response.raw().setHeader("Access-Control-Allow-Origin", "*");
                response.raw().setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
                response.raw().setHeader(
                        "Access-Control-Allow-Headers",
                        "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin"
                );
            }
        };

        Spark.after(filter);
    }
}