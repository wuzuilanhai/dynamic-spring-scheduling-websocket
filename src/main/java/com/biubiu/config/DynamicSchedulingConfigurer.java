package com.biubiu.config;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

/**
 * Created by zhanghaibiao on 2017/10/13.
 */
@Component
@EnableScheduling
public class DynamicSchedulingConfigurer implements SchedulingConfigurer {

    private ScheduledTaskRegistrar taskRegistrar;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    /**
     * 任务调度是否已经初始化完成
     *
     * @return
     */
    public boolean inited() {
        return this.taskRegistrar != null && this.taskRegistrar.getScheduler() != null;
    }

    public ScheduledTaskRegistrar getTaskRegistrar() {
        return taskRegistrar;
    }

}
