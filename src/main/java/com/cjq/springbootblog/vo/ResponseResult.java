package com.cjq.springbootblog.vo;

/**
 * REST接口统一返回的值对象
 */
public class ResponseResult {
    //处理是否成功
    private boolean success;
    //处理后的消息提示
    private String message;
    //返回的数据
    private Object body;

    /**
     * 判断响应是否处理成功
     *
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public ResponseResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseResult(boolean success, String message, Object body) {
        this.success = success;
        this.message = message;
        this.body = body;
    }
}
