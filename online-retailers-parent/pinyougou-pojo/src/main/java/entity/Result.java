package entity;

import java.io.Serializable;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package entity
 * @company www.itheima.com
 */
public class Result implements Serializable {
    private boolean success;//是否成功的标志

    private String message;//信息

//    private Object data ;

    public Result() {//alt+insert
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
