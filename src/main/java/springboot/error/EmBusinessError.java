package springboot.error;

public enum EmBusinessError implements CommonError {
    // 定义通用的类型错误
    PARAMETER_VALIDATE_ERROR(100001,"参数不合法"),
    UNKNOW_ERROR(100002,"未知错误"),
    // 用户相关的错误
    USER_NOT_EXIST(200001,"用户不存在"),
    USER_LOGIN_FAIL(200002,"用户名或者密码不正确")
    ;

    private int errorCode;
    private String errorMsg;

    private EmBusinessError(int errorCode, String errorMsg){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}
