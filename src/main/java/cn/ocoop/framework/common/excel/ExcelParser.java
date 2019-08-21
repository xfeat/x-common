package cn.ocoop.framework.common.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.IndexedColors.RED;

/**
 * LinkedHashMap<String, CellValueMapper> cm = Maps.newLinkedHashMap();
 *         cm.put("createTime", value -> {
 *             try {
 *                 if (StringUtils.isBlank(value)) return LocalDateTime.now();
 *
 *                 return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DateTimeDeserializer.getPattern(value)));
 *             } catch (Throwable throwable) {
 *
 *             }
 *             return LocalDateTime.now();
 *         });
 *         cm.put("remarks", value -> {
 *             if (StringUtils.isBlank(value)) {
 *                 return "";
 *             }
 *             return value.trim();
 *         });
 *         cm.put("mobile", value -> {
 *             System.out.println(value);
 *             if (StringUtils.isBlank(value) || !value.trim().matches("1\\d{10}")) {
 *                 i[0]++;
 *                 throw new InvalidValueException("手机号不匹配");
 *             }
 *             return Long.parseLong(value.trim());
 *         });
 *         cm.put("name", value -> value);
 *         cm.put("idCard", value -> {
 *             if (StringUtils.isBlank(value) || value.length() != 18) return null;
 *             return value;
 *         });
 *         cm.put("birthday", value -> {
 *             try {
 *                 if (StringUtils.isBlank(value)) return null;
 *                 return LocalDate.parse(value, DateTimeFormatter.ofPattern(DateTimeDeserializer.getPattern(value)));
 *             } catch (Throwable throwable) {
 *
 *             }
 *             return null;
 *         });
 *         cm.put("sex", value -> "女".equals(value) ? "F" : "M");
 *         cm.put("education", value -> {
 *             if (StringUtils.isBlank(value)) return null;
 *
 *             if (value.contains("初中")) return "CZ";
 *             if (value.contains("高中")) return "GZ";
 *             if (value.contains("大专")) return "DZ";
 *             if (value.contains("硕士")) return "SS";
 *             if (value.contains("博士")) return "BS";
 *             return null;
 *         });
 *
 *         cm.put("nationCode", value -> {
 *             if (StringUtils.isBlank(value)) return null;
 *             String name = value.replace("族","" );
 *             if (StringUtils.isBlank(name)) return null;
 *
 *             for (String s : code_name.keySet()) {
 *                 if (s.startsWith(name)) {
 *                     return code_name.get(s);
 *                 }
 *             }
 *             return null;
 *         });
 *         cm.put("inauguralState", value -> "在职".equals(value) ? "ZZ" : "QZZ");
 *
 *
 *         ExcelParser<Member> memberExcelParser = ExcelParser.newInstance(1, 0, new FileInputStream("C:\\Users\\79407\\Desktop\\ssss.xlsx"), Member.class, cm);
 *         memberExcelParser.sax();
 *         StringBuilder sql = new StringBuilder();
 *         System.out.println(i[0]);
 *         for (DataWrapper<Member> memberDataWrapper : memberExcelParser.getData()) {
 *             Member data = memberDataWrapper.getData();
 *             sql.append("INSERT INTO member(A,ID,IMPORT_TIME,CREATE_TIME,SOURCE,STATE,MOBILE,NAME,ID_CARD,BIRTHDAY,SEX,EDUCATION,NATION_CODE,INAUGURAL_STATE,REMARKS) VALUES (")
 *                     .append("'").append("FK").append("',")
 *                     .append(Id.next()).append(",")
 *                     .append("NOW()").append(",")
 *                     .append("'").append(data.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("',")
 *                     .append("'").append("HTDR").append("',")
 *                     .append("'").append("ZC").append("',")
 *                     .append(data.getMobile()).append(",")
 *                     .append("'").append(data.getName()).append("',");
 *             if (data.getIdCard() != null) {
 *                 sql.append("'").append(data.getIdCard()).append("',");
 *             } else {
 *                 sql.append("null").append(",");
 *             }
 *             if (data.getBirthday() != null) {
 *                 sql.append("'").append(data.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("',");
 *             } else {
 *                 sql.append("null").append(",");
 *             }
 *
 *             if (data.getSex() != null) {
 *                 sql.append("'").append(data.getSex()).append("',");
 *             } else {
 *                 sql.append("null").append(",");
 *             }
 *             if (data.getEducation() != null) {
 *                 sql.append("'").append(data.getEducation()).append("',");
 *             } else {
 *                 sql.append("null").append(",");
 *             }
 *             if (data.getNationCode() != null) {
 *                 sql.append("'").append(data.getNationCode()).append("',");
 *             } else {
 *                 sql.append("null").append(",");
 *             }
 *             sql.append("'").append(data.getInauguralState()).append("',").append("'").append(data.getRemarks()).append("')  ON DUPLICATE KEY UPDATE UPDATE_TIME=UPDATE_TIME,A = 'FK'");
 *             if (data.getNationCode() != null) {
 *                 sql.append(",NATION_CODE = '" + data.getNationCode() + "'");
 *             }
 *             sql.append(";").append(System.lineSeparator());
 *         }
 *         FileUtils.write(new File("G:\\java\\workspace\\data.sql"), sql.toString(), "utf-8");
 * @param <T>
 */
@Slf4j
public class ExcelParser<T> {
    //删除行标记
    private Set<Integer> proceedRowsIndex = Collections.synchronizedSet(new TreeSet<Integer>(Comparator.reverseOrder()));

    @Getter//校验成功数据模型
    private List<DataWrapper<T>> data = Lists.newArrayList();
    //起始行 从0开始
    private int rowNumStart;
    //起始列 从0开始
    private int cellNumStart;
    //行数据要转换的类型
    private Class<T> modelClass;
    //单元格数据校验及针对modelClass属性的格式换换
    private LinkedHashMap<String, CellValueMapper> cellMapper;
    private Map<Integer, CellValueMapper> cellIndex_checker = Maps.newHashMap();
    private Workbook workbook;
    private FormulaEvaluator evaluator;

    @Getter//是否存在错误
    private boolean error;


    public ExcelParser(int rowNumStart, int cellNumStart, InputStream inputStream, Class<T> modelClass, LinkedHashMap<String, CellValueMapper> cellMapper) throws IOException, InvalidFormatException {
        this.rowNumStart = rowNumStart;
        this.cellNumStart = cellNumStart;
        this.modelClass = modelClass;
        this.cellMapper = cellMapper;

        int cellIndex = cellNumStart;
        for (CellValueMapper checker : this.cellMapper.values()) {
            cellIndex_checker.put(cellIndex++, checker);
        }

        workbook = WorkbookFactory.create(inputStream);
        evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    }

    public static <T> ExcelParser<T> newInstance(int rowNumStart, int cellNumStart, InputStream inputStream, Class<T> modelClass, LinkedHashMap<String, CellValueMapper> cellMapper) throws IOException, InvalidFormatException {
        return new ExcelParser<>(rowNumStart, cellNumStart, inputStream, modelClass, cellMapper);
    }

    public void sax() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Sheet sheet = workbook.getSheetAt(0);

        for (int rowNum = rowNumStart; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                proceedRowsIndex.add(rowNum);
                continue;
            }

            RowData rowData = new RowData();
            rowData.setIndex(rowNum);
            log.info("行号：{}", row.getRowNum());

            for (int cellIndex = cellNumStart; cellIndex < cellNumStart + cellMapper.size(); cellIndex++) {
                Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                CellData cellData = new CellData(cellIndex, cell != null ? getValue(cell) : null);

                CellValueMapper cellValueMapper = cellIndex_checker.get(cellIndex);
                Assert.notNull(cellValueMapper, "未找到合适的处理mapper");
                try {
                    cellData.setTypedValue(cellValueMapper.map((String) cellData.getValue()));
                } catch (InvalidValueException e) {
                    log.error("参数校验失败", e);
                    cellData.setMessage(e.getMessage());
                }
                rowData.addCell(cellData);
            }
            if (rowData.getCellData().stream().allMatch(cellData -> cellData.getValue() == null)) {
                //标记删除行
                proceedRowsIndex.add(rowData.getIndex());
                continue;
            }

            //生成数据模型/记录错误数据
            if (rowData.getCellData().stream().noneMatch(CellData::hasError)) {//不存在错误
                T model = ConstructorUtils.invokeConstructor(modelClass, null);

                int index = 0;
                for (String property : cellMapper.keySet()) {
                    BeanUtils.setProperty(model, property, rowData.getCellData().get(index++).getTypedValue());
                }
                data.add(new DataWrapper<>(rowData.getIndex(), model));
            } else {
                error(
                        rowData.getIndex(),
                        rowData.getCellData().stream()
                                .filter(cellData -> StringUtils.isNotBlank(cellData.getMessage()))
                                .map(CellData::getMessage)
                                .collect(Collectors.joining(";"))
                );
            }
        }


    }

    public void error(int rowIndex, String message) {
        error = true;

        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(rowIndex);
        Cell cell = row.createCell(cellNumStart + cellMapper.size());
        Font font = workbook.createFont();
        font.setColor(RED.index);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(message);
    }

    private String getValue(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    if (cell.getDateCellValue() == null) return null;
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cell.getDateCellValue());
                }

                String numString = new DecimalFormat("#.00").format(cell.getNumericCellValue());
                while (numString.endsWith("0")) {
                    numString = StringUtils.removeEnd(numString, "0");
                }
                return StringUtils.removeEnd(numString, ".");
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return getValue(evaluator.evaluateInCell(cell));
            case BLANK:
                return null;
            default:
                return null;
        }
    }


    public void proceed(int rowIndex) {
        proceedRowsIndex.add(rowIndex);
    }

    public void write(OutputStream outputStream) throws IOException {
        Sheet sheet = workbook.getSheetAt(0);
        for (Integer rowIndex : proceedRowsIndex) {
            sheet.shiftRows(Math.min(sheet.getLastRowNum(), rowIndex + 1), sheet.getLastRowNum(), -1);
        }

        workbook.write(outputStream);
        workbook.close();
    }


}
