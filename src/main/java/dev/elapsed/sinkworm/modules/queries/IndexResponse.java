package dev.elapsed.sinkworm.modules.queries;

import com.google.gson.Gson;
import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.QueryDatabase;
import dev.elapsed.sinkworm.modules.queries.payloads.IndexQueryPayload;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.modules.queries.payloads.QueryPayload;
import dev.elapsed.sinkworm.modules.queries.payloads.QueryResponse;
import dev.elapsed.sinkworm.utility.ResourceTools;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IndexResponse implements Route {

    public IndexResponse() {
        System.out.println("Initialized IndexResponse route");
        System.out.println("http://localhost:" + Configurations.SERVER_PORT + "/sinkworm-api/v1/index?page=1&limit=25");
    }

    private final int DEFAULT_PAGE = 1;
    private final int DEFAULT_LIMIT = 25;
    private final int MAX_LIMIT = 200;

    @Override
    public Object handle(Request request, Response response) {

        Gson gson = SinkWorm.getInstance().getPersist().getGson();
        response.type("application/json");

        int page = ResourceTools.parseInt(request.queryParams("page"), DEFAULT_PAGE);
        int limit = ResourceTools.parseInt(request.queryParams("limit"), DEFAULT_LIMIT);
        limit = ResourceTools.clamp(limit, 1, MAX_LIMIT);

        List<QueryPayload> records = new ArrayList<>();

        for (QueryData data : QueryDatabase.QUERIES.values()) {
            records.add(new QueryPayload(data));
        }

        records.sort(Comparator.comparingLong(QueryPayload::lastSeen).reversed());

        int totalQueries = records.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalQueries / (double) limit));

        page = ResourceTools.clamp(page, 1, totalPages);

        int fromIndex = Math.min((page - 1) * limit, totalQueries);
        int toIndex = Math.min(fromIndex + limit, totalQueries);

        List<QueryPayload> pageItems = records.subList(fromIndex, toIndex);

        IndexQueryPayload payload = new IndexQueryPayload(
                page,
                limit,
                totalQueries,
                totalPages,
                page > 1,
                page < totalPages,
                pageItems
        );

        response.status(200);
        return gson.toJson(new QueryResponse<>("success", "OK", payload));
    }

}
