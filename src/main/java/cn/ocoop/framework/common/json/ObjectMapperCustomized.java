package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ObjectMapperCustomized extends com.fasterxml.jackson.databind.ObjectMapper {
    {
        DefaultSerializerProvider.Impl sp = new DefaultSerializerProvider.Impl();
        this.setSerializerProvider(sp);
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        SimpleModule module = new SimpleModule();

        module.addDeserializer(String.class, new StdDeserializer<String>(String.class) {

            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String result = StringDeserializer.instance.deserialize(p, ctxt);
                if (StringUtils.isBlank(result)) return null;
                return result;
            }
        });

        registerModule(module);
    }
}
