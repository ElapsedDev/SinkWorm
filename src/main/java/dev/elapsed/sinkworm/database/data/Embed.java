package dev.elapsed.sinkworm.database.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Embed {

    private String title, description, footerText, timestampIso;
    private Integer color;

    private List<Field> fields = new ArrayList<>();

    public void addField(String name, String value, boolean inline) {
        fields.add(new Field(name, value, inline));
    }

    @Getter @Setter
    public static class Field {

        public String name;
        public String value;
        public boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }

}