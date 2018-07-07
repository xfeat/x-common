package cn.ocoop.framework.common.spring;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CommonExceptionAdviceHandler {


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, Throwable ex) {
        log.error("系统异常", ex);
        Map<String, String> errorMsg = Maps.newHashMap();
        errorMsg.put("title", "系统异常");
        errorMsg.put("exception", ClassUtils.getShortName(ex.getClass()));
        errorMsg.put("message", ex.getMessage());
        return new ResponseEntity<>(errorMsg, getStatus(request));
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Map<String, Object> handleBindException(HttpServletRequest request, BindException bindException) throws IOException {
        return errorProcess(request, "请求参数格式错误", buildObjectBindException(bindException.getBindingResult()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleBindException(HttpServletRequest request, MethodArgumentNotValidException bindException) throws IOException {
        return errorProcess(request, "请求参数格式错误", buildObjectBindException(bindException.getBindingResult()));
    }

    private Exception buildObjectBindException(BindingResult bindingResult) {
        return new InvalidParameterException(
                bindingResult.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getObjectName() + "." + fieldError.getField() + ":" + fieldError.getDefaultMessage() + ";")
                        .reduce(String::concat)
                        .orElse("")
        );
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleBindException(HttpServletRequest request, ConstraintViolationException bindException) throws IOException {
        return errorProcess(request, "请求参数格式错误", new InvalidParameterException(
                bindException.getConstraintViolations()
                        .stream()
                        .map(constraintViolation -> "输入的值:" + constraintViolation.getInvalidValue() + ",提示:" + constraintViolation.getMessage() + ";")
                        .reduce(String::concat)
                        .orElse("")
        ));
    }


    private Map<String, Object> errorProcess(HttpServletRequest request, String exceptionSubject, Throwable ex) throws IOException {
        log.error(exceptionSubject + ",{}", request.getRequestURL() + "?" + request.getQueryString(), ex);
        Map<String, Object> errorMsg = Maps.newHashMap();
        errorMsg.put("title", exceptionSubject);
        errorMsg.put("exceptionType", ClassUtils.getShortName(ex.getClass()));
        errorMsg.put("message", ex.getMessage());
        return errorMsg;
    }


}
