package springboot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import springboot.error.BusinessException;
import springboot.error.EmBusinessError;
import springboot.response.CommonReturnType;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

    //处理全局的异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handleException(HttpServletRequest request,Exception ex){
        BusinessException businessException = (BusinessException) ex;
        Map<String,Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException){
            responseData.put("errorCode",businessException.getErrorCode());
            responseData.put("errorMsg",businessException.getErrorMsg());
        }else{
            responseData.put("errorCode", EmBusinessError.UNKNOW_ERROR.getErrorCode());
            responseData.put("errorMsg",EmBusinessError.UNKNOW_ERROR.getErrorMsg());
        }

        return CommonReturnType.create("fail",responseData);
    }
}
