package dev.elapsed.sinkworm.modules.queries.payloads;

import java.util.List;

public record IndexQueryPayload(int page, int limit, int totalQueries, int totalPages, boolean hasPrevious, boolean hasNext, List<QueryPayload> records) {
}
