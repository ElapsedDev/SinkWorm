package dev.elapsed.sinkworm.database.data;

import dev.elapsed.sinkworm.utility.ResourceTools;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class QueryData {

    private String address;
    private long firstConnection;
    private int connectionCount;

    private Map<String, List<Long>> queryPaths;
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
        this.queryPaths.computeIfAbsent(path, k -> new ArrayList<>()).add(System.currentTimeMillis());
    }

    public int getUniqueQueryCount() {
        return this.queryPaths.size();
    }

    public void addMetaData(String id, String tag, String information) {

        MetaData meta = this.metadata.getOrDefault(id, new MetaData());
        meta.getFields().put(tag, information);
        meta.setTime(System.currentTimeMillis());

        this.metadata.put(id, meta);
    }

    public long getLastSeen() {
        return ResourceTools.computeLastSeen(this);
    }

}
