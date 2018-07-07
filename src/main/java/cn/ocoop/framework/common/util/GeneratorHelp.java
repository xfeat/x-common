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
        String sql = "CREATE TABLE `b_company_contract` (\n" +
                "  `COMPANY_ID` bigint(20) NOT NULL,\n" +
                "  `ID` bigint(20) NOT NULL,\n" +
                "  `PRODUCT` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务产品\\r\\nWB_RSDL:外包-人事代理\\r\\nWB_LWPQ:外包-劳务派遣\\r\\nWB_GWPQ:外包-岗位外包\\r\\nWB_YWWB:外包-业务外包\\r\\nLT:猎头\\r\\nPRO:RPO\\r\\nQYPX:企业内训\\r\\nTZ:拓展\\r\\nGLZX:管理咨询\\r\\nQT:其他',\n" +
                "  `PRICE` decimal(10,2) NOT NULL COMMENT '总价',\n" +
                "  `BEGIN_TIME` datetime NOT NULL COMMENT '执行日期 开始时间',\n" +
                "  `END_TIME` datetime NOT NULL COMMENT '执行日期 结束时间',\n" +
                "  `DEMAND_PERSON` int(10) DEFAULT NULL COMMENT '需求量',\n" +
                "  `DEMAND_DURATION` int(10) DEFAULT NULL COMMENT '需求量 时间(月)',\n" +
                "  `UNIT_PRICE` decimal(10,2) DEFAULT NULL COMMENT '单价 元/人/月',\n" +
                "  `FEE_BASE_SB` decimal(10,2) DEFAULT NULL COMMENT '社保缴费基数',\n" +
                "  `FEE_BASE_GJJ` decimal(10,2) DEFAULT NULL COMMENT '公积金缴费基数',\n" +
                "  `HAS_CONTRACT` char(1) CHARACTER SET utf8mb4 NOT NULL DEFAULT 'Y' COMMENT '是否签订合同 Y:是 N：否',\n" +
                "  `OTHER_NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对方客户名称',\n" +
                "  `OTHER_CONTRACT_NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对方合同主体名称',\n" +
                "  `OTHER_CONTACT_NAME` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对方负责人',\n" +
                "  `OTHER_CONTACT_MOBILE` bigint(11) DEFAULT NULL COMMENT '对方负责人手机',\n" +
                "  `OTHER_CONTACT_EMAIL` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对方负责人邮箱',\n" +
                "  `OTHER_ADDRESS` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对方公司地址',\n" +
                "  `OUR_NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '我方客户名称',\n" +
                "  `OUR_CONTRACT_NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '我方合同主体名称',\n" +
                "  `OUR_CONTACT_NAME` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '我方负责人',\n" +
                "  `OUR_CONTACT_MOBILE` bigint(11) DEFAULT NULL COMMENT '我方负责人手机',\n" +
                "  `OUR_CONTACT_EMAIL` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '我方负责人邮箱',\n" +
                "  `OUR_ADDRESS` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '我方公司地址',\n" +
                "  `ATTACH_URL` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件地址',\n" +
                "  `CREATE_TIME` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  `CREATE_USER_ID` bigint(20) NOT NULL,\n" +
                "  PRIMARY KEY (`ID`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业合同';\n" +
                "\n";

        printInfo(sql);
    }


    private static void printInfo(String sql) {
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
