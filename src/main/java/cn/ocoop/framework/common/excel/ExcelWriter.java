package cn.ocoop.framework.common.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 *  @RequiresPermissions("pms_bmly_employ_export")
 *     @ApiOperation(value = "导出入职记录", httpMethod = "GET")
 *     @RequestMapping("/export")
 *     public void export(HttpServletResponse response, EmployListFilter filter) throws IOException {
 *         response.setContentType("application/octet-stream");
 *         response.setHeader("Content-disposition", "attachment; filename=" + new String("入职记录.xlsx".getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
 *         ExcelWriter writer = ExcelWriter.newInstance("/employExport.xlsx", 1, 0);
 *         int[] pages = new int[]{1};
 *         writer.createExcel(() -> {
 *             PageHelper.startPage(pages[0]++, 5000);
 *             List<EmployDetail> employDetails = employNewService.list(filter);
 *             if (CollectionUtils.isEmpty(employDetails)) return null;
 *
 *             List<String[]> data = Lists.newArrayList();
 *             for (EmployDetail employDetail : employDetails) {
 *
 *                 try {
 *                     data.add(
 *                             new String[]{
 *                                     employDetail.getMemberName(), employDetail.getMemberMobile(), employDetail.mapper().sex(), String.valueOf(employDetail.getAge()), employDetail.getCompanyName(),
 *                                     employDetail.getJobTitle(), employDetail.mapper().entryDate(), employDetail.mapper().quitDateDate(), employDetail.mapper().jobSource(),
 *                                     String.valueOf(employDetail.getSalary()), String.valueOf(employDetail.getSubsidy())
 *                             }
 *                     );
 *                 } catch (Exception e) {
 *                     e.printStackTrace();
 *                 }
 *
 *             }
 *
 *             return data;
 *         });
 *
 *         writer.write(response.getOutputStream());
 *     }
 */
@Slf4j
public class ExcelWriter {
    private String templatePath;
    //起始行 从0开始
    private int rowNumStart;
    //起始列 从0开始
    private int cellNumStart;
    private Workbook workbook;

    private ExcelWriter(String template, int rowNumStart, int cellNumStart) {
        this.templatePath = ExcelWriter.class.getResource(template).getPath();
        this.rowNumStart = rowNumStart;
        this.cellNumStart = cellNumStart;
    }

    public static ExcelWriter newInstance(String templatePath, int rowNumStart, int cellNumStart) {
        return new ExcelWriter(templatePath, rowNumStart, cellNumStart);
    }

    public void createExcel(DataInputChannel dataInputChannel) throws IOException {
        workbook = new SXSSFWorkbook(new XSSFWorkbook(templatePath), 10000);
        Sheet sheet = workbook.getSheetAt(0);
        List<String[]> readData;
        for (; (readData = dataInputChannel.read()) != null; ) {
            for (int dataIndex = 0; dataIndex < readData.size(); dataIndex++) {
                Row row = sheet.createRow(rowNumStart++);
                String[] rowData = readData.get(dataIndex);
                for (int propertyIndex = 0; propertyIndex < rowData.length; propertyIndex++) {
                    Cell cell = row.createCell(cellNumStart + propertyIndex, CellType.STRING);
                    cell.setCellValue(rowData[propertyIndex]);
                }
            }
        }
    }

    public void write(OutputStream outputStream) throws IOException {
        try {
            workbook.write(outputStream);
        } catch (Exception e) {
            log.error("导出Excel异常", e);
        } finally {
            workbook.close();
        }
    }

    public interface DataInputChannel {
        List<String[]> read();
    }

}
