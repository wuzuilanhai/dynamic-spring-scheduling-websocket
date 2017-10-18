package com.biubiu;

import com.biubiu.constant.TaskCronConstants;
import com.biubiu.support.handler.DynamicSchedulingHandler;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhanghaibiao on 2017/10/18.
 */
@RestController
@RequestMapping("/schedule")
public class DynamicSchedulingController {

    @Autowired
    private DynamicSchedulingHandler handler;

    @GetMapping("/reset/{taskId}")
    public String resetTaskCron(@PathVariable("taskId") String taskId, @RequestParam("cron") String cron) {
        if (Strings.isNullOrEmpty(taskId) || Strings.isNullOrEmpty(cron)) {
            return TaskCronConstants.RESET_FAIL.getMessage();
        }
        handler.resetTriggerTask(taskId, cron);
        return TaskCronConstants.RESET_SUCCESS.getMessage();
    }

}
