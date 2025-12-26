package dev.elapsed.sinkworm.modules.queries.payloads;

import java.util.LinkedHashMap;

public record SummaryPayload(
        int addressCount,
        long connectionCount,
        int pathCount,
        int metaDataCollected,
        long lastAttemptTimestamp,
        LinkedHashMap<String, Integer> paths
) {}