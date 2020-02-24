package com.waxsb.util.Json;

import java.io.Serializable;

/**
 * 用于封装后端返回前端数据对象
 */
public class ResultInfo implements Serializable {
    private boolean flag;//后端返回结果正常为true，发生异常返回false
    private Object data;//后端返回结果数据对象
    private String errorMsg;//发生异常的错误消息

    public ResultInfo() {
    }
    public ResultInfo(boolean flag) {
        this.flag = flag;
    }

    public ResultInfo(boolean flag, Object data) {
        this.flag = flag;
        this.data = data;
    }

    public ResultInfo(boolean flag, String errorMsg) {
        this.flag = flag;
        this.errorMsg = errorMsg;
    }

    public ResultInfo(boolean flag, Object data, String errorMsg) {
        this.flag = flag;
        this.data = data;
        this.errorMsg = errorMsg;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 表示响应成功
     */
    public static ResultInfo  ResponseSuccess(){
        return new ResultInfo(true);
    }
    public static ResultInfo  ResponseSuccess(Object data){
        return new ResultInfo(true,data);
    }
    public static ResultInfo  ResponseSuccess(String  message){
        return new ResultInfo(true,message);
    }
    public static ResultInfo  ResponseSuccess(String  message,Object data){
        return new ResultInfo(true,data,message);
    }

    /**
     * 表示响应失败
     */
    public static ResultInfo  ResponseFail(){
        return new ResultInfo(false);
    }
    public static ResultInfo  ResponseFail(String  message){
        return new ResultInfo(false,message);
    }

}
