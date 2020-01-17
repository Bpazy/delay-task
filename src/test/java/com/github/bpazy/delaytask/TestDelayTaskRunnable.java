package com.github.bpazy.delaytask;

import com.github.bpazy.delaytask.AbstractDelayTaskRunnable;

import java.util.Date;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
public class TestDelayTaskRunnable extends AbstractDelayTaskRunnable {
    public TestDelayTaskRunnable() {
        super();
    }

    public TestDelayTaskRunnable(String taskId, Date runAt, String argument) {
        super(taskId, runAt, argument);
    }

    @Override
    public void run(String argument) {
        System.out.println(argument);
    }
}
