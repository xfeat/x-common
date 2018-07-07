package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDeserializer extends JsonDeserializer<LocalTime> {
    public static final String HH_MM_SS = "HH$1mm$2ss$3";
    public static final String HH_MM = "HH$1mm$2";
    public static final String HH = "HH$1";
    private static final Map<String, Pattern> patternMap = Maps.newLinkedHashMap();

    static {
        patternMap.put(HH_MM_SS, Pattern.compile("^\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(HH_MM, Pattern.compile("^\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(HH, Pattern.compile("^\\d{1,2}([^\\d]?)$"));
    }

    public static void main(String[] args) {
        System.out.println(getPattern("11:11:11"));
        System.out.println(getPattern("11点11分"));
        System.out.println(getPattern("11点"));
        System.out.println(getPattern("11"));
    }

    private static String getPattern(String dateString) {
        for (Map.Entry<String, Pattern> entry : patternMap.entrySet()) {
            Matcher matcher = entry.getValue().matcher(dateString);
            if (matcher.find()) {
                if (HH_MM_SS.equals(entry.getKey())) {
                    return HH_MM_SS.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2))
                            .replace("$3", matcher.group(3));
                }

                if (HH_MM.equals(entry.getKey())) {
                    return HH_MM.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2));

                }
                if (HH.equals(entry.getKey())) {
                    return HH.replace("$1", matcher.group(1));
                }
            }
        }
        return null;
    }

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (StringUtils.isBlank(p.getText())) {
            return null;
        }

        String pattern = getPattern(p.getText());
        if (pattern == null) throw new RuntimeException("不支持的时间格式:" + p.getText());

        return LocalTime.parse(p.getText(), DateTimeFormatter.ofPattern(pattern));
    }
}
