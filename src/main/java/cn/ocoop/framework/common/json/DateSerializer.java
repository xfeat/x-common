package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateSerializer extends JsonSerializer<LocalDate> {
    private String pattern;

    public DateSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ofPattern(pattern)));
    }

    public static class YYYY_MM_DD extends DateSerializer {
        public YYYY_MM_DD() {
            super("yyyy-MM-dd");
        }
    }

    public static class YYYY_MM extends DateSerializer {
        public YYYY_MM() {
            super("yyyy-MM");
        }
    }

    public static class YYYY extends DateSerializer {
        public YYYY() {
            super("yyyy");
        }
    }

}
