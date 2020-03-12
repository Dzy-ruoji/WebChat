package com.waxsb.util.Json;

import com.waxsb.model.SocketMsg;

public  class  WsInfo {
    //所需实现(返回)的方法
    private  String methodName;
    //储存消息的对象
    private  SocketMsg socketMsg;
    //所携带的参数数组
    private  Object[] parameters;
    //返回的结果
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    private boolean isSave;

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public SocketMsg getSocketMsg() {
        return socketMsg;
    }

    public void setSocketMsg(SocketMsg socketMsg) {
        this.socketMsg = socketMsg;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
