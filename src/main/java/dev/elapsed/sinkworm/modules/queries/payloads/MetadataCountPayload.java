package dev.elapsed.sinkworm.modules.queries.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MetadataCountPayload {

    private String key, value;
    private int count;

    public MetadataCountPayload(String key, String value) {
        this.key = key;
        this.value = value;
        this.count = 1;
    }

    public void increment() {
        count++;
    }
}
