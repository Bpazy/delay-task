package com.github.bpazy.delaytask;

import java.util.List;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
public interface DelayTaskManager {
    List<DelayTask> queryDelayTasks(List<String> taskIds);

    void addDelayTask(DelayTask delayTask);

    void updateDelayTask(DelayTask delayTask);
}
