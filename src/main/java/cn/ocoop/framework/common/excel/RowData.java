package cn.ocoop.framework.common.excel;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class RowData {
    private Integer index;
    private List<CellData> cellData = Lists.newArrayList();

    public RowData() {
    }

    public RowData(List<CellData> cellData) {
        this.cellData = cellData;
    }

    public void addCell(CellData cell) {
        cellData.add(cell);
    }
}
