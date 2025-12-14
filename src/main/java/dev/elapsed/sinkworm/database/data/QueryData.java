package dev.elapsed.sinkworm.database.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class QueryData {

    private String address;
    private long firstConnection;
    private int connectionCount;

    private Map<String, Long> queryPaths;
    private Map<String, MetaData> metadata;

    public QueryData() {
        this.firstConnection = System.currentTimeMillis();
        this.connectionCount = 0;
        this.queryPaths = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    public void recordConnection() {
        this.connectionCount++;
    }

    public void recordQueryPath(String path) {
        this.queryPaths.put(path, System.currentTimeMillis());
    }

    public void addMetaData(String id, String tag, String information) {

        MetaData meta = this.metadata.getOrDefault(id, new MetaData());
        meta.getFields().put(tag, information);
        meta.setTime(System.currentTimeMillis());

        this.metadata.put(id, meta);
    }

}
