package dev.elapsed.sinkworm.modules.queries.payloads;

import dev.elapsed.sinkworm.database.data.MetaData;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.utility.ResourceTools;

import java.util.List;
import java.util.Map;

public record QueryPayload(
        String address,
        long firstSeen,
        long lastSeen,
        int connectionCount,
        int uniquePaths,
        Map<String, List<Long>> queryPaths,
        Map<String, MetaData> metadata
) {

    public QueryPayload(QueryData data) {
        this(
                data.getAddress(),
                data.getFirstConnection(),
                data.getLastSeen(),
                data.getConnectionCount(),
                ResourceTools.safeMap(data.getQueryPaths()).size(),
                ResourceTools.safeMap(data.getQueryPaths()),
                ResourceTools.safeMap(data.getMetadata())
        );
    }
}