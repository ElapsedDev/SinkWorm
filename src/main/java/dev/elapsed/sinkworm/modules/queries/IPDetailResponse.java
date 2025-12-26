package dev.elapsed.sinkworm.modules.queries;

import com.google.gson.Gson;
import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.QueryDatabase;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.modules.queries.payloads.QueryPayload;
import dev.elapsed.sinkworm.modules.queries.payloads.QueryResponse;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;
import java.util.regex.Pattern;

public class IPDetailResponse implements Route {

    private final Pattern addressPattern;

    public IPDetailResponse() {
        System.out.println("Initialized IPDetailResponse route");
        System.out.println("http://localhost:" + Configurations.SERVER_PORT + "/sinkworm-api/v1/ip/:address");

        // Constructor can be expanded if needed
        String ipv4 = "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)" + "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$";

        addressPattern = Pattern.compile(ipv4);
    }

    @Override
    public Object handle(Request request, Response response) {

        String address = request.params(":address");

        response.type("application/json");

        Gson gson = SinkWorm.getInstance().getPersist().getGson();

        if (address == null || address.isBlank()) {
            response.status(400);
            return gson.toJson(new QueryResponse<>("error", "IP_ADDRESS_MISSING", "No IP address provided"));
        }

        if (!isIP(address)) {
            response.status(400);
            return gson.toJson(new QueryResponse<>("error", "INVALID_IP_ADDRESS", "Invalid IP address provided"));
        }

        QueryData data = QueryDatabase.QUERIES.get(address);

        if (data == null) {
            response.status(404);
            return gson.toJson(new QueryResponse<>("error", "NO_DATA_FOUND", "No data found for the provided IP address"));
        }

        QueryPayload payload = new QueryPayload(
          data.getAddress(),
                data.getFirstConnection(),
                data.getLastSeen(),
                data.getConnectionCount(),
                data.getUniqueQueryCount(),
                data.getQueryPaths().isEmpty() ? Map.of() : data.getQueryPaths(),
                data.getMetadata().isEmpty() ? Map.of() : data.getMetadata()
        );

        response.status(200);

        return gson.toJson(new QueryResponse<>("success", "OK", payload));
    }

    public boolean isIP(String address) {
        return addressPattern.matcher(address).matches();
    }

}
