package com.github.bpazy.delaytask;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
public interface DelayTaskLoader {
    AbstractDelayTaskRunnable getInstance(String className);
}
