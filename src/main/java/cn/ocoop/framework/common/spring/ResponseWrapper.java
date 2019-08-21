package cn.ocoop.framework.common.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class ResponseWrapper extends HttpServletResponseWrapper {
    private static final String LOG_PATTERN = "响应id:{}" + System.lineSeparator() + "Response Headers:" + System.lineSeparator() + "{}" + System.lineSeparator() + "Response Body:" + System.lineSeparator() + "{}";

    private PrintWriterWrapper writerWrapper;
    private OutputStreamWrapper outputStreamWrapper;
    private String requestId;
    private String requestAcceptContentType;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response The response to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public ResponseWrapper(HttpServletResponse response, String requestId, String requestAcceptContentType) {
        super(response);
        this.requestId = requestId;
        this.requestAcceptContentType = requestAcceptContentType;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writerWrapper != null) return writerWrapper;

        writerWrapper = new PrintWriterWrapper(super.getWriter());
        return writerWrapper;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStreamWrapper != null) return outputStreamWrapper;

        outputStreamWrapper = new OutputStreamWrapper(super.getOutputStream());
        return outputStreamWrapper;
    }

    public void log() {
        log.info(
                LOG_PATTERN,
                this.requestId,
                getRequestHeaders(),
                getBody()
        );

    }

    private String getBody() {
        if (!StringUtils.containsIgnoreCase(requestAcceptContentType, "application/json") && !StringUtils.containsIgnoreCase(requestAcceptContentType, "*/*"))
            return "请求mediaType非application/json,不支持输出";

        if (writerWrapper != null) return writerWrapper.getOutputString();
        if (outputStreamWrapper != null) return outputStreamWrapper.getOutputString();
        return "没有响应内容";
    }

    public Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new TreeMap<>();

        Collection<String> headerNames = getHeaderNames();
        if (CollectionUtils.isEmpty(headerNames)) return headers;

        for (String headerName : headerNames) {
            headers.put(headerName, String.join(";", getHeaders(headerName)));
        }
        return headers;
    }
}
