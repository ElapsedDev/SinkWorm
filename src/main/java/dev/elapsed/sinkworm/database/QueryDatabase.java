package dev.elapsed.sinkworm.database;


import dev.elapsed.sinkworm.SinkWorm;
import dev.elapsed.sinkworm.database.data.QueryData;

import java.util.HashMap;
import java.util.Map;

public class QueryDatabase {

    private static transient QueryDatabase instance = new QueryDatabase();

    public static Map<String, QueryData> QUERIES = new HashMap<>();

    public static void save() {
        SinkWorm.getInstance().getPersist().save(instance, "query-database");
    }

    public static void load() {
        SinkWorm.getInstance().getPersist().loadOrSaveDefault(instance, QueryDatabase.class, "query-database");
    }

}
