package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeSerializer extends JsonSerializer<LocalDateTime> {
    private String pattern;

    public DateTimeSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ofPattern(pattern)));
    }

    public static class YYYY_MM_DD_HH_MM_SS extends DateTimeSerializer {
        public YYYY_MM_DD_HH_MM_SS() {
            super("yyyy-MM-dd HH:mm:ss");
        }
    }

    public static class YYYY_MM_DD_HH_MM extends DateTimeSerializer {
        public YYYY_MM_DD_HH_MM() {
            super("yyyy-MM-dd HH:mm");
        }
    }

    public static class YYYY_MM_DD_HH extends DateTimeSerializer {
        public YYYY_MM_DD_HH() {
            super("yyyy-MM-dd HH");
        }
    }

    public static class YYYY_MM_DD extends DateTimeSerializer {
        public YYYY_MM_DD() {
            super("yyyy-MM-dd");
        }
    }

    public static class YYYY_MM extends DateTimeSerializer {
        public YYYY_MM() {
            super("yyyy-MM");
        }
    }

    public static class HH_MM_SS extends DateTimeSerializer {
        public HH_MM_SS() {
            super("HH:mm:ss");
        }
    }

    public static class HH_MM extends DateTimeSerializer {
        public HH_MM() {
            super("HH:mm");
        }
    }
}
