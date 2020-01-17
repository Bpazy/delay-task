package com.github.bpazy.delaytask;

import com.github.bpazy.delaytask.DelayTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
public interface DelayTaskMapper {
    List<DelayTask> queryDelayTasks(@Param("taskIds") List<String> taskIds);

    void addDelayTask(DelayTask delayTask);

    void updateDelayTask(DelayTask delayTask);
}
