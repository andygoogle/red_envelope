package com.zzhl.exception;

import com.zzhl.exception.ErrorCode;
import com.zzhl.dto.BaseResult;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 异常增强类,以JSON的形式返回给客户端
 * 异常增强类型：NullPointerException,RunTimeException,ClassCastException,
 * 　　　　　　　　 NoSuchMethodException,IOException,IndexOutOfBoundsException
 * 　　　　　　　　 以及springmvc自定义异常等，如下：
 * SpringMVC自定义异常对应的status code
 * Exception                       HTTP Status  Code
 * BindException							400 (Bad Request)
 * ConversionNotSupportedException			500 (Internal Server Error)
 * HttpMediaTypeNotAcceptableException		406 (Not Acceptable)
 * HttpMediaTypeNotSupportedException		415 (Unsupported Media Type)
 * HttpMessageNotReadableException			400 (Bad Request)
 * HttpMessageNotWritableException			500 (Internal Server Error)
 * HttpRequestMethodNotSupportedException	405 (Method Not Allowed)
 * MethodArgumentNotValidException			400 (Bad Request)
 * MissingPathVariableException			    500 (Internal Server Error)
 * MissingServletRequestParameterException	400 (Bad Request)
 * MissingServletRequestParameterException	400 (Bad Request)
 * NoHandlerFoundException					404 (Not Found)
 * NoSuchRequestHandlingMethodException	    404 (Not Found)
 * TypeMismatchException					400 (Bad Request)
 *
 * <p>Created: 2017-02-20</p>
 *
 * @author andy
 **/
@RestControllerAdvice
public class RestExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object defaultExceptionHandler(Exception e) throws Exception {
        logger.error("exception : {}", e);

        // 抛出http的异常
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        return handleException(e);
    }

    /**
     * 统一异常处理
     */
    public static BaseResult<Object> handleException(Throwable e) {
        BaseResult<Object> resultBean = new BaseResult<>();
        logger.error("{}", e);
        CodeException exception = handleRealException(e);
        resultBean.setResultCode(exception.getCode());
        resultBean.setResultMessage(exception.getMsg());
        logger.info("Controller response body : {}", resultBean.toString());
        return resultBean;
    }

    private static CodeException handleRealException(Throwable e) {
        CodeException exception;
        if (e instanceof NumberFormatException || e instanceof HttpMessageNotReadableException || e instanceof MethodArgumentNotValidException) {
            exception = new CodeException(ErrorCode.PARAMS_ERROR);
        } else if (e instanceof CodeException) {
            exception = (CodeException) e;
        } else if (e instanceof SQLException || e instanceof MyBatisSystemException || e instanceof InvalidResultSetAccessException || e instanceof BadSqlGrammarException) {
            exception = new CodeException(ErrorCode.DB_ERROR);
        } else {
            exception = new CodeException(ErrorCode.SYSTEM_ERROR);
        }
        return exception;
    }


}
