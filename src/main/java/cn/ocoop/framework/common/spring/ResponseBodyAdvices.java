package cn.ocoop.framework.common.spring;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@ControllerAdvice
public class ResponseBodyAdvices implements ResponseBodyAdvice {
    private static final String LOG_PATTERN = "响应id:{}" + System.lineSeparator() + "Response Headers:" + System.lineSeparator() + "{}" + System.lineSeparator() + "Response Body:" + System.lineSeparator() + "{}";

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!(((ServletServerHttpRequest) request).getServletRequest() instanceof RequestWrapper)) {
            log.info(
                    LOG_PATTERN,
                    "未知",
                    getRequestHeaders(((ServletServerHttpResponse) response).getServletResponse()),
                    JSON.toJSONString(body)
            );
            return body;
        }


        log.info(
                LOG_PATTERN,
                ((RequestWrapper) ((ServletServerHttpRequest) request).getServletRequest()).getId(),
                getRequestHeaders(((ServletServerHttpResponse) response).getServletResponse()),
                JSON.toJSONString(body)
        );
        return body;
    }

    public Map<String, String> getRequestHeaders(HttpServletResponse response) {
        Map<String, String> headers = new TreeMap<>();

        Collection<String> headerNames = response.getHeaderNames();
        if (CollectionUtils.isEmpty(headerNames)) return headers;

        for (String headerName : headerNames) {
            headers.put(headerName, String.join(";", response.getHeaders(headerName)));
        }
        return headers;
    }
}
