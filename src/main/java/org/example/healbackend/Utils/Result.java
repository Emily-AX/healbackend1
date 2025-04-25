package org.example.healbackend.Utils;

public class Result {

    private int code;
    private boolean result;
    private String message;
    private Object data;
    public Result() {}
    public Result(int code) {
        this.code = code;
    }
    public Result(boolean result) {
        this.result = result;
    }
    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public Result(boolean result,int code, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
        this.code=code;
    }
    public Result(boolean result,int code){
        this.result=result;
        this.code=code;
    }

    public static Result success() {
        return new Result(true,ResultCode.SUCCESS);
    }

    public static Result success(Object data) {
        return new Result(true,ResultCode.SUCCESS, "success", data);
    }

    public static Result error() {
        return new Result(false,ResultCode.ERROR);
    }
    public static Result error( String message) {
        return new Result(false,ResultCode.ERROR, message, null); 
    }

    public boolean isResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
    public Result setMessage(String message) {
        this.message = message;
        return this;
    }
    public Result setData(Object data) {
        this.data = data;
        return this;
    }
    public Result data(Object data) {
        this.data = data;
        return this;
    }
    public Result message(String message) {
        this.message = message;
        return this;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
