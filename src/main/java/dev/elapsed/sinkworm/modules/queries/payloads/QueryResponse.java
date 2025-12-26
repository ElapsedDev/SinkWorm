package dev.elapsed.sinkworm.modules.queries.payloads;

public record QueryResponse<T>(String status, String code, T data) {
}
