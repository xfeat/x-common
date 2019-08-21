package cn.ocoop.framework.common.spring;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class OutputStreamWrapper extends ServletOutputStream {
    private ServletOutputStream outputStream;
    private ByteArrayServletOutputStream buf = new ByteArrayServletOutputStream();

    public OutputStreamWrapper(ServletOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean isReady() {
        return outputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener listener) {
        outputStream.setWriteListener(listener);
        buf.setWriteListener(listener);
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        buf.write(b);
    }

    public String getOutputString() {
        try {
            return new String(buf.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {

        }
        return "获取响应内容出错";
    }
}
