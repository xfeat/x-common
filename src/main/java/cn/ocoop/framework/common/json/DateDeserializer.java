package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateDeserializer extends JsonDeserializer<LocalDate> {
    public static final String YYYY_MM_DD = "yyyy$1MM$2dd$3";
    public static final String YYYY_MM = "yyyy$1MM$2";
    public static final String MM_DD = "MM$1dd$2";
    public static final String YYYY = "yyyy$1";
    private static final Map<String, Pattern> patternMap = Maps.newLinkedHashMap();

    static {
        patternMap.put(YYYY_MM_DD, Pattern.compile("^\\d{4}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(YYYY_MM, Pattern.compile("^\\d{4}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(MM_DD, Pattern.compile("^\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(YYYY, Pattern.compile("^\\d{4}([^\\d]?)$"));
    }

    public static void main(String[] args) {
        System.out.println(getPattern("2014/10/10"));
        System.out.println(getPattern("2014-10-10"));
        System.out.println(getPattern("2014-10"));
        System.out.println(getPattern("2014"));
    }

    public static String getPattern(String dateString) {
        for (Map.Entry<String, Pattern> entry : patternMap.entrySet()) {
            Matcher matcher = entry.getValue().matcher(dateString);
            if (matcher.find()) {
                if (YYYY_MM_DD.equals(entry.getKey())) {
                    return YYYY_MM_DD.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2))
                            .replace("$3", matcher.group(3));
                }

                if (YYYY_MM.equals(entry.getKey())) {
                    return YYYY_MM.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2));
                }

                if (MM_DD.equals(entry.getKey())) {
                    return MM_DD.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2));
                }

                if (YYYY.equals(entry.getKey())) {
                    return YYYY.replace("$1", matcher.group(1));

                }
            }
        }
        return null;
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (StringUtils.isBlank(p.getText())) {
            return null;
        }
        
        String pattern = getPattern(p.getText());
        if (pattern == null) throw new RuntimeException("不支持的日期格式:" + p.getText());

        return LocalDate.parse(p.getText(), DateTimeFormatter.ofPattern(pattern));
    }
}
