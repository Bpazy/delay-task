package com.github.bpazy.delaytask;

/**
 * @author ziyuan
 * created on 2020/1/14
 */
public interface DelayTaskSubmitter {

    void submit(AbstractDelayTaskRunnable delayTask);
}
