package cn.ocoop.framework.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy$1MM$2dd$3HH$4mm$5ss$6";
    public static final String YYYY_MM_DD_HH_MM = "yyyy$1MM$2dd$3HH$4mm$5";
    public static final String YYYY_MM_DD_HH = "yyyy$1MM$2dd$3HH$4";
    public static final String YYYY_MM_DD = "yyyy$1MM$2dd$3";
    public static final String YYYY_MM = "yyyy$1MM$2";
    public static final String HH_MM_SS = "HH$1mm$2ss$3";
    public static final String HH_MM = "HH$1mm$2";
    private static final Map<String, Pattern> patternMap = Maps.newLinkedHashMap();

    static {
        patternMap.put(YYYY_MM_DD_HH_MM_SS, Pattern.compile("^\\d{4}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(YYYY_MM_DD_HH_MM, Pattern.compile("^\\d{4}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(YYYY_MM_DD_HH, Pattern.compile("^\\d{4}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(YYYY_MM_DD, Pattern.compile("^\\d{4}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(YYYY_MM, Pattern.compile("^\\d{4}([^\\d]+)\\d{1,2}([^\\d]?)$"));

        patternMap.put(HH_MM_SS, Pattern.compile("^\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
        patternMap.put(HH_MM, Pattern.compile("^\\d{1,2}([^\\d]+)\\d{1,2}([^\\d]?)$"));
    }

    public static void main(String[] args) {
        System.out.println(getPattern("2014/10/10 11:11:11"));
        System.out.println(getPattern("2014-10-10"));
        System.out.println(getPattern("2014-10-10 11:11"));
        System.out.println(getPattern("2014-10"));
        System.out.println(getPattern("2014年10月10日 11点11分11秒"));
        System.out.println(getPattern("11:11:11"));
        System.out.println(getPattern("11点11分"));
    }

    public static String getPattern(String dateString) {
        for (Map.Entry<String, Pattern> entry : patternMap.entrySet()) {
            Matcher matcher = entry.getValue().matcher(dateString);
            if (matcher.find()) {
                if (YYYY_MM_DD_HH_MM_SS.equals(entry.getKey())) {
                    return YYYY_MM_DD_HH_MM_SS.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2))
                            .replace("$3", matcher.group(3))
                            .replace("$4", matcher.group(4))
                            .replace("$5", matcher.group(5))
                            .replace("$6", matcher.group(6));
                }

                if (YYYY_MM_DD_HH_MM.equals(entry.getKey())) {
                    return YYYY_MM_DD_HH_MM.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2))
                            .replace("$3", matcher.group(3))
                            .replace("$4", matcher.group(4))
                            .replace("$5", matcher.group(5));
                }

                if (YYYY_MM_DD_HH.equals(entry.getKey())) {
                    return YYYY_MM_DD_HH.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2))
                            .replace("$3", matcher.group(3))
                            .replace("$4", matcher.group(4));
                }
                if (YYYY_MM_DD.equals(entry.getKey())) {
                    return YYYY_MM_DD.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2))
                            .replace("$3", matcher.group(3));
                }

                if (YYYY_MM.equals(entry.getKey())) {
                    return YYYY_MM.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2));
                }

                if (HH_MM_SS.equals(entry.getKey())) {
                    return HH_MM_SS.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2))
                            .replace("$3", matcher.group(3));
                }

                if (HH_MM.equals(entry.getKey())) {
                    return HH_MM.replace("$1", matcher.group(1))
                            .replace("$2", matcher.group(2));

                }
            }
        }
        return null;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (StringUtils.isBlank(p.getText())) {
            return null;
        }

        String pattern = getPattern(p.getText());
        if (pattern == null) throw new RuntimeException("unsupported date pattern:" + p.getText());

        return LocalDateTime.parse(p.getText(), DateTimeFormatter.ofPattern(pattern));
    }
}
