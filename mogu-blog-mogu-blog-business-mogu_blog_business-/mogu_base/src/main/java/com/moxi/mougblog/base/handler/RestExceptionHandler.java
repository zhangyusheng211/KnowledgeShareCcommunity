package com.moxi.mougblog.base.handler;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.global.ErrorCode;
import com.moxi.mougblog.base.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: RestApi参数校验异常处理
 * @author: 陌溪
 * @date: 2019年12月4日22:48:18
 */

// zz @RestControllerAdvice 是 Spring Framework 中用于全局处理控制器异常的注解，它是 @ControllerAdvice
// 和 @ResponseBody 的组合，用于创建一个全局异常处理器，专门处理 RESTful Web 服务的异常。
// 这个注解简化了返回 JSON 响应的过程，使得异常处理器能够直接返回 JSON 格式的响应。
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    /**
     * 400错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result requestNotReadable(HttpMessageNotReadableException ex) {
        log.error("异常类 HttpMessageNotReadableException {},", ex.getMessage());
        return Result.createWithErrorMessage("参数校验异常", ErrorCode.PARAM_INCORRECT);
    }

    // z指定处理 MethodArgumentNotValidException 异常的方法。
    // z设置 HTTP 响应状态为 400 Bad Request。
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException：", e);
        // z创建一个 errMap，用于存储字段错误信息。
        Map<String, String> errMap = new HashMap<>();
        // z遍历异常的 BindingResult 对象中的字段错误，将字段名和错误信息存入 errMap.
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return ResultUtil.resultWithMessage(ErrorCode.PARAM_INCORRECT, "参数校验异常");
    }

    /**
     * 400错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({TypeMismatchException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result requestTypeMismatch(TypeMismatchException ex) {
        log.error("异常类 TypeMismatchException {},", ex.getMessage());
        return Result.createWithErrorMessage("参数校验异常", ErrorCode.PARAM_INCORRECT);
    }

    /**
     * 400错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public Result requestMissingServletRequest(MissingServletRequestParameterException ex) {
        log.error("异常类 MissingServletRequestParameterException {},", ex.getMessage());
        return Result.createWithErrorMessage("参数校验异常", ErrorCode.PARAM_INCORRECT);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBindException(BindException e) {
        log.error("异常类BindException：", e);
        Map<String, String> errMap = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return ResultUtil.resultWithMessage(ErrorCode.PARAM_INCORRECT, "参数校验异常");
    }

    /**
     * 405错误
     *
     * @return
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result request405() {
        log.error("异常类 HttpRequestMethodNotSupportedException ");
        return Result.createWithErrorMessage("参数校验异常", ErrorCode.PARAM_INCORRECT);
    }


    /**
     * 415错误
     *
     * @return
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public Result request415(HttpMediaTypeNotSupportedException ex) {
        log.error("异常类 HttpMediaTypeNotSupportedException {}", ex.getMessage());
        return Result.createWithErrorMessage("参数校验异常", ErrorCode.PARAM_INCORRECT);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleSQLException(SQLException ex) {
        log.error("异常类SQLException: ", ex);
        return ResultUtil.errorWithMessage("访问数据异常");
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return ResultUtil.resultWithMessage(ECode.FORBIDDEN, "没有权限，请联系管理员授权");
    }
}
