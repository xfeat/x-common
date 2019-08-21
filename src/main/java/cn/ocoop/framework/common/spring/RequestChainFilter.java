package cn.ocoop.framework.common.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RequestChainFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("SafeFilter:请求地址:{}{}", request.getRequestURL(), StringUtils.isNotBlank(request.getQueryString()) ? "?" + request.getQueryString() : "");

        RequestWrapper requestWrapper = new RequestWrapper(request);
        requestWrapper.log();

//        ResponseWrapper responseWrapper = new ResponseWrapper(response, requestWrapper.getId(), requestWrapper.getHeader("accept"));
        filterChain.doFilter(requestWrapper, response);

//        responseWrapper.log();
    }
}
