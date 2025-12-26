package dev.elapsed.sinkworm.modules.queries;

import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.QueryDatabase;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.modules.queries.payloads.QueryResponse;
import dev.elapsed.sinkworm.modules.queries.payloads.SummaryPayload;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.LinkedHashMap;

public class SummaryResponse implements Route {

    public SummaryResponse() {
        System.out.println("Initialized SummaryResponse route");
        System.out.println("http://localhost:" + Configurations.SERVER_PORT + "/sinkworm-api/v1/summary");
    }

    @Override
    public Object handle(Request request, Response response) {

        response.type("application/json");

        int addressCount = 0;
        int connectionCount = 0;
        int pathCount = 0;
        int metaDataCollected = 0;
        long lastAttemptTimestamp = 0;
        LinkedHashMap<String, Integer> paths = new LinkedHashMap<>();

        for (QueryData data : QueryDatabase.QUERIES.values()) {

            addressCount++;
            connectionCount += data.getConnectionCount();
            pathCount += data.getUniqueQueryCount();

            if (data.getLastSeen() > lastAttemptTimestamp) {
                lastAttemptTimestamp = data.getLastSeen();
            }

            for (String path : data.getQueryPaths().keySet()) {
                paths.put(path, paths.getOrDefault(path, 0) + 1);
            }

            metaDataCollected += data.getMetadata().size();
        }

        SummaryPayload payload = new SummaryPayload(
                addressCount,
                connectionCount,
                pathCount,
                metaDataCollected,
                lastAttemptTimestamp,
                paths);

        return SinkWorm.getInstance().getPersist().getGson().toJson(new QueryResponse<>("success", "OK", payload));
    }

}
