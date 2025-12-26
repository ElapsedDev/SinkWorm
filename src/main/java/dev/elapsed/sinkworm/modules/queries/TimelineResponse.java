package dev.elapsed.sinkworm.modules.queries;

import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.QueryDatabase;
import dev.elapsed.sinkworm.database.data.MetaData;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.modules.queries.payloads.MetadataCountPayload;
import dev.elapsed.sinkworm.modules.queries.payloads.QueryResponse;
import dev.elapsed.sinkworm.modules.queries.payloads.TimelineRecordPayload;
import dev.elapsed.sinkworm.utility.ResourceTools;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimelineResponse implements Route {

    public TimelineResponse() {

        System.out.println("Initialized TimelineResponse route");
        System.out.println("http://localhost:" + Configurations.SERVER_PORT + "/sinkworm-api/v1/timeline");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        response.type("application/json");
        response.status(200);

        Map<String, TimelineRecordPayload> path = new HashMap<>();

        for (QueryData data : QueryDatabase.QUERIES.values()) {

            for (Map.Entry<String, List<Long>> entry : data.getQueryPaths().entrySet()) {

                String pathKey = entry.getKey();
                List<Long> timestamps = entry.getValue();

                for (Long timestamp : timestamps) {

                    String date = getDate(timestamp);
                    path.computeIfAbsent(date, k -> new TimelineRecordPayload(new HashMap<>(), new ArrayList<>())).addPath(pathKey);
                }
            }

            for (Map.Entry<String, MetaData> entry : data.getMetadata().entrySet()) {

                MetaData metaData = entry.getValue();
                String date = getDate(metaData.getTime());

                for (Map.Entry<String, String> kv : metaData.getFields().entrySet()) {
                    MetadataCountPayload record = new MetadataCountPayload(kv.getKey(), kv.getValue());
                    path.computeIfAbsent(date, k -> new TimelineRecordPayload(new HashMap<>(), new ArrayList<>())).addMetaData(record);
                }
            }
        }

        return SinkWorm.getInstance().getPersist().getGson().toJson(new QueryResponse<>("success", "OK", path));
    }

    public String getDate(long timestamp) {
        return ResourceTools.formatDate(timestamp);
    }

}
