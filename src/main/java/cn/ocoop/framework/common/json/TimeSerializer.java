package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeSerializer extends JsonSerializer<LocalTime> {
    private String pattern;

    public TimeSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ofPattern(pattern)));
    }

    public static class HH_MM_SS extends TimeSerializer {
        public HH_MM_SS() {
            super("HH:mm:ss");
        }
    }

    public static class HH_MM extends TimeSerializer {
        public HH_MM() {
            super("HH:mm");
        }
    }

    public static class HH extends TimeSerializer {
        public HH() {
            super("HH");
        }
    }

}
