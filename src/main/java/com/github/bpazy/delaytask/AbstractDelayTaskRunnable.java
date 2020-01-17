package com.github.bpazy.delaytask;

import lombok.Getter;

import java.util.Date;

/**
 * @author ziyuan
 * created on 2020/1/14
 */
public abstract class AbstractDelayTaskRunnable {
    @Getter
    private String taskId;
    @Getter
    private Date runAt;
    @Getter
    private String argument;

    public AbstractDelayTaskRunnable() {

    }

    public AbstractDelayTaskRunnable(String taskId, Date runAt, String argument) {
        this.taskId = taskId;
        this.runAt = runAt;
        this.argument = argument;
    }

    public abstract void run(String argument);
}
