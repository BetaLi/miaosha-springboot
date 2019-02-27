package springboot.response;

public class CommonReturnType {
    // 表示返回成功或者失败
    private String status;

    private Object data;

    public static CommonReturnType create(Object data){
        CommonReturnType type = new CommonReturnType();
        type.setStatus("success");
        type.setData(data);
        return type;
    }

    public static CommonReturnType create(String status, Object data){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(data);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
