package cn.ocoop.framework.common.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class PrintWriterWrapper extends PrintWriter {
    private StringWriter stringWriter = new StringWriter();

    public PrintWriterWrapper(Writer out) {
        super(out);
    }

    @Override
    public void write(int c) {
        super.write(c);
        stringWriter.write(c);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        super.write(buf, off, len);
        stringWriter.write(buf, off, len);
    }

    @Override
    public void write(char[] buf) {
        super.write(buf);
        try {
            stringWriter.write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
        stringWriter.write(s, off, len);
    }

    @Override
    public void write(String s) {
        super.write(s);
        stringWriter.write(s);
    }

    public String getOutputString() {
        return stringWriter.toString();
    }


}
