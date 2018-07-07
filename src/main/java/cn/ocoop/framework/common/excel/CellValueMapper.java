package cn.ocoop.framework.common.excel;


public interface CellValueMapper {
    Object map(String value) throws InvalidValueException;
}
