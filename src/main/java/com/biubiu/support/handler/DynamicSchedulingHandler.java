package com.biubiu.support.handler;

import com.biubiu.config.DynamicSchedulingConfigurer;
import com.biubiu.constant.SpringScheduleConstants;
import com.biubiu.utils.CustomBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by zhanghaibiao on 2017/10/13.
 */
@Component
public class DynamicSchedulingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicSchedulingHandler.class);

    private final String FIELD_SCHEDULED_FUTURES = SpringScheduleConstants.FIELD_SCHEDULED_FUTURES;

    private final Map<String, ScheduledTask> taskMap = new ConcurrentHashMap<>();

    private Set<ScheduledTask> scheduledTasks = null;

    @Autowired
    private DynamicSchedulingConfigurer configurer;

    private Set<ScheduledTask> getScheduledTasks() {
        if (scheduledTasks == null) {
            try {
                scheduledTasks = (Set<ScheduledTask>) CustomBeanUtils.getProperty(configurer.getTaskRegistrar(), FIELD_SCHEDULED_FUTURES);
            } catch (NoSuchFieldException e) {
                LOGGER.error(e.getMessage());
                throw new SchedulingException(e.getMessage());
            }
        }
        return scheduledTasks;
    }

    /**
     * 添加任务
     *
     * @param taskId
     * @param triggerTask
     */
    public void addTriggerTask(String taskId, TriggerTask triggerTask) {
        if (taskMap.containsKey(taskId)) {
            String message = "the taskId[".concat(taskId).concat("] was added.");
            LOGGER.error(message);
            throw new SchedulingException(message);
        }
        TaskScheduler scheduler = configurer.getTaskRegistrar().getScheduler();
        ScheduledFuture<?> future = scheduler.schedule(triggerTask.getRunnable(), triggerTask.getTrigger());
        try {
            ScheduledTask scheduledTask = null;
            Optional<Object> instance = CustomBeanUtils.newInstance(ScheduledTask.class);
            if (instance.isPresent()) {
                scheduledTask = (ScheduledTask) instance.get();
                CustomBeanUtils.setFieldValue(scheduledTask, SpringScheduleConstants.FIELD_FUTURE_NAME, future);
                getScheduledTasks().add(scheduledTask);
                taskMap.put(taskId, scheduledTask);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 取消任务
     *
     * @param taskId
     */
    public void cancelTriggerTask(String taskId) {
        ScheduledTask task = taskMap.get(taskId);
        if (task != null) {
            task.cancel();
        }
        taskMap.remove(taskId);
        getScheduledTasks().remove(task);
    }

    /**
     * 重置任务
     *
     * @param taskId
     * @param triggerTask
     */
    public void resetTriggerTask(String taskId, TriggerTask triggerTask) {
        cancelTriggerTask(taskId);
        addTriggerTask(taskId, triggerTask);
    }

    /**
     * 任务编号
     *
     * @return
     */
    public Set<String> taskIds() {
        return taskMap.keySet();
    }

    /**
     * 是否存在任务
     *
     * @param taskId
     * @return
     */
    public boolean hasTask(String taskId) {
        return this.taskMap.containsKey(taskId);
    }

    public DynamicSchedulingConfigurer getConfigurer() {
        return configurer;
    }

}
