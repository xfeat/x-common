package cn.ocoop.framework.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneratorHelp {
    public static final Map<String, String> db2JavaType = Maps.newHashMap();
    public static final Pattern COLUMN_PATTERN = Pattern.compile("`(.*?)`\\s(\\w+)(\\(.*?\\))?\\s.*?(COMMENT\\s'(.*?)')?,");
    public static final Pattern TABLE_PATTERN = Pattern.compile("(?i)CREATE\\sTABLE\\s`(.*?)`");
    public static final Pattern CAMEL_FIRST_CHAR_PATTERN = Pattern.compile("_(\\w)");
    public static final Pattern TABLE_COMMENT_PATTERN = Pattern.compile("COMMENT='(.*?)';");

    static {
        db2JavaType.put("bigint", "long");
        db2JavaType.put("int", "int");
        db2JavaType.put("tinyint", "int");
        db2JavaType.put("char", "String");
        db2JavaType.put("varchar", "String");
        db2JavaType.put("text", "String");
        db2JavaType.put("date", "LocalDate");
        db2JavaType.put("datetime", "LocalDateTime");
        db2JavaType.put("decimal", "BigDecimal");
    }


    public static void main(String[] args) {
        String sql = "CREATE TABLE `flex_job_item` (\n" +
                "  `JOB_ID` bigint(20) NOT NULL,\n" +
                "  `ID` bigint(20) NOT NULL,\n" +
                "  `QUANTITY` int(11) DEFAULT NULL COMMENT '需求数量',\n" +
                "  `DATE_BEGIN` date DEFAULT NULL COMMENT '工作日期开始',\n" +
                "  `DATE_END` date DEFAULT NULL COMMENT '工作日期结束',\n" +
                "  `TIME_BEGIN` time DEFAULT NULL COMMENT '工作时间开始',\n" +
                "  `TIME_END` time DEFAULT NULL COMMENT '工作时间结束',\n" +
                "  `PROVINCE_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作省份编码',\n" +
                "  `PROVINCE_NAME` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作省份名称',\n" +
                "  `CITY_CODE` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作城市编码',\n" +
                "  `CITY_NAME` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作城市名称',\n" +
                "  `ADDRESS` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作详细地址',\n" +
                "  `ORDER` int(11) DEFAULT NULL COMMENT '显示排序 升序排列',\n" +
                "  PRIMARY KEY (`ID`),\n" +
                "  KEY `JOB_ID` (`JOB_ID`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位工作地点,时间及招聘需求信息表';";

        printInfo(sql);
    }


    public static void printInfo(String sql) {
        StringBuilder modelBuilder = new StringBuilder();

        StringBuilder insertBuilder = new StringBuilder();
        List<String> insertColumns = Lists.newArrayList();
        List<String> insertProperties = Lists.newArrayList();

        StringBuilder selectBuilder = new StringBuilder();
        List<String> selectColumns = Lists.newArrayList();


        Matcher tableMatcher = TABLE_PATTERN.matcher(sql);
        tableMatcher.find();
        Matcher tableCommentMatcher = TABLE_COMMENT_PATTERN.matcher(sql);
        boolean hasComment = tableCommentMatcher.find();

        if (hasComment) {
            modelBuilder.append("@ApiModel(value = \"").append(tableCommentMatcher.group(1)).append("\")\n");
        }
        modelBuilder.append("@Data\npublic class ");
        modelBuilder.append(getName(StringUtils.capitalize(tableMatcher.group(1).replaceFirst("\\w_", "")))).append(" {\n");

        insertBuilder.append("INSERT INTO ").append(tableMatcher.group(1).toUpperCase()).append("(");
        selectBuilder.append("SELECT ");

        Matcher columnMatcher = COLUMN_PATTERN.matcher(sql);
        while (columnMatcher.find()) {
            String comment = columnMatcher.group(5);
            String type = db2JavaType.get(columnMatcher.group(2).toLowerCase());
            String name = getName(columnMatcher.group(1).toLowerCase());

            insertColumns.add(columnMatcher.group(1));
            insertProperties.add("#{" + name + "}");

            if (selectColumns.size() != 0) {
                selectColumns.add("\n      ,A." + columnMatcher.group(1));
            } else {
                selectColumns.add("\n       A." + columnMatcher.group(1));
            }

            if (columnMatcher.group(5) != null) {
                modelBuilder.append("    @ApiModelProperty(value = \"").append(comment).append("\")\n");
            }

            if ("LocalDate".equals(type)) {
                modelBuilder.append("    @JsonSerialize(using = DateSerializer.YYYY_MM_DD.class)\n");
                modelBuilder.append("    @JsonDeserialize(using = DateDeserializer.class)\n");
            } else if ("LocalDateTime".equals(type)) {
                modelBuilder.append("    @JsonSerialize(using = DateTimeSerializer.YYYY_MM_DD_HH_MM_SS.class)\n");
                modelBuilder.append("    @JsonDeserialize(using = DateTimeDeserializer.class)\n");
            }
            if ("createUserId".equals(name)) {
                modelBuilder.append("    private User createUser;\n");
            } else if ("updateUserId".equals(name)) {
                modelBuilder.append("    private User updateUser;\n");
            } else {
                modelBuilder.append("    private ").append(type).append(" ").append(name).append(";\n");
            }
        }
        modelBuilder.append("}");
        System.out.println("==================Model============================");
        System.out.println(modelBuilder.toString());

        System.out.println("==================insert============================");
        insertBuilder.append(String.join(", ", insertColumns));
        insertBuilder.append(")\nVALUES\n(");
        insertBuilder.append(String.join(", ", insertProperties));
        insertBuilder.append("\n)");
        System.out.println(insertBuilder.toString());

        System.out.println("==================select============================");
        selectBuilder.append(String.join("", selectColumns));
        selectBuilder.append("\n FROM ").append(tableMatcher.group(1).toUpperCase()).append(" A");
        System.out.println(selectBuilder.toString());

    }


    private static String getName(String name) {
        Matcher m = CAMEL_FIRST_CHAR_PATTERN.matcher(name);
        while (m.find()) {
            name = m.replaceFirst(m.group(1).toUpperCase());
            m.reset(name);
        }
        return name;
    }
}
