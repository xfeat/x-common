package cn.ocoop.framework.common.excel;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CellData {
    private int index;
    private Object value;
    private Object typedValue;
    private String message;

    public CellData(int index, Object value) {
        this.index = index;
        this.value = value;
    }

    public CellData(int index, Object value, String message) {
        this.index = index;
        this.value = value;
        this.message = message;
    }

    public boolean hasError() {
        return StringUtils.isNotBlank(message);
    }
}
