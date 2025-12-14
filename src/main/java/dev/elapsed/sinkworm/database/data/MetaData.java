package dev.elapsed.sinkworm.database.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class MetaData {

    private long time;
    private final Map<String, String> fields = new HashMap<>();

    public MetaData() {
        this.time = System.currentTimeMillis();
    }



}
