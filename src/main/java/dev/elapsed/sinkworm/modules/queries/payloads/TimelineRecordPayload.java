package dev.elapsed.sinkworm.modules.queries.payloads;

import java.util.List;
import java.util.Map;

public class TimelineRecordPayload {

    Map<String, Integer> paths;
    List<MetadataCountPayload> metadata;

    public TimelineRecordPayload(Map<String, Integer> paths, List<MetadataCountPayload> metadata) {
        this.paths = paths;
        this.metadata = metadata;
    }

    public void addPath(String path) {
        int count = paths.getOrDefault(path, 0);
        paths.put(path, count + 1);
    }

    public void addMetaData(MetadataCountPayload meta) {
        for (MetadataCountPayload record : metadata) {
            if (record.getKey().equals(meta.getKey()) && record.getValue().equals(meta.getValue())) {
                record.increment();
                return;
            }
        }
        metadata.add(meta);
    }

}
