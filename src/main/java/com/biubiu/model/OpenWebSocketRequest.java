package com.biubiu.model;

import lombok.Data;

/**
 * Created by zhanghaibiao on 2017/10/17.
 */
@Data
public class OpenWebSocketRequest {

    private String userCode;

    private String uniqueConnectionCode;

    private String sql;

    private Object[] params;

    private String cron;

}
