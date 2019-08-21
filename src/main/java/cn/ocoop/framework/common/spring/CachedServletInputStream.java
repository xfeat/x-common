package cn.ocoop.framework.common.spring;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CachedServletInputStream extends ServletInputStream {
    private ByteArrayInputStream cacheInputStream;
    private ByteArrayOutputStream cacheOutputStream = new ByteArrayOutputStream();

    public CachedServletInputStream(ServletInputStream inputStream) throws IOException {

        IOUtils.copy(inputStream, cacheOutputStream);
        this.cacheInputStream = new ByteArrayInputStream(cacheOutputStream.toByteArray());
    }


    @Override
    public boolean isFinished() {
        return cacheInputStream.available() > 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }

    @Override
    public int read() throws IOException {
        return cacheInputStream.read();
    }


    @Override
    public String toString() {
        try {
            return cacheOutputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
