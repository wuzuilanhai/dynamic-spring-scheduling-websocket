package com.biubiu.constant;

/**
 * Created by zhanghaibiao on 2017/10/18.
 */
public enum TaskCronConstants {
    RESET_SUCCESS(200L, "重置成功"), RESET_FAIL(400L, "重置失败");

    TaskCronConstants(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    private Long code;

    private String message;

    public Long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
