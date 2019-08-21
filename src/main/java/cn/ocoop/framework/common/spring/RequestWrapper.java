package cn.ocoop.framework.common.spring;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {
    private static final String LOG_PATTERN = "请求id:{}" + System.lineSeparator() + "{} {}{}" + System.lineSeparator() + "Request Headers:" + System.lineSeparator() + "{}" + System.lineSeparator() + "Request Body:" + System.lineSeparator() + "{}";
    private String requestId;
    private ServletInputStream cache;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public RequestWrapper(HttpServletRequest request) {
        super(request);
        this.requestId = UUID.randomUUID().toString();
        request.setAttribute("X-LAN-RequestId", this.requestId);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cache != null) return cache;


        cache = new CachedServletInputStream(super.getInputStream());
        return cache;
    }

    public String getId() {
        return requestId;
    }

    public void log() {

        log.info(
                LOG_PATTERN,
                this.requestId,
                getMethod(),
                getRequestURL(), StringUtils.isNotBlank(getQueryString()) ? "?" + getQueryString() : "",
                getRequestHeaders(),
                getBody()
        );
    }

    public Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new TreeMap<>();
        Enumeration<String> headerNames = getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            headers.put(key, getHeader(key));
        }
        return headers;
    }

    public String getBody() {

        if (StringUtils.containsIgnoreCase(getContentType(), "application/x-www-form-urlencoded")) {
            Map<String, String> map = Maps.newTreeMap();
            for (Map.Entry<String, String[]> stringEntry : getParameterMap().entrySet()) {
                map.put(stringEntry.getKey(), String.join(",", stringEntry.getValue()));
            }
            return map.toString();
        }

        if (!StringUtils.containsIgnoreCase(getContentType(), "application/json")) {
            return "请求mediaType非application/json,不支持输出";
        }

        try {
            return getInputStream().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "未识别";
    }


}
