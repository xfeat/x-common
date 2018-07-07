package cn.ocoop.framework.common.excel;

public class InvalidValueException extends Exception {
    public InvalidValueException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
