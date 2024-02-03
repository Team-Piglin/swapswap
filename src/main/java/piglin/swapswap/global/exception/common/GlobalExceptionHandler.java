package piglin.swapswap.global.exception.common;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import piglin.swapswap.global.exception.coupon.DuplicateCouponTypeException;
import piglin.swapswap.global.exception.ajax.AjaxRequestException;
import piglin.swapswap.global.exception.jwt.JwtInvalidException;
import piglin.swapswap.global.exception.jwt.NoJwtException;
import piglin.swapswap.global.exception.jwt.UnsupportedGrantTypeException;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(DuplicateCouponTypeException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicateCouponTypeException(
            DuplicateCouponTypeException e) {

        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST,
                List.of(e.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected RedirectView handleValidationException(
            MethodArgumentNotValidException e) {

        log.error("ValidationException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(NoJwtException.class)
    protected RedirectView handleNoJwtException(NoJwtException e) {

        log.error("NoJwtException", e);

        return new RedirectView("error/errorpage");
    }

    @ResponseBody
    @ExceptionHandler(AjaxRequestException.class)
    protected ResponseEntity<String> handleAjaxRequestException(AjaxRequestException e) {

        log.error("AjaxRequestException", e);

        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(UnsupportedGrantTypeException.class)
    protected RedirectView handleUnSupportedGrantTypeException(
            UnsupportedGrantTypeException e) {

        log.error("UnSupportedGrantTypeException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(JwtInvalidException.class)
    protected RedirectView handleJwtInvalidException(JwtInvalidException e) {

        log.error("JwtInvalidException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(BindException.class)
    protected RedirectView handleBindException(BindException e) {

        log.error("handleBindException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected RedirectView handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {

        log.error("HttpRequestMethodNotSupportedException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(BusinessException.class)
    protected RedirectView handleConflict(BusinessException e) {

        log.error("BusinessException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    protected RedirectView handleHttpMessageConversionException(
            HttpMessageConversionException e) {

        log.error("HttpMessageConversionException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected RedirectView handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) {

        log.error("HttpMediaTypeNotSupportedException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(Exception.class)
    protected RedirectView handleException(Exception e) {

        log.error("Exception", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(DataAccessException.class)
    protected RedirectView handleDataAccessException(DataAccessException e) {
        log.error("DataAccessException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(NullPointerException.class)
    protected RedirectView handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected RedirectView handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException", e);
        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(SecurityException.class)
    protected RedirectView handleSecurityException(SecurityException e) {
        log.error("SecurityException", e);

        return new RedirectView("error/errorpage");
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected RedirectView handlerNoSuchElementFoundException(NoSuchElementException e) {
        log.error("NoSuchElementFoundException", e);

        return new RedirectView("error/errorpage");
    }
}
