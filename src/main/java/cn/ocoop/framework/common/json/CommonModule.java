package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CommonModule extends SimpleModule {
    private static JsonSerializer<Long> longSerializer = new StdSerializer<Long>(Long.class) {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeString(String.valueOf(value));
        }
    };

    {
        addSerializer(Long.TYPE, longSerializer)
                .addSerializer(Long.class, longSerializer)
                .addDeserializer(String.class, new StdDeserializer<String>(String.class) {

                    @Override
                    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        String result = StringDeserializer.instance.deserialize(p, ctxt);
                        if (StringUtils.isBlank(result)) return null;
                        return StringUtils.trim(result);
                    }
                });
    }
}
