package com.meishuosoft.rag.common.exception;

import com.meishuosoft.rag.common.api.ApiResponse;
import com.meishuosoft.rag.common.web.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = httpStatus(exception.errorCode());
        return ResponseEntity.status(status)
                .body(ApiResponse.fail(exception.errorCode(), exception.getMessage(), requestId(request)));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleValidationException(Exception exception, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(ErrorCode.BAD_REQUEST, exception.getMessage(), requestId(request)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.message(), requestId(request)));
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
        return requestId == null ? null : requestId.toString();
    }

    private HttpStatus httpStatus(ErrorCode errorCode) {
        if (errorCode == ErrorCode.BAD_REQUEST) {
            return HttpStatus.BAD_REQUEST;
        }
        if (errorCode == ErrorCode.UNAUTHORIZED) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (errorCode == ErrorCode.FORBIDDEN) {
            return HttpStatus.FORBIDDEN;
        }
        if (errorCode == ErrorCode.NOT_FOUND) {
            return HttpStatus.NOT_FOUND;
        }
        if (errorCode == ErrorCode.CONFLICT) {
            return HttpStatus.CONFLICT;
        }
        if (errorCode == ErrorCode.DEPENDENCY_UNAVAILABLE) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
