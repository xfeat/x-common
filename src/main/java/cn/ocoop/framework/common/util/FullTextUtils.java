package cn.ocoop.framework.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullTextUtils {
    public static Pattern imageTagPattern = Pattern.compile("(?i)(<img.*?src=[\"'])(data:image/.*?;base64,.*?)([\"'].*?>)");

    public static String convertBase64Image(String fullText, Function<String, String> srcConvertor) {
        if (StringUtils.isBlank(fullText)) return fullText;

        Matcher matcher = imageTagPattern.matcher(fullText);
        while (matcher.find()) {
            fullText = matcher.replaceFirst(matcher.group(1) + srcConvertor.apply(matcher.group(2)) + matcher.group(3));
            matcher.reset(fullText);
        }
        return fullText;
    }

    public static void main(String[] args) {
        String str = "<img src='data:image/png;base64,sdf'><img src='http://xxxx'><img src='data:image/png;base64,sdf'><img src='data:image/png;base64,sdf'><img src='data:image/png;base64,sdf'>";
        System.out.println(convertBase64Image(str, src -> {
            System.out.println(src);
            return "=====" + src;
        }));
    }
}
